import datetime
from sqlalchemy import Column, Integer, String, Boolean, BigInteger, DateTime
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class FileRecord(Base):
    __tablename__ = 'file_registry'

    id = Column(Integer, primary_key=True)
    filename = Column(String, nullable=False)
    remote_path = Column(String, unique=True, nullable=False)
    size = Column(BigInteger, nullable=False)
    remote_mtime = Column(DateTime(timezone=True), nullable=False)
    downloaded = Column(Boolean, default=False)
    last_downloaded = Column(DateTime(timezone=True))
    created_at = Column(DateTime(timezone=True), default=datetime.datetime.utcnow)
    updated_at = Column(DateTime(timezone=True), default=datetime.datetime.utcnow, onupdate=datetime.datetime.utcnow)
