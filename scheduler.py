from apscheduler.schedulers.blocking import BlockingScheduler
from .loader import delta_scan, initial_scan
from .db import SessionLocal
from .models import FileRecord

sched = BlockingScheduler()

@sched.scheduled_job('interval', minutes=5)
def scheduled_delta():
    delta_scan()


def maybe_initial_scan():
    session = SessionLocal()
    try:
        has_records = session.query(FileRecord).first() is not None
    finally:
        session.close()
    if not has_records:
        initial_scan()


def start():
    maybe_initial_scan()
    sched.start()
