'''
from flask import Flask, render_template, request, redirect, url_for, session
from flask_mysqldb import MySQL
import MySQLdb.cursors
import re
from flask_cors import CORS, cross_origin
from flask import jsonify
import json

app = Flask(__name__)
CORS(app)

# Change this to your secret key (can be anything, it's for extra protection)
app.secret_key = 'your secret key'

# Enter your database connection details below
app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root'
app.config['MYSQL_PASSWORD'] = 'password'
app.config['MYSQL_DB'] = 'backend'

# Intialize MySQL
mysql = MySQL(app)

# http://localhost:5000/pythonlogin/ - this will be the login page, we need to use both GET and POST requests
@app.route('/pythonlogin/', methods=['GET', 'POST'])
def login():
    # Output message if something goes wrong...
    msg = ''
    # Check if "username" and "password" POST requests exist (user submitted form)
    if request.method == 'POST' and 'username' in request.form and 'password' in request.form:
        # Create variables for easy access
        username = request.form['username']
        password = request.form['password']
        # Check if account exists using MySQL
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT * FROM users WHERE userName = %s AND userpassword = %s', (username, password,))
        # Fetch one record and return result
        account = cursor.fetchone()
        # If account exists in accounts table in out database
        if account:
            # Create session data, we can access this data in other routes
            session['loggedin'] = True
            session['id'] = account['user_id']
            session['username'] = account['userName']
            # Redirect to home page
            return redirect(url_for('home'))
        else:
            # Account doesnt exist or username/password incorrect
            msg = 'Incorrect username/password!'
    # Show the login form with message (if any)
    return render_template('index.html', msg=msg)

# http://localhost:5000/python/logout - this will be the logout page
@app.route('/pythonlogin/logout')
def logout():
    # Remove session data, this will log the user out
   session.pop('loggedin', None)
   session.pop('id', None)
   session.pop('username', None)
   # Redirect to login page
   return redirect(url_for('login'))


# http://localhost:5000/pythinlogin/register - this will be the registration page, we need to use both GET and POST requests
@app.route('/pythonlogin/register', methods=['GET', 'POST'])
def register():
    # Output message if something goes wrong...
    msg = ''
    # Check if "username", "password" and "email" POST requests exist (user submitted form)
    if request.method == 'POST' and 'username' in request.form and 'password' in request.form and 'email' in request.form:
        # Create variables for easy access
        username = request.form['username']
        password = request.form['password']
        email = request.form['email']
        roleid = 1
        company = ""
		        # Check if account exists using MySQL
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT * FROM users WHERE userName = %s', (username,))
        account = cursor.fetchone()
        # If account exists show error and validation checks
        if account:
            msg = 'Account already exists!'
        elif not re.match(r'[^@]+@[^@]+\.[^@]+', email):
            msg = 'Invalid email address!'
        elif not re.match(r'[A-Za-z0-9]+', username):
            msg = 'Username must contain only characters and numbers!'
        elif not username or not password or not email:
            msg = 'Please fill out the form!'
        else:
            # Account doesnt exists and the form data is valid, now insert new account into accounts table
            cursor.execute('INSERT INTO users VALUES (NULL, %s, %s, %s, %s, %s)', (password, roleid, username, email, company))
            mysql.connection.commit()
            msg = 'You have successfully registered!'
    elif request.method == 'POST':
        # Form is empty... (no POST data)
        msg = 'Please fill out the form!'
    # Show registration form with message (if any)
    return render_template('register.html', msg=msg)


# http://localhost:5000/pythinlogin/home - this will be the home page, only accessible for loggedin users
@app.route('/pythonlogin/home')
def home():
    # Check if user is loggedin
    if 'loggedin' in session:
        # User is loggedin show them the home page
        return render_template('home.html', username=session['username'])
    # User is not loggedin redirect to login page
    return redirect(url_for('login'))

# http://localhost:5000/pythinlogin/profile - this will be the profile page, only accessible for loggedin users
@app.route('/pythonlogin/profile')
def profile():
    # Check if user is loggedin
    if 'loggedin' in session:
        # We need all the account info for the user so we can display it on the profile page
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT * FROM users WHERE user_id = %s', (session['id'],))
        account = cursor.fetchone()
        # Show the profile page with account info
        return render_template('profile.html', account=account)
    # User is not loggedin redirect to login page
    return redirect(url_for('login'))
'''
from flask import Flask, render_template, request, redirect, url_for, session
from flask_mysqldb import MySQL
import MySQLdb.cursors
import re
import sharepoint
from flask_cors import CORS, cross_origin
from flask import jsonify
import json
from functools import wraps
import jwt
from datetime import datetime, timedelta
app = Flask(__name__)
CORS(app)

