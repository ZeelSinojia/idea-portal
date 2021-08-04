from app.utils import uti as ut


# http://localhost:5000/pythonlogin/ - this will be the login page, we need to use both GET and POST requests

def get_account_details(username,password):
    cursor= ut.cur_defination()
    cursor.execute('SELECT * FROM users WHERE userName = %s AND userpassword = %s ', (username, password))
    return cursor.fetchone()        


def check_account(username):
	cursor= ut.cur_defination()
	cursor.execute('SELECT * FROM users WHERE userName = %s', (username,))
	return cursor.fetchone()


def insert_new_login(password,roleid,username, email,companyname):
	cursor= ut.cur_defination()
	cursor.execute('INSERT INTO users VALUES (NULL, %s, %s,%s, %s,%s)', (password,roleid,username, email,companyname,))
	ut.cur_commit()

def update_company_getrole(userid):
	cursor= ut.cur_defination()
	cursor.execute('Select roleId from users where user_id LIKE %s ',[userid])
	return cursor.fetchall()

def update_company(company,user_id):
	cursor= ut.cur_defination()
	cursor.execute('Update users set userCompany=%s where user_id=%s and roleId=%s',(company,user_id,'001'))
	ut.cur_commit()

def update_email(email,user_id):
	cursor= ut.cur_defination()
	cursor.execute('Update users set userEmail=%s where user_id=%s ',(email,user_id))
	ut.cur_commit()

def update_password_getpassword(user_id):
	cursor= ut.cur_defination()
	cursor.execute('Select userpassword from users where user_id LIKE %s ',[user_id])
	return cursor.fetchall()

def update_password(newpassword,user_id):
	cursor= ut.cur_defination()
	cursor.execute('Update users set userpassword=%s where user_id=%s ',(newpassword,user_id))
	ut.cur_commit()