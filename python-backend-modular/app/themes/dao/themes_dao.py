from app.utils import uti as ut


# http://localhost:5000/pythonlogin/ - this will be the login page, we need to use both GET and POST requests

def view_all_themes():
    cursor= ut.cur_defination()
    cursor.execute('SELECT t.themename,t.themedescription,u.userName FROM themes t join users u on t.user_id=u.user_id')
    return cursor.fetchall()        

def view_one_theme(t_id):
	cursor= ut.cur_defination()
	cursor.execute("SELECT t.themename,t.themedescription,u.userName FROM themes t join users u on t.user_id=u.user_id WHERE t.theme_id LIKE %s ", [t_id])
	return cursor.fetchall()