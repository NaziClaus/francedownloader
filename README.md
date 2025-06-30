# SFTP Loader

This service periodically scans a remote SFTP directory and downloads new or updated `.zip` and `.rar` files to a local folder. File metadata is stored in PostgreSQL.
Each successful download is also appended to a CSV report file.

## Building (requires JDK 21)

```bash
mvn clean package
```

## Running with Docker
The provided Dockerfile uses Amazon Corretto 21 as the base JRE image.

Edit environment variables in `.env` and run:

```bash
docker-compose up --build
```

The database schema is created automatically on startup using `src/main/resources/schema.sql`. Ensure the PostgreSQL container credentials are set via environment variables.

The CSV report path can be configured via `app.csv-file` in `application.yml`.
