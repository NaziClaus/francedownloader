from .scheduler import start
from .db import init_db

if __name__ == '__main__':
    init_db()
    start()
