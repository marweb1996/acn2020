import qrcode
from Crypto.Random import get_random_bytes
import uuid
from base64 import b64encode
import socket
import time
import requests
from Crypto.Cipher import AES
import os
from Crypto.Util.Padding import pad, unpad


def generate_qr():
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )

    key = get_random_bytes(16)
    clientId = uuid.uuid4()
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    IPAddr = s.getsockname()[0]
    s.close()

    qr_code = str(clientId) + "&&&" + b64encode(key).decode() + "&&&" + IPAddr
    print(qr_code)

    qr.add_data(qr_code)
    qr.make(fit=True)

    img = qr.make_image(fill_color="black", back_color="white")

    img.show()

    return clientId, b64encode(key).decode()


def decrypt_message(cipher_text, key):
    # cipher = AES.new(key, AES.MODE_CBC)
    # plaintext = cipher.decrypt(encrypted_message)
    # print(plaintext)
    cipher = AES.new(key.encode("utf8"), AES.MODE_ECB)
    print(cipher_text)
    print(cipher_text.encode("utf8"))
    print(pad(cipher_text.encode("utf8"), 16))
    print(cipher_text.decode("hex"))
    message = cipher.decrypt(pad(cipher_text.encode("utf8"), 16))
    print(b64encode(message).decode())
    #text = AESCrypto(key).decrypt(cipher_text)
    #print(text)


def main():
    print("Starting python client")
    clientId, key = generate_qr()
    while True:
        response = requests.get(f"http://192.168.8.100:3000/getMessages?clientId={clientId}")
        data = response.json()
        if data["status"] == 200 and len(data["messages"]) > 0:
            for m in data["messages"]:
                decrypt_message(m, key)
        print(data)
        time.sleep(1)


if __name__ == "__main__":
    main()