# Change this to your secret key (can be anything, it's for extra protection)
app.secret_key = '12345'

# Enter your database connection details below
app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root'
app.config['MYSQL_PASSWORD'] = 'rootPassword'
app.config['MYSQL_DB'] = 'ideaportal'
#app.config['PROPAGATE_EXCEPTIONS']='true'

# Intialize MySQL
mysql = MySQL(app)


# decorator for verifying the JWT
def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = None
        # jwt is passed in the request header
        if 'x-access-token' in request.headers:
            token = request.headers['x-access-token']
        # return 401 if token is not passed
        if not token:
            return jsonify({'message' : 'Token is missing !!'})
   
        try:
            # decoding the payload to fetch the stored details
            data = jwt.decode(token,app.secret_key)
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            cursor.execute('SELECT userName FROM users WHERE userName = %s', (data['user_id'],))
            result_set= cursor.fetchall()
            for row in result_set:     
                current_user=row["userName"]
        except Exception as e:
            print(e)
            return jsonify({
                'message' : 'Token is invalid !!'
            })
        # returns the current logged in users contex to the routes
        return  f(current_user, *args, **kwargs)
   
    return decorated

# http://localhost:5000/pythonlogin/ - this will be the login page, we need to use both GET and POST requests
@app.route('/login', methods=['GET', 'POST'])
def login():
    # Output message if something goes wrong...
    msg = ''
    # Check if "username" and "password" POST requests exist (user submitted form)
    if request.method == 'POST': #and 'username' in request.form and 'password' in request.form:
        # Create variables for easy access
        data = json.loads(request.data.decode())
        username = data["user"]
        password = data["password"]

        # Check if account exists using MySQL
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT * FROM users WHERE userName = %s AND userpassword = %s ', (username, password))
        # Fetch one record and return result
        account = cursor.fetchone()
        # If account exists in accounts table in out database
        if account:
            # Create session data, we can access this data in other routes
            session['loggedin'] = True
            session['user_id'] = account['user_id']
            session['userName'] = account['userName']
            session['accessToken'] = sharepoint.accessToken()
            session['accessToken'] = eval(session['accessToken'])
            session['accessToken'] = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Im5PbzNaRHJPRFhFSzFqS1doWHNsSFJfS1hFZyIsImtpZCI6Im5PbzNaRHJPRFhFSzFqS1doWHNsSFJfS1hFZyJ9.eyJhdWQiOiIwMDAwMDAwMy0wMDAwLTBmZjEtY2UwMC0wMDAwMDAwMDAwMDAvdGVhbWNvZGVyZWQuc2hhcmVwb2ludC5jb21ANDlmYWExNzQtYmZiYS00Y2I2LWIwZmItZDQ1ZDY1ZmEzYzI5IiwiaXNzIjoiMDAwMDAwMDEtMDAwMC0wMDAwLWMwMDAtMDAwMDAwMDAwMDAwQDQ5ZmFhMTc0LWJmYmEtNGNiNi1iMGZiLWQ0NWQ2NWZhM2MyOSIsImlhdCI6MTYyMTMzOTY0NywibmJmIjoxNjIxMzM5NjQ3LCJleHAiOjE2MjE0MjYzNDcsImlkZW50aXR5cHJvdmlkZXIiOiIwMDAwMDAwMS0wMDAwLTAwMDAtYzAwMC0wMDAwMDAwMDAwMDBANDlmYWExNzQtYmZiYS00Y2I2LWIwZmItZDQ1ZDY1ZmEzYzI5IiwibmFtZWlkIjoiNjBkNDcyZjktM2FlMS00MGUxLWFkZWQtZjYwM2Y3MzJjZjg2QDQ5ZmFhMTc0LWJmYmEtNGNiNi1iMGZiLWQ0NWQ2NWZhM2MyOSIsIm9pZCI6IjE5Zjc4MjI0LWNjZjYtNGVlMC04ZTJmLWExOWM1ZGM0YTM5ZCIsInN1YiI6IjE5Zjc4MjI0LWNjZjYtNGVlMC04ZTJmLWExOWM1ZGM0YTM5ZCIsInRydXN0ZWRmb3JkZWxlZ2F0aW9uIjoiZmFsc2UifQ.XjXgL9GTExqF2x3hi0uXTUGCaujNaslIqDDHc43t4uwI7STauR1VcYMFv33hnGK27HawkLwT17_eUMDpNVv4Uzi2TpX5NZHKw_gJrokd6nfFi6nW8iK2KLBIkWVTCMMdw_UqSwQI4fFVlhED8hkVHagGgk33mDf69ISJzvf_QXhQltZptB3ngLlAsTveFv3lqQQ47mmJ4xgYfRep1FniQOvHNcBI9nMM2dGenDxw2zbPi6E6pJ0vDY6hz_AMMU8ZIFx6GigCnE8V-OlE0VJsjkour_8-tEssAZt3Oiu4jPGGfRaYmC2rE-CVaVixNjgZdswmx6s-FMMpA82j99gxkw'
            print(session['accessToken'])

            #generating jwt access token
            payload = {"user_id": username,'exp' : datetime.utcnow() + timedelta(minutes = 30)}
            JwtToken = jwt.encode(payload,app.secret_key)
   
            return jsonify({'token' :JwtToken.decode("UTF-8"),'result':'success'})
        
        else:
            # Account doesnt exist or username/password incorrect
            msg = 'Incorrect username/password!'
    # Show the login form with message (if any)
    return jsonify({'code':'404','result':'failed','msg':msg})

