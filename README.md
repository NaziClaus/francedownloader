# SFTP Data Loader

A simple service that periodically scans a remote SFTP directory and downloads new or updated `.zip` and `.rar` files. Metadata about processed files is stored in PostgreSQL.

## Requirements
- Python 3.9+
- PostgreSQL 14+

Install dependencies:
```bash
pip install -r requirements.txt
```

Copy `.env.example` to `.env` and edit connection parameters.

Run the service:
```bash
python -m francedownloader.main
```
