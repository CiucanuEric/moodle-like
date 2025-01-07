from peewee import Model, IntegerField, CharField
from database import db

class BaseModel(Model):
    class Meta:
        database = db

class User(BaseModel):
    id = IntegerField(primary_key=True)
    email = CharField()
    password = CharField()
    role = CharField()

    class Meta:
        db_table = "users"
