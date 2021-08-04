from app import mysql
from flask_mysqldb import MySQL
import MySQLdb.cursors
# http://localhost:5000/pythonlogin/ - this will be the login page, we need to use both GET and POST requests

def cur_defination():
    return mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        
def cur_commit():
	mysql.connection.commit()