# http://localhost:5000/chekLogin
@app.route('/checkLogin')
@token_required
def checkLogin(user='default'):
    return jsonify({'result':'success','user':user})

# http://localhost:5000/update
@app.route('/LogoutCheck')
@token_required
def LogoutCheck(user='default'):
    return jsonify({'result':'success','user':user})
# http://localhost:5000/python/logout - this will be the logout page
@app.route('/pythonlogin/logout')
def logout():
    # Remove session data, this will log the user out
   session.pop('loggedin', None)
   session.pop('user_id', None)
   session.pop('userName', None)
   session.pop('accessToken', None)
   # Redirect to login page
   return jsonify({'result':'success'})


# http://localhost:5000/pythinlogin/register - this will be the registration page, we need to use both GET and POST requests
@app.route('/register', methods=['GET', 'POST'])
def register():
    # Output message if something goes wrong...
    msg = ''
    # Check if "username", "password" and "email" POST requests exist (user submitted form)
    if request.method == 'POST' :#and 'username' in request.form and 'password' in request.form and 'companyname' in request.form and 'email' in request.form and 'role' in request.form:
        # Create variables for easy access
        data = json.loads(request.data.decode())
        username = data["userName"]
        password = data["password"]
        companyname=data['companyname']
        email = data['userEmail']
        role=data['role']

         # Check if account exists using MySQL
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT * FROM users WHERE userName = %s', (username,))
        account = cursor.fetchone()
        # If account exists show error and validation checks
        if account:
            msg = 'Account already exists!'
        elif not re.match(r'[^@]+@[^@]+\.[^@]+', email):
            msg = 'Invalid email address!'
        elif not re.match(r'[A-Za-z0-9]+', username):
            msg = 'Username must contain only characters and numbers!'
        elif not username or not password or not email or not role:
            msg = 'Please fill out the form!'
        else:
            # Account doesnt exists and the form data is valid, now insert new account into accounts table
            if(role=='Client Partner'):
                cursor.execute('INSERT INTO users VALUES (NULL, %s, %s,%s, %s,%s)', (password,'001',username, email,companyname,))
                msg = 'You have successfully registered!'
            elif(role=='Project Manager'):
                cursor.execute('INSERT INTO users VALUES (NULL, %s, %s,%s, %s,%s)', (password,'002',username, email,'Persistent',))
                msg = 'You have successfully registered!'
            elif(role=='Employee'):
                cursor.execute('INSERT INTO users VALUES (NULL, %s, %s,%s, %s,%s)', (password,'003',username, email,'Persistent',))
                msg = 'You have successfully registered!'    
            mysql.connection.commit()
            
    elif request.method == 'POST':
        # Form is empty... (no POST data)
        msg = 'Please fill out the form!'
    # Show registration form with message (if any)
    return jsonify({'result':msg})


# http://localhost:5000/pythinlogin/home - this will be the home page, only accessible for loggedin users
@app.route('/pythonlogin/home')
def home():
    # Check if user is loggedin
    if 'loggedin' in session:
        # User is loggedin show them the home page
        return render_template('home.html', userName=session['userName'])
    # User is not loggedin redirect to login page
    return redirect(url_for('login'))

@app.route('/pythonlogin/profile')
def profile():
    # Check if user is loggedin
    if 'loggedin' in session:
        # We need all the account info for the user so we can display it on the profile page
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT * FROM users WHERE user_id = %s', (session['user_id'],))
        account = cursor.fetchone()
        # Show the profile page with account info
        return jsonify({'result':'success', 'account': account})
    # User is not loggedin redirect to login page
    return jsonify({'result':'failed'})

