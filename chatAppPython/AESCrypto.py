import base64
from Crypto.Cipher import AES

BS = 16
pad = lambda s: s + (BS - len(s) % BS) * chr(BS - len(s) % BS)
unpad = lambda s: s[0:-ord(s[-1])]


class AESCrypto:
    def __init__(self, key):
        self.key = key

    def encrypt(self, raw):
        raw = pad(raw)
        cipher = AES.new(self.key, AES.MODE_ECB)
        raw = cipher.encrypt(raw)
        encrypt_val = base64.b64encode(raw)
        return encrypt_val

    def decrypt(self, raw):
        raw = raw.decode('base64')
        cipher = AES.new(self.key, AES.MODE_ECB)
        raw = cipher.decrypt(raw)
        raw = unpad(raw)
        return raw
