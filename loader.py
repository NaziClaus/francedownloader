import os
import datetime
import logging
from typing import Iterable

from .config import REMOTE_DIR, LOCAL_DIR, LOG_FILE
from .db import SessionLocal
from .sftp_client import SftpClient
from .registry import Registry
from .models import FileRecord

logging.basicConfig(
    filename=LOG_FILE,
    level=logging.INFO,
    format='%(asctime)s %(levelname)s %(message)s'
)

sftp_client = SftpClient()


def filter_files(files: Iterable):
    for f in files:
        if f.filename.lower().endswith(('.zip', '.rar')):
            yield f


def initial_scan():
    session = SessionLocal()
    registry = Registry(session)
    try:
        files = sftp_client.list_files(REMOTE_DIR)
        for fileattr in filter_files(files):
            remote_path = os.path.join(REMOTE_DIR, fileattr.filename)
            registry.upsert(
                filename=fileattr.filename,
                path=remote_path,
                size=fileattr.st_size,
                mtime=datetime.datetime.fromtimestamp(fileattr.st_mtime)
            )
        logging.info('Initial scan completed.')
    except Exception as e:
        logging.error('Initial scan failed: %s', e)
        raise
    finally:
        session.close()


def delta_scan():
    session = SessionLocal()
    registry = Registry(session)
    try:
        files = sftp_client.list_files(REMOTE_DIR)
        for fileattr in filter_files(files):
            remote_path = os.path.join(REMOTE_DIR, fileattr.filename)
            mtime = datetime.datetime.fromtimestamp(fileattr.st_mtime)
            record = registry.get_by_path(remote_path)
            if not record or mtime > record.remote_mtime:
                temp_path = os.path.join(LOCAL_DIR, fileattr.filename + '.part')
                final_path = os.path.join(LOCAL_DIR, fileattr.filename)
                sftp_client.download(remote_path, temp_path)
                os.replace(temp_path, final_path)
                registry.upsert(
                    filename=fileattr.filename,
                    path=remote_path,
                    size=fileattr.st_size,
                    mtime=mtime,
                    downloaded=True
                )
                logging.info('Downloaded %s', fileattr.filename)
    except Exception as e:
        logging.error('Delta scan failed: %s', e)
        raise
    finally:
        session.close()
