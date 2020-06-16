# Android: Notifier (Server)
> Notifications from a mobile phone on a computer with end-to-end encryption

This project demonstrates an indirect connection (= using a web server) to a target while ensuring that all transfered data is encrypted. In order to show how exactly end-to-end encryption works and why the server only plays a secondary role, authentication was omitted in this example.


## Installation

NodeJS Server:

```sh
cd chatServerNode
npm install
npm start
```

Android App:

*   Open Android Studio
*   Replace the Server IP in the Main Activity with your server's IP address
*   Compile and run the application on your test device

Python Client:

```sh
cd chatAppPython
virtualenv -p python3 venv
pip install -r requirements.txt
python main.py
```

## Usage example

To test the project you just have to start the NodeJS server, the Android app and the Python client, scan the generated QR code with the app and then you can send encrypted messages to the client.

## Creators

Kristof Krenn (kristof.krenn@student.tugraz.at)

Markus Weber (markus.weber@student.tugraz.at)