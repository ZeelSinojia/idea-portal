import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  empData;
  ideaUrl : string = "http://127.0.0.1:5000/";
  constructor(private httpClient : HttpClient) { }

  login(user,pass){
    //window.alert("get all called");
    return this.httpClient.post('http://127.0.0.1:5000/login',{"user":user,"password":pass});
  }

  register(userName,pass,email,role,company){
    //window.alert("get all called");
    return this.httpClient.post('http://127.0.0.1:5000/register',{"userName":userName,"password":pass,"userEmail":email,"role":role,"companyname":company});
  }

  loggedIn(){
    return this.httpClient.get('http://127.0.0.1:5000/checkLogin',{headers: {'x-access-token':localStorage.getItem('access_token') }});
  }

  logout() {
    //console.log(localStorage.getItem('access_token').substring(0,10));
    if(localStorage.getItem('access_token')!==null){
    return this.httpClient.get('http://127.0.0.1:5000/LogoutCheck',{headers: {'x-access-token':localStorage.getItem('access_token') }});
    }
    else{
      window.alert("logged out");
      return (null);
    }
  }

  updateEmail(email){
    return this.httpClient.post('http://127.0.0.1:5000/updateemail',{'email':email},{headers: {'x-access-token':localStorage.getItem('access_token')} });
  }

  updateCompany(company){
    return this.httpClient.post('http://127.0.0.1:5000/updatecompany',{'company':company},{headers: {'x-access-token':localStorage.getItem('access_token') }});
  }

  updatePassword(oldPassword,newPassword,confirmPassword){
    return this.httpClient.post('http://127.0.0.1:5000/updatepassword',{'oldPassword':oldPassword,'newPassword':newPassword,'confirmPassword':confirmPassword},{headers: {'x-access-token':localStorage.getItem('access_token') }});
  }
}
