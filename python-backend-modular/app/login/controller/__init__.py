'''
from flask import Flask
from flask_mysqldb import MySQL


app = Flask(__name__)

# Change this to your secret key (can be anything, it's for extra protection)
app.secret_key = '12345'

# Enter your database connection details below
app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root1'
app.config['MYSQL_PASSWORD'] = 'mayuri9011'
app.config['MYSQL_DB'] = 'ideaportal'

from controller import login
'''