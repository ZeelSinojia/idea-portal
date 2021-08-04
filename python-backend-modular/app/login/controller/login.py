from app.login.services import login_services as ls
from app import app
from flask import Flask, render_template, request, redirect, url_for, session
from flask_mysqldb import MySQL
import MySQLdb.cursors
import re

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
from app.utils import uti as ut

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
            cursor= ut.cur_defination()
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
    if request.method == 'POST':
        # Create variables for easy access
        data = json.loads(request.data.decode())
        username = data["user"]
        password = data["password"]
        
        # Check if account exists using MySQL
        account=ls.get_account_services(username,password)
        # If account exists in accounts table in out database
        if account:
            # Create session data, we can access this data in other routes
            session['loggedin'] = True
            session['user_id'] = account['user_id']
            session['userName'] = account['userName']
            #generating jwt access token
            payload = {"user_id": username,'exp' : datetime.utcnow() + timedelta(minutes = 30)}
            JwtToken = jwt.encode(payload,app.secret_key)
            # Redirect to home page
            return jsonify({'token' :JwtToken.decode("UTF-8"),'result':'success'})
        else:
            # Account doesnt exist or username/password incorrect
            msg = 'Incorrect username/password!'
    # Show the login form with message (if any)
    return jsonify({'code':'404','result':'failed','msg':msg})

# http://localhost:5000/checkLogin
@app.route('/checkLogin')
@token_required
def checkLogin(user='default'):
    return jsonify({'result':'success','user':user})

@app.route('/LogoutCheck')
@token_required
def LogoutCheck(user='default'):
    return jsonify({'result':'success','user':user})

@app.route('/pythonlogin/home')
def home():
    # Check if user is loggedin
    
    if 'loggedin' in session:
        # User is loggedin show them the home page
        return render_template('home.html', userName=session['userName'])
    # User is not loggedin redirect to login page
    return redirect(url_for('login'))

# http://localhost:5000/python/logout - this will be the logout page
@app.route('/logout')
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
    if request.method == 'POST':
        # Create variables for easy access
        data = json.loads(request.data.decode())
        username = data["userName"]
        password = data["password"]
        companyname=data['companyname']
        email = data['userEmail']
        role=data['role']

        
        account = ls.check_account_services(username)
        # If account exists show error and validation checks
        if account:
            msg = 'Account already exists!'
        elif not re.match(r'[^@]+@[^@]+\.[^@]+', email):
            msg = 'Invalid email address!'
        elif not re.match(r'[A-Za-z0-9]+', username):
            msg = 'Username must contain only characters and numbers!'
        elif not username or not password or not email or not companyname:
            msg = 'Please fill out the form!'
        elif len(username)<8 and len(password)<8:
            msg='Length of username password should be atleast 8 characters'
        else:
            # Account doesnt exists and the form data is valid, now insert new account into accounts table
            if(role=='Client Partner'):
                roleid='001'
                ls.insert_new_login_services(password,roleid,username, email,companyname)
            elif(role=='Product Manag'):
                companyname='Persistent'
                roleid='002'
                ls.insert_new_login_services(password,roleid,username, email,companyname)
            elif(role=='Participant'):
                companyname='Persistent'
                roleid='003'
                ls.insert_new_login_services(password,roleid,username, email,companyname)
        
            msg = 'You have successfully registered!'
    elif request.method == 'POST':
        # Form is empty... (no POST data)
        msg = 'Please fill out the form!'
    # Show registration form with message (if any)
    return jsonify({'result':msg})

@app.route('/pythonlogin/updatecompany', methods=['GET', 'POST'])
def updatecompany():
    msg=''
    msg1=''
    #r='001'
    if request.method == 'POST' and 'loggedin' in session :
        company= request.form['company']
        result_set =ls.update_company_getrole_services(session['user_id'])
        for row in result_set:
            msg1=row["roleId"]
        
        if msg1 != '001':
            msg='cannot change company name'
        else:
            ls.update_company_servies(company,session['user_id'])
            msg='successful'
        
        return jsonify({'result':msg})

    return jsonify({'result':msg})

@app.route('/pythonlogin/updateemail', methods=['GET', 'POST'])
def updateemail():
    msg=''
    if request.method == 'POST' and 'loggedin' in session :
        email= request.form['email']
        if  not re.match(r'[^@]+@[^@]+\.[^@]+', email):
            msg = 'Invalid email address!'
        else:
            ls.update_email_services(email,session['user_id'])
            msg='successful'
            return jsonify({'result':msg})

    return jsonify({'result':msg})

@app.route('/pythonlogin/updatepassword', methods=['GET', 'POST'])
def updatepassword():
    msg=''
    msg1=''
    if request.method == 'POST' and 'loggedin' in session :
        oldpassword= request.form['oldpassword']
        newpassword= request.form['newpassword']
        confirmpassword= request.form['confirmpassword']
        result_set =ls.update_password_getpassword_services(session['user_id'])
        for row in result_set:
            msg1=row["userpassword"]
        
        
        if oldpassword != msg1:
            msg='incorrect password'
        else:
            if len(newpassword)<8:
                msg = 'Length should atleast be 8 characters'
            elif newpassword != confirmpassword:
                msg = 'new passowrd and confirm password do not match'
            else:
                ls.update_password_services(newpassword,session['user_id'])
                msg='successful'
        
        return jsonify({'result':msg})


    return jsonify({'result':'failed to update password'})


