# SFTP Loader

This service periodically scans a remote SFTP directory and downloads new or updated `.zip` and `.rar` files to a local folder. File metadata is stored in PostgreSQL.

## Building

```bash
mvn clean package
```

## Running with Docker

Edit environment variables in `.env` and run:

```bash
docker-compose up --build
```
