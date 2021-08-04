from app.themes.services import themes_services as ts
from app import app
from flask import Flask, render_template, request, redirect, url_for, session
from flask_mysqldb import MySQL
from flask import jsonify
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

@app.route('/pythonlogin/viewidea', methods=['GET', 'POST'])
def viewidea():
   
    # Check if user is loggedin
    if request.method == "POST":
        t_id= request.form['t_id']
        # search by author or book
        if request.form.get('action1') == 'value1':
       
            cursor= ut.cur_defination()
            cursor.execute("SELECT t.themename,i.ideadescription,u.userName,iu.idea_URL FROM ideas i join themes t on i.theme_id=t.theme_id join users u on i.user_id=u.user_id  join idea_upload iu on i.idea_id=iu.idea_id WHERE i.theme_id LIKE %s ", [t_id])
            # cursor.execute("SELECT t.themename,i.ideadescription,u.userName,iu.idea_URL FROM ideas i join themes t on i.theme_id=t.theme_id join users u on i.user_id=u.user_id  join idea_upload iu on i.i_upload_id=iu.i_upload_id WHERE i.theme_id LIKE %s ", [t_id])
            #conn.commit()
            data = cursor.fetchall()
            
        return jsonify({'result':'success', 'data': data})
    
    return jsonify({'result':'failed'})




@app.route('/pythonlogin/viewoneidea', methods=['GET', 'POST'])
def viewoneidea():
    
    if request.method == "POST":
        i_id= request.form['i_id']
        # search by author or book
        cursor= ut.cur_defination()
        # cursor.execute("SELECT t.themename,i.ideadescription,u.userName,iu.idea_URL FROM ideas i join themes t on i.theme_id=t.theme_id join users u on i.user_id=u.user_id  join idea_upload iu on i.i_upload_id=iu.i_upload_id WHERE i.idea_id LIKE %s ", [i_id])
        cursor.execute("SELECT t.themename,i.ideadescription,u.userName,iu.idea_URL FROM ideas i join themes t on i.theme_id=t.theme_id join users u on i.user_id=u.user_id  join idea_upload iu on i.idea_id=iu.idea_id WHERE i.idea_id LIKE %s ", [i_id])
        #conn.commit()
        data = cursor.fetchall()
        return jsonify({'result':'success', 'data': data})
    return jsonify({'result':'failed'})

@app.route('/pythonlogin/commentidea', methods=['GET', 'POST'])
def commentidea():

    return render_template('commentidea.html')

@app.route('/pythonlogin/commentonidea', methods=['GET', 'POST'])
def commentonidea():
    msg=''
    if request.method == 'POST' and 'comment' in request.form :
        comment= request.form['comment']
        i_id= request.form['i_id']
        cursor= ut.cur_defination()
        cursor.execute('INSERT INTO comments VALUES (NULL, %s, %s,%s)', (comment,i_id, session['user_id'],))
        ut.cur_commit()
        msg='comment successful'
        return jsonify({'result':'success'})
    return jsonify({'result':'failed'})


@app.route('/pythonlogin/createidea', methods=['GET', 'POST'])
def createidea():
    if request.method == "POST":
        if 'loggedin' in session:
            theme_id = request.form['theme_id']
            description = request.form['description']
            f = request.files['file']
            sp.createTheme(f)
            cursor= ut.cur_defination()
            url = "https://teamcodered.sharepoint.com/sites/SEProductions/trial_1/folderB/"
            url = url + f.filename
            cursor.execute('INSERT INTO idea_upload VALUES (NULL, %s)', (url,))
            cursor.execute('SELECT idea_id FROM idea_upload ORDER BY idea_id DESC LIMIT 1')
            account = cursor.fetchone()
            u_id = account['idea_id']
            print(account['idea_id'])
            ut.cur_commit()
            cursor.execute('INSERT INTO ideas VALUES (NULL, %s, %s,%s, %s)', (description,session['user_id'],theme_id,u_id,))
            ut.cur_commit()
            return('Inserted' + str(url))
        return()