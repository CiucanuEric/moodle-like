import os

from peewee import MySQLDatabase

DATABASE=os.getenv("DB_NAME")
USER=os.getenv("DB_USER")
PASSWORD=os.getenv("DB_PASSWORD")
HOST=os.getenv("DB_HOST")
PORT=int(os.getenv("DB_PORT"))


db = MySQLDatabase(database=DATABASE,user=USER,password=PASSWORD,host=HOST,port=PORT)
