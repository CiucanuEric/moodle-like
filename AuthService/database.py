from peewee import MySQLDatabase

DATABASE="idm"
USER="root"
PASSWORD="admin"
HOST="localhost"
PORT=3306


db = MySQLDatabase(database=DATABASE,user=USER,password=PASSWORD,host=HOST,port=PORT)
