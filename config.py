from decouple import config

SFTP_HOST = config('SFTP_HOST')
SFTP_PORT = config('SFTP_PORT', cast=int, default=22)
SFTP_USER = config('SFTP_USER')
SFTP_PASSWORD = config('SFTP_PASSWORD', default=None)
SFTP_KEY_PATH = config('SFTP_KEY_PATH', default=None)
REMOTE_DIR = config('REMOTE_DIR', default='.')
LOCAL_DIR = config('LOCAL_DIR', default='D:/Downloads')
DATABASE_URL = config('DATABASE_URL')
LOG_FILE = config('LOG_FILE', default='D:/Downloads/Logs/sftp_loader.log')
