from uuid import uuid5, uuid4

from jwt import ExpiredSignatureError, InvalidIssuerError

from model import User
from datetime import datetime, timezone, timedelta
from database import db
import jwt

from concurrent import futures
import logging
import uuid

import grpc
import auth_pb2
import auth_pb2_grpc


MY_URL="http://localhost:50051"

key = "secret"



class AuthService(auth_pb2_grpc.AuthServicer):
    def Login(self, request, context):
        users=User.select().where(request.username == User.email).where(request.password == User.password)
        print(request.username + "-" + request.password)
        if not len(users):
            return auth_pb2.LoginResponse(message="incorrect")
        else:
            current_time=datetime.now(tz=timezone.utc)
            uuid=str(uuid4())
            print(uuid)
            token=jwt.encode({"iss":MY_URL,"sub":users[0].email,"exp":current_time + timedelta(minutes=30),"jit":uuid,"role":users[0].role},key,algorithm="HS256")
            return auth_pb2.LoginResponse(message=token)
    def Validate(self, request, context):
        encrypted_token=request.token
        print(encrypted_token)
        sub="none"
        try:
            json_token=jwt.decode(encrypted_token,key,issuer=MY_URL, algorithms="HS256")
            role=json_token["role"]
            sub=json_token["sub"]
        except ExpiredSignatureError:
            role="expired"
        except InvalidIssuerError:
            role="wrong_issuer"
        return auth_pb2.TokenResponse(role=role,sub=sub)


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
