import socket
import threading
import numpy as np
import cv2

from sabr_host.target_info import TargetInfo
from sabr_host.connection_utilities import *


target_info = TargetInfo(capture_device=-1)


def receive_image(client_socket):
    # Read image length
    image_length = receive_uint(client_socket)

    # Read image bytes
    image_bytes = np.frombuffer(receive_bytes(client_socket, image_length), dtype="byte")

    # Decode bytes to image
    img_np = cv2.imdecode(image_bytes, cv2.IMREAD_COLOR)

    return img_np


def client_handler(client_socket):
    while True:
        targets, width = target_info.get_targets(receive_image(client_socket))

        # Send meta information
        send_short(client_socket, width)
        send_short(client_socket, len(targets))

        # Send target dimensions
        for target in targets:
            send_short(client_socket, target.x_min)
            send_short(client_socket, target.y_min)
            send_short(client_socket, target.width)
            send_short(client_socket, target.height)


class Server:
    def __init__(self, port):
        self.port = port
        self.server_socket = None

    def listen(self):
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # Bind to all interfaces on specified port
        self.server_socket.bind(('', self.port))
        self.server_socket.listen(16)

        print(f"Listening on port {self.port}")

        # Accept incoming connections
        while True:
            (client, address) = self.server_socket.accept()

            client_thread = threading.Thread(target=client_handler, args=[client])
            client_thread.start()
