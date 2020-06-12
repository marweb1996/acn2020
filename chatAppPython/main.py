import qrcode
from Crypto.Random import get_random_bytes
import uuid
from base64 import b64encode


qr = qrcode.QRCode(
    version=1,
    error_correction=qrcode.constants.ERROR_CORRECT_L,
    box_size=10,
    border=4,
)

key = get_random_bytes(16)
clientId = uuid.uuid4()

qr_code = str(clientId) + "&&&" + b64encode(key).decode()
print(qr_code)

qr.add_data(qr_code)
qr.make(fit=True)

img = qr.make_image(fill_color="black", back_color="white")

img.show()