import datetime
from sqlalchemy.orm import Session
from .models import FileRecord

class Registry:
    def __init__(self, db: Session):
        self.db = db

    def get_by_path(self, path: str):
        return self.db.query(FileRecord).filter_by(remote_path=path).first()

    def upsert(self, filename: str, path: str, size: int, mtime: datetime.datetime, downloaded: bool=False):
        record = self.get_by_path(path)
        if record:
            record.size = size
            record.remote_mtime = mtime
            record.downloaded = downloaded
            if downloaded:
                record.last_downloaded = datetime.datetime.utcnow()
        else:
            record = FileRecord(
                filename=filename,
                remote_path=path,
                size=size,
                remote_mtime=mtime,
                downloaded=downloaded,
                last_downloaded=datetime.datetime.utcnow() if downloaded else None
            )
            self.db.add(record)
        self.db.commit()
        return record
