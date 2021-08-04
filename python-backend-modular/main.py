from app import app
from app.login.controller import login
from app.themes.controller import themes
from app.ideas.controller import idea
from flask_cors import CORS, cross_origin
CORS(app)
app.run()
#@app.route('/login', methods=['GET', 'POST'])
#def login():
#    lo
'''
if __name__=="__main__":
    app.run()
'''
'''
from flask import Flask, render_template, request, redirect, url_for, session
from flask_mysqldb import MySQL
import MySQLdb.cursors
import re

app = Flask(__name__)

# Change this to your secret key (can be anything, it's for extra protection)
app.secret_key = '12345'

# Enter your database connection details below
app.config['MYSQL_HOST'] = 'localhost'
app.config['MYSQL_USER'] = 'root1'
app.config['MYSQL_PASSWORD'] = 'mayuri9011'
app.config['MYSQL_DB'] = 'ideaportal'

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
        cursor.execute('SELECT * FROM users WHERE userName = %s AND userpassword = %s ', (username, password))
        # Fetch one record and return result
        account = cursor.fetchone()
        # If account exists in accounts table in out database
        if account:
            # Create session data, we can access this data in other routes
            session['loggedin'] = True
            session['user_id'] = account['user_id']
            session['userName'] = account['userName']
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
   session.pop('user_id', None)
   session.pop('userName', None)
   # Redirect to login page
   return redirect(url_for('login'))


# http://localhost:5000/pythinlogin/register - this will be the registration page, we need to use both GET and POST requests
@app.route('/pythonlogin/register', methods=['GET', 'POST'])
def register():
    # Output message if something goes wrong...
    msg = ''
    # Check if "username", "password" and "email" POST requests exist (user submitted form)
    if request.method == 'POST' and 'username' in request.form and 'password' in request.form and 'companyname' in request.form and 'email' in request.form and 'role' in request.form:
        # Create variables for easy access
        username = request.form['username']
        password = request.form['password']
        companyname=request.form['companyname']
        email = request.form['email']
        role=request.form['role']

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
        elif not username or not password or not email or not companyname:
            msg = 'Please fill out the form!'
        elif len(username)<8 and len(password)<8:
            msg='Length of username password should be atleast 8 characters'
        else:
            # Account doesnt exists and the form data is valid, now insert new account into accounts table
            if(role=='Client Partner'):
                cursor.execute('INSERT INTO users VALUES (NULL, %s, %s,%s, %s,%s)', (password,'001',username, email,companyname,))
            elif(role=='Product Manag'):
                cursor.execute('INSERT INTO users VALUES (NULL, %s, %s,%s, %s,%s)', (password,'002',username, email,'Persistent',))
            elif(role=='Participant'):
                cursor.execute('INSERT INTO users VALUES (NULL, %s, %s,%s, %s,%s)', (password,'003',username, email,'Persistent',))
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


@app.route('/pythonlogin/updatecompany', methods=['GET', 'POST'])
def updatecompany():
    msg=''
    msg1=''
    r='001'
    if request.method == 'POST' and 'loggedin' in session :
        company= request.form['company']
        cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
        cursor.execute('Select roleId from users where user_id LIKE %s ',[session['user_id']])
        result_set = cursor.fetchall()
        for row in result_set:
            msg1=row["roleId"]
        
        if msg1 != '001':
            msg='cannot change company name'
        else:
            cursor.execute('Update users set userCompany=%s where user_id=%s and roleId=%s',(company,session['user_id'],'001'))
            mysql.connection.commit()
            msg='successful'
        
        return render_template('updatecompany.html',msg=msg)

    return render_template('updatecompany.html',msg=msg)

@app.route('/pythonlogin/updateemail', methods=['GET', 'POST'])
def updateemail():
    msg=''
    if request.method == 'POST' and 'loggedin' in session :
        email= request.form['email']
        if  not re.match(r'[^@]+@[^@]+\.[^@]+', email):
            msg = 'Invalid email address!'
        else:
            cursor = mysql.connection.cursor(MySQLdb.cursors.DictCursor)
            cursor.execute('Update users set userEmail=%s where user_id=%s ',(email,session['user_id']))
            mysql.connection.commit()
            return render_template('updateemail.html',msg=msg)

    return render_template('updateemail.html',msg=msg)

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