'''

    
@app.route('/pythonlogin/profile')
def profile():
    # Check if user is loggedin
    if 'loggedin' in session:
        # We need all the account info for the user so we can display it on the profile page
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT * FROM users WHERE user_id = %s', (session['user_id'],))
        account = cursor.fetchone()
        # Show the profile page with account info
        return render_template('profile.html', account=account)
    # User is not loggedin redirect to login page
    return redirect(url_for('login'))

# http://localhost:5000/pythinlogin/profile - this will be the profile page, only accessible for loggedin users
@app.route('/pythonlogin/viewtheme')
def viewtheme():
    # Check if user is loggedin
    if 'loggedin' in session:
        # We need all the account info for the user so we can display it on the profile page
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT t.themename,t.themedescription,u.userName FROM themes t join users u on t.user_id=u.user_id')
        data = cursor.fetchall() #data from database 
        return render_template('viewtheme.html', value=data)
    # User is not loggedin redirect to login page
    return redirect(url_for('login'))

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
            
        return render_template('viewidea.html', data=data)
    
    return render_template('viewidea.html')

@app.route('/pythonlogin/viewonetheme', methods=['GET', 'POST'])
def viewonetheme():
    
    if request.method == "POST":
        t_id= request.form['t_id']
        # search by author or book
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute("SELECT t.themename,t.themedescription,u.userName FROM themes t join users u on t.user_id=u.user_id WHERE t.theme_id LIKE %s ", [t_id])
        #conn.commit()
        data = cursor.fetchall()
        return render_template('viewonetheme.html', data=data)
    return render_template('viewonetheme.html')

@app.route('/pythonlogin/viewoneidea', methods=['GET', 'POST'])
def viewoneidea():
    
    if request.method == "POST":
        i_id= request.form['i_id']
        # search by author or book
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute("SELECT t.themename,i.ideadescription,u.userName,iu.idea_URL FROM ideas i join themes t on i.theme_id=t.theme_id join users u on i.user_id=u.user_id  join idea_upload iu on i.i_upload_id=iu.i_upload_id WHERE i.idea_id LIKE %s ", [i_id])
        #conn.commit()
        data = cursor.fetchall()
        return render_template('viewoneidea.html', data=data)
    return render_template('viewoneidea.html')

@app.route('/pythonlogin/commentidea', methods=['GET', 'POST'])
def commentidea():

    return render_template('commentidea.html')

@app.route('/pythonlogin/commentonidea', methods=['GET', 'POST'])
def commentonidea():
    msg=''
    if request.method == 'POST' and 'comment' in request.form :
        comment= request.form['comment']
        i_id= request.form['i_id']
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('INSERT INTO comments VALUES (NULL, %s, %s,%s)', (comment,i_id, session['user_id'],))
        mysql.connection.commit()
        msg='comment successful'
        return render_template('commentidea.html',msg=msg)
    return render_template('commentidea.html',msg=msg)

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
            return render_template('likedislike.html',msg=msg)
        elif request.form.get('action2') == 'value2':
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            cursor.execute('INSERT INTO dislikes VALUES (NULL, %s, %s)', (i_id,session['user_id'],))
            mysql.connection.commit()
            msg='dislike successful'
            return render_template('likedislike.html',msg=msg)
    return render_template('likedislike.html',msg=msg)

@app.route('/pythonlogin/viewcomment', methods=['GET', 'POST'])
def viewcomment():
    if 'loggedin' in session:
        # We need all the account info for the user so we can display it on the profile page
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('SELECT c.commentvalue,i.ideadescription,u.userName FROM comments c join ideas i on c.idea_id=i.idea_id join users u on c.user_id=u.user_id')
        data = cursor.fetchall() #data from database 
        return render_template('viewcomment.html', value=data)
    # User is not loggedin redirect to login page
    return redirect(url_for('login'))

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
        return render_template('countlikedislike.html', data=data,data1=data1)
    return render_template('countlikedislike.html')



@app.route('/pythonlogin/updatepassword', methods=['GET', 'POST'])
def updatepassword():
    msg=''
    msg1=''
    if request.method == 'POST' and 'loggedin' in session :
        oldpassword= request.form['oldpassword']
        newpassword= request.form['newpassword']
        confirmpassword= request.form['confirmpassword']
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('Select userpassword from users where user_id LIKE %s ',[session['user_id']])
        result_set = cursor.fetchall()
        for row in result_set:
            msg1=row["userpassword"]
        
        
        if oldpassword != msg1:
            msg='incorrect password'
        else:
            if len(newpassword)<8:
                msg = 'Length should atleast be 8 characters'
            elif newpassword != confirmpassword:
                msg = 'new passowrd and confirm password do not match'
            else:

                cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
                cursor.execute('Update users set userpassword=%s where user_id=%s ',(newpassword,session['user_id']))
                mysql.connection.commit()
                msg='successful'
        
        return render_template('updatepassword.html',msg=msg)


    return render_template('updatepassword.html',msg=msg)

'''