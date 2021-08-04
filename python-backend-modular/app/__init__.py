from flask import Flask, render_template, request, redirect, url_for, session
from flask_mysqldb import MySQL
import MySQLdb.cursors
import re
from configparser import ConfigParser

from flask import Flask, render_template, request, redirect, url_for, session
from flask_mysqldb import MySQL
import MySQLdb.cursors
import re
from flask_cors import CORS, cross_origin
from flask import jsonify
import json
from functools import wraps
import jwt
from datetime import datetime, timedelta

app = Flask(__name__)
CORS(app)
configur = ConfigParser()
path=r'config.ini'	
configur.read(path)
app = Flask(__name__)

# Change this to your secret key (can be anything, it's for extra protection)
app.secret_key = '12345'
'''
# Enter your database connection details below
app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root1'
app.config['MYSQL_PASSWORD'] = 'mayuri9011'
app.config['MYSQL_DB'] = 'ideaportal'

'''
app.config['MYSQL_HOST'] = configur.get('sql','MYSQL_HOST')
app.config['MYSQL_USER'] = configur.get('sql','MYSQL_USER')
app.config['MYSQL_PASSWORD'] = configur.get('sql','MYSQL_PASSWORD')
app.config['MYSQL_DB'] = configur.get('sql','MYSQL_DB')
app.config['MYSQL_PORT'] = configur.getint('sql','MYSQL_PORT')
mysql = MySQL(app)

app.run()

