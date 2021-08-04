from app.login.dao import login_dao as ld
# http://localhost:5000/pythonlogin/ - this will be the login page, we need to use both GET and POST requests

def get_account_services(username,password):
    account = ld.get_account_details(username,password)
    return account        

def check_account_services(username):
	account=ld.check_account(username)
	return account

def insert_new_login_services(password,roleid,username, email,companyname):
	ld.insert_new_login(password,roleid,username, email,companyname)

def update_company_getrole_services(userid):
	result_set=ld.update_company_getrole(userid)
	return result_set

def update_company_servies(company,user_id):
	ld.update_company(company,user_id)

def update_email_services(email,user_id):
	ld.update_email(email,user_id)

def update_password_getpassword_services(user_id):
	result_set=ld.update_password_getpassword(user_id)
	return result_set

def update_password_services(newpassword,user_id):
	ld.update_password(newpassword,user_id)