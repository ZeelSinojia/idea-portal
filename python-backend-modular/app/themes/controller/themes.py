from app.themes.services import themes_services as ts
from app import app
from flask import Flask, render_template, request, redirect, url_for, session
from flask_mysqldb import MySQL
import MySQLdb.cursors
import re
from app.sharepoint import sharepoint as sp
import app

from app.login.services import login_services as ls
from app import app
from flask import Flask, render_template, request, redirect, url_for, session
from flask_mysqldb import MySQL
import MySQLdb.cursors
import re
from functools import wraps
from app.utils import uti as ut
from flask import jsonify
import jwt

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

@app.route('/pythonlogin/viewtheme')
def viewtheme():
    # Check if user is loggedin
    if 'loggedin' in session:
        data = ts.view_all_themes_services() #data from database 
        return jsonify({'result':'success', 'data': data})
    # User is not loggedin redirect to login page
    return jsonify({'result':'failed'})

@app.route('/pythonlogin/viewonetheme', methods=['GET', 'POST'])
def viewonetheme():
    
    if request.method == "POST":
        t_id= request.form['t_id']
        # search by author or book
        
        data = ts.view_one_theme_services(t_id)
        return jsonify({'result':'success', 'data': data})
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
            sp.createTheme(f)
            cursor= ut.cur_defination()
            url = "https://teamcodered.sharepoint.com/sites/SEProductions/trial_1/folderB/"
            url = url + f.filename
            # print(url)
            cursor.execute('INSERT INTO theme_upload VALUES (NULL, %s)', (url,))
            cursor.execute('SELECT t_upload_id FROM theme_upload ORDER BY t_upload_id DESC LIMIT 1')
            account = cursor.fetchone()
            u_id = account['t_upload_id']
            print(account['t_upload_id'])
            ut.cur_commit()
            # u_id = cursor.execute('SELECT t_upload_id from theme_upload where theme_URL like %s' , (url,))
            
            cursor.execute('INSERT INTO themes VALUES (NULL, %s, %s,%s, %s)', (name,description,u_id, session['user_id'],))
            ut.cur_commit()
            # print(f)
            print()
            return('Inserted' + str(url))
        return()
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