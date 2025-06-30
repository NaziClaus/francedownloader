import paramiko
from tenacity import retry, wait_fixed, stop_after_attempt
from .config import SFTP_HOST, SFTP_PORT, SFTP_USER, SFTP_PASSWORD, SFTP_KEY_PATH

class SftpClient:
    def __init__(self):
        self.transport = paramiko.Transport((SFTP_HOST, SFTP_PORT))
        if SFTP_KEY_PATH:
            private_key = paramiko.RSAKey.from_private_key_file(SFTP_KEY_PATH)
            self.transport.connect(username=SFTP_USER, pkey=private_key)
        else:
            self.transport.connect(username=SFTP_USER, password=SFTP_PASSWORD)

    @retry(stop=stop_after_attempt(3), wait=wait_fixed(30))
    def list_files(self, remote_dir):
        with paramiko.SFTPClient.from_transport(self.transport) as sftp:
            return sftp.listdir_attr(remote_dir)

    @retry(stop=stop_after_attempt(3), wait=wait_fixed(30))
    def download(self, remote_file, local_path):
        with paramiko.SFTPClient.from_transport(self.transport) as sftp:
            sftp.get(remote_file, local_path)
