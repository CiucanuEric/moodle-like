from model import User
from database import db

from concurrent import futures
import logging

import grpc
import auth_pb2
import auth_pb2_grpc


class AuthService(auth_pb2_grpc.AuthServicer):
    def Login(self, request, context):
        users=User.select().where(request.username == User.email).where(request.password == User.password)
        print(users[0])
        if not len(users):
            return auth_pb2.LoginResponse(message="No users with this name")
        else:
            return auth_pb2.LoginResponse(message="Username found!")

def serve():
    port="50051"
    server=grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    auth_pb2_grpc.add_AuthServicer_to_server(AuthService(),server)
    server.add_insecure_port("[::]:" +port)
    server.start()
    print("Server started, listening on " + port)
    server.wait_for_termination()

if __name__ == "__main__":
    logging.basicConfig()
    db.connect()
    serve()
    print("No error")