# http://localhost:5000/pythonlogin/profile - this will be the profile page, only accessible for loggedin users
@app.route('/pythonlogin/viewtheme')
def viewtheme():
    # Check if user is loggedin
    if 'loggedin' in session:
        # We need all the account info for the user so we can display it on the profile page
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT * FROM themes')
        data = cursor.fetchall() #data from database 
        return jsonify({'result':'success', 'data': data})
    # User is not loggedin redirect to login page
    return jsonify({'result':'failed'})

@app.route('/pythonlogin/viewidea', methods=['GET', 'POST'])
def viewidea():
   
    # Check if user is loggedin
    if request.method == "POST":
        t_id= request.form['t_id']
        # search by author or book
        if request.form.get('action1') == 'value1':
       
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            cursor.execute("SELECT t.themename,i.ideadescription,u.userName,iu.idea_URL FROM ideas i join themes t on i.theme_id=t.theme_id join users u on i.user_id=u.user_id  join idea_upload iu on i.i_upload_id=iu.i_upload_id WHERE i.theme_id LIKE %s ", [t_id])
            #conn.commit()
            data = cursor.fetchall()
            
        return jsonify({'result':'success', 'data': data})
    
    return jsonify({'result':'failed'})

@app.route('/pythonlogin/viewonetheme', methods=['GET', 'POST'])
def viewonetheme():
    
    if request.method == "POST":
        t_id= request.form['t_id']
        # search by author or book
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute("SELECT t.themename,t.themedescription,u.userName FROM themes t join users u on t.user_id=u.user_id WHERE t.theme_id LIKE %s ", [t_id])
        #conn.commit()
        data = cursor.fetchall()
        return jsonify({'result':'success', 'data': data})
    return jsonify({'result':'failed'})

@app.route('/pythonlogin/viewoneidea', methods=['GET', 'POST'])
def viewoneidea():
    
    if request.method == "POST":
        i_id= request.form['i_id']
        # search by author or book
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute("SELECT t.themename,i.ideadescription,u.userName,iu.idea_URL FROM ideas i join themes t on i.theme_id=t.theme_id join users u on i.user_id=u.user_id  join idea_upload iu on i.i_upload_id=iu.i_upload_id WHERE i.idea_id LIKE %s ", [i_id])
        #conn.commit()
        data = cursor.fetchall()
        return jsonify({'result':'success', 'data': data})
    return jsonify({'result':'failed'})

@app.route('/pythonlogin/commentidea', methods=['GET', 'POST'])
def commentidea():

    return render_template('commentidea.html')

@app.route('/pythonlogin/commentonidea', methods=['GET', 'POST'])
@token_required
def commentonidea():
    msg=''
    if request.method == 'POST' and 'comment' in request.form :
        comment= request.form['comment']
        i_id= request.form['i_id']
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('INSERT INTO comments VALUES (NULL, %s, %s,%s)', (comment,i_id, session['user_id'],))
        mysql.connection.commit()
        msg='comment successful'
        return jsonify({'result':'success'})
    return jsonify({'result':'failed'})

@app.route('/pythonlogin/likedislike', methods=['GET', 'POST'])
def likedislike():
    msg=''
    if request.method == 'POST':
        i_id= request.form['i_id']
        if request.form.get('action1') == 'value1':
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            cursor.execute('INSERT INTO likes VALUES (NULL, %s, %s)', (i_id,session['user_id'],))
            mysql.connection.commit()
            msg='like successful'
            return jsonify({'result':'success'})
        elif request.form.get('action2') == 'value2':
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            cursor.execute('INSERT INTO dislikes VALUES (NULL, %s, %s)', (i_id,session['user_id'],))
            mysql.connection.commit()
            msg='dislike successful'
            return jsonify({'result':'success'})
    return jsonify({'result':'failed'})

@app.route('/pythonlogin/viewcomment', methods=['GET', 'POST'])
def viewcomment():
    if 'loggedin' in session:
        # We need all the account info for the user so we can display it on the profile page
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT c.commentvalue,i.ideadescription,u.userName FROM comments c join ideas i on c.idea_id=i.idea_id join users u on c.user_id=u.user_id')
        data = cursor.fetchall() #data from database 
        return jsonify({'result':'success', 'data': data})
    # User is not loggedin redirect to login page
    return jsonify({'result':'failed'}) 

@app.route('/pythonlogin/countlikedislike', methods=['GET', 'POST'])
def countlikedislike():
    if request.method == 'POST':
        i_id= request.form['i_id']
        if request.form.get('action1') == 'value1':
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            cursor.execute('Select count(l.like_id) as c_l FROM likes l  where l.idea_id like %s',[i_id])
            data = cursor.fetchall()
            cursor.execute('Select count(d.dislike_id) as c_d FROM dislikes d  where d.idea_id like %s',[i_id])
            data1 = cursor.fetchall()
        return jsonify({'result':'success', 'like': data, 'dislike': data1})
    return jsonify({'result':'failed'}) 

