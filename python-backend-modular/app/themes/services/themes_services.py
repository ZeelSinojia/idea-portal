from app.themes.dao import themes_dao as td
# http://localhost:5000/pythonlogin/ - this will be the login page, we need to use both GET and POST requests

def view_all_themes_services():
    data = td.view_all_themes()
    return data        

def view_one_theme_services(t_id):
	data=td.view_one_theme(t_id)
	return data