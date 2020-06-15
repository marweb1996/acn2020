import socket
import time
import uuid
import qrcode
import requests

from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
from Crypto.Util.Padding import unpad

from base64 import b64encode, b64decode


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
    host_name = socket.gethostname()
    qr_code = str(clientId) + "&&&" + b64encode(key).decode() + "&&&" + IPAddr + "&&&" + host_name
    qr.add_data(qr_code)
    qr.make(fit=True)

    img = qr.make_image(fill_color="black", back_color="white")

    img.show()

    return clientId, key


def decrypt_message(cipher_text, key):
    ct_encoded = b64decode(cipher_text)
    iv = ct_encoded[:16]
    ct = ct_encoded[16:]
    cipher = AES.new(key, AES.MODE_CBC, iv)
    message = unpad(cipher.decrypt(ct), AES.block_size)
    return message


def main():
    print("Starting python client..")
    print('-' * 50 + "\n\n")
    clientId, key = generate_qr()
    running = True
    while running:
        response = requests.get(f"http://192.168.8.100:3000/getMessages?clientId={clientId}")
        data = response.json()
        if data["status"] == 200 and len(data["messages"]) > 0:
            for m in data["messages"]:
                message = decrypt_message(m["message"], key)
                if message.decode("utf-8").startswith("pair"):
                    print("*" * 20 + " CONNECTED TO " + message.decode("utf-8").split(":")[1] + " " + "*" * 20)
                elif message.decode("utf-8").startswith("unpair"):
                    print("*" * 20 + " DISCONNECTED FROM " + message.decode("utf-8").split(":")[1] + " " + "*" * 20)
                    running = False
                    break
                else:
                    print("*" * 20 + " YOU GOT A NEW MESSAGE " + "*" * 20)
                    print("Message: " + str(message.decode("utf-8")))
                    print("Date: " + str(m["timestamp"]))
                print("\n")
        time.sleep(0.1)


if __name__ == "__main__":
    main()