@app.route('/pythonlogin/createtheme', methods=['GET', 'POST'])
@token_required
def createtheme():
    if request.method == "POST":
        if 'loggedin' in session:
            name = request.form['themename']
            description = request.form['description']
            f = request.files['file']
            # print(f.filename)
            sharepoint.createTheme(f)
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            url = "https://teamcodered.sharepoint.com/sites/SEProductions/trial_1/folderB/"
            url = url + f.filename
            # print(url)
            cursor.execute('INSERT INTO theme_upload VALUES (NULL, %s)', (url,))
            cursor.execute('SELECT t_upload_id FROM theme_upload ORDER BY t_upload_id DESC LIMIT 1')
            account = cursor.fetchone()
            u_id = account['t_upload_id']
            print(account['t_upload_id'])
            mysql.connection.commit()
            # u_id = cursor.execute('SELECT t_upload_id from theme_upload where theme_URL like %s' , (url,))
            
            cursor.execute('INSERT INTO themes VALUES (NULL, %s, %s,%s, %s)', (name,description,u_id, session['user_id'],))
            mysql.connection.commit()
            # print(f)
            print()
            return render_template('createtheme.html')
    return render_template('createtheme.html')

@app.route('/pythonlogin/createidea', methods=['GET', 'POST'])
def createidea():
    if request.method == "POST":
        if 'loggedin' in session:
            theme_id = request.form['theme_id']
            description = request.form['description']
            f = request.files['file']
            sharepoint.createTheme(f)
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            url = "https://teamcodered.sharepoint.com/sites/SEProductions/trial_1/folderB/"
            url = url + f.filename
            cursor.execute('INSERT INTO idea_upload VALUES (NULL, %s)', (url,))
            cursor.execute('SELECT i_upload_id FROM idea_upload ORDER BY i_upload_id DESC LIMIT 1')
            account = cursor.fetchone()
            u_id = account['i_upload_id']
            print(account['i_upload_id'])
            mysql.connection.commit()
            cursor.execute('INSERT INTO ideas VALUES (NULL, %s, %s,%s, %s)', (description,session['user_id'],theme_id,u_id,))
            mysql.connection.commit()
            return('Inserted' + str(url))
        return()

@app.route('/updatecompany', methods=['GET', 'POST'])
@token_required
def updatecompany(user="default"):
    msg=''
    msg1=''
    r='001'
    if request.method == 'POST':
        data = json.loads(request.data.decode())
        company= data['company']
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('Select roleId from users where userName=%s ',[user])
        result_set = cursor.fetchall()
        for row in result_set:
            msg1=row["roleId"]
        
        if msg1 != '001':
            msg='cannot change company name'
        else:
            cursor.execute('Update users set userCompany=%s where userName=%s and roleId=%s',(company,user,'001'))
            mysql.connection.commit()
            msg='successful'
        return jsonify({'result':msg})

    return jsonify({'result':msg})

@app.route('/updateemail', methods=['GET', 'POST'])
@token_required
def updateemail(user="default"):
    if request.method == 'POST':
        data = json.loads(request.data.decode())
        email= data['email']
        if  not re.match(r'[^@]+@[^@]+\.[^@]+', email):
            msg = 'Invalid email address!'
        else:
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            cursor.execute('Update users set userEmail=%s where userName=%s ',(email,user))
            mysql.connection.commit()
            msg='success'
            return jsonify({'result':msg})

    return jsonify({'result':msg})

@app.route('/updatepassword', methods=['GET', 'POST'])
@token_required
def updatepassword(user="default"):
    msg=''
    msg1=''
    if request.method == 'POST':
        data = json.loads(request.data.decode())
        oldpassword= data['oldPassword']
        newpassword= data['newPassword']
        confirmpassword= data['confirmPassword']
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('Select userpassword from users where userName=%s ',(user,))
        result_set = cursor.fetchall()
        for row in result_set:
            msg1=row["userpassword"]
        
        
        if oldpassword != msg1:
            msg='incorrect password'
        else:
            if newpassword != confirmpassword:
                msg = 'new passowrd and confirm password do not match'
            else:
                cursor.execute('Update users set userpassword=%s where userName=%s ',(newpassword,user))
                mysql.connection.commit()
                msg='successful'
        
        return jsonify({'result':msg})


    return jsonify({'result':'failed to update password'})


app.run()
