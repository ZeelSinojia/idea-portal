import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class DataService {
  private SERVICE_URL ="/psl_idea_portal/api";
  token:any='';
  private _isLoggedIn = new BehaviorSubject<boolean>(false);
  private user = new BehaviorSubject<string>('');
  constructor(private httpClient: HttpClient) {
    this.token = localStorage.getItem('token');
    }

   httpOptions = {
    headers: new HttpHeaders({
      'Content-Type':  'application/json',
      //'Authorization': 'Bearer '+this.token
    })
  };

  httpOptions_auth= {
    headers: new HttpHeaders({
          'Authorization': 'Bearer '+localStorage.getItem('token')
    })
  };

   isLoggedIn() {
    return this._isLoggedIn.asObservable();
}

   setLoggedIn(flag:boolean) {
    this._isLoggedIn.next(flag);
}

setUser(user){
this.user.next(user);
}

getUser(){
  return this.user.asObservable();
}

  public login(data: any):  Observable<any[]>{
    return this.httpClient.post<any>(this.SERVICE_URL + "/login",data, this.httpOptions);
  }

  public signup(data: any) : Observable<any[]>{
    return this.httpClient.post<any>(this.SERVICE_URL + "/signup", data, this.httpOptions);
  }

  public themes() : Observable<any[]>{
    return this.httpClient.get<any>(this.SERVICE_URL + "/themes", this.httpOptions);
  }
  public getTheme(id) : Observable<any[]>{
    return this.httpClient.get<any>(this.SERVICE_URL + "/themes/"+id, this.httpOptions);
  }

  public createTheme(data:any) : Observable<any[]>{
   const httpOptions_auth= {
      headers: new HttpHeaders({
            'Authorization': 'Bearer '+localStorage.getItem('token')
      })
    };

    return this.httpClient.post<any>(this.SERVICE_URL + "/user/create/theme", data, httpOptions_auth);
  }

  public createIdea(data:any) : Observable<any[]>{
    const httpOptions_auth= {
      headers: new HttpHeaders({
            'Authorization': 'Bearer '+localStorage.getItem('token')
      })
    };
    return this.httpClient.post<any>(this.SERVICE_URL + "/user/create/idea", data, httpOptions_auth);
  }

  public getIdeas(themeId) : Observable<any[]>{
    return this.httpClient.get<any>(this.SERVICE_URL + "/themes/"+themeId+"/ideas/newest", this.httpOptions);
  }

  public getIdea(id) : Observable<any[]>{
    return this.httpClient.get<any>(this.SERVICE_URL + "/idea/"+id, this.httpOptions);
  }

  public saveLikes(data:any) : Observable<any[]>{
    const httpOptions_auth= {
      headers: new HttpHeaders({
            'Authorization': 'Bearer '+localStorage.getItem('token')
      })
    };
    return this.httpClient.put<any>(this.SERVICE_URL + "/user/idea/like", data, httpOptions_auth);
  }

  public saveDisLikes(data:any) : Observable<any[]>{
    const httpOptions_auth= {
      headers: new HttpHeaders({
            'Authorization': 'Bearer '+localStorage.getItem('token')
      })
    };
    return this.httpClient.put<any>(this.SERVICE_URL + "/user/idea/like", data, httpOptions_auth);
  }

  public getAllLikes(ideaId) : Observable<any[]>{

    return this.httpClient.get<any>(this.SERVICE_URL + "/idea/"+ideaId+"/likes", this.httpOptions);
  }

  public getAllDisLikes(ideaId) : Observable<any[]>{

    return this.httpClient.get<any>(this.SERVICE_URL + "/idea/"+ideaId+"/dislikes", this.httpOptions);
  }

  public resetPassword(email) :Observable<any[]> {
    return this.httpClient.post<any>(this.SERVICE_URL + "/login/resetPassword",{"userEmail":email})
  }

  public setPassword(data:any) : Observable<any[]> {
    return this.httpClient.put<any> (this.SERVICE_URL + "/login/savePassword",data,this.httpOptions);
  }

  public saveComments(data:any) : Observable<any[]>{
    const httpOptions_auth= {
      headers: new HttpHeaders({
            'Authorization': 'Bearer '+localStorage.getItem('token')
      })
    };
    return this.httpClient.post<any>(this.SERVICE_URL + "/user/idea/comment", data, httpOptions_auth);
  }

  public getComments(ideaId) : Observable<any[]>{
       
    return this.httpClient.get<any>(this.SERVICE_URL + "/idea/"+ideaId+"/comments", this.httpOptions);
  }

  public saveParticipant(data:any) : Observable<any[]>{
    const httpOptions_auth= {
      headers: new HttpHeaders({
            'Authorization': 'Bearer '+localStorage.getItem('token')
      })
    };
    return this.httpClient.post<any>(this.SERVICE_URL + "/user/participant/participate", data, httpOptions_auth);
  }

  public getParticipants(ideaId) : Observable<any[]>{
    const httpOptions_auth= {
      headers: new HttpHeaders({
            'Authorization': 'Bearer '+localStorage.getItem('token')
      })
    };       
    return this.httpClient.get<any>(this.SERVICE_URL + "/user/participantsForIdea/"+ideaId, httpOptions_auth);
  }
  public updateProfile(data:any) : Observable<any[]>{
    const httpOptions_auth= {
      headers: new HttpHeaders({
            'Authorization': 'Bearer '+localStorage.getItem('token')
      })
    };
    return this.httpClient.put<any>(this.SERVICE_URL + "/user/profile/update/profile", data, httpOptions_auth);
  }

  public chnagePassword(data:any) : Observable<any[]>{
    const httpOptions_auth= {
      headers: new HttpHeaders({
            'Authorization': 'Bearer '+localStorage.getItem('token')
      })
    };
    return this.httpClient.put<any>(this.SERVICE_URL + "/user/profile/update/password", data, httpOptions_auth);
  }
}
