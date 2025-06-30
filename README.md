# SFTP Data Loader

A simple service that periodically scans a remote SFTP directory and downloads new or updated `.zip` and `.rar` files. Metadata about processed files is stored in PostgreSQL.

## Requirements
- Python 3.9+
- PostgreSQL 14+

Install dependencies manually (optional):
```bash
pip install -r requirements.txt
```

Copy `.env.example` to `.env` and edit connection parameters. The same file is
used by `docker-compose`.

Run the service:
```bash
python -m francedownloader.main
```

## Docker

You can run the loader together with PostgreSQL using Docker Compose.

Build and start the containers:

```bash
docker-compose up --build
```

Logs and downloaded files will be placed in the `data/` directory on the host. The service creates the log directory automatically if needed.
