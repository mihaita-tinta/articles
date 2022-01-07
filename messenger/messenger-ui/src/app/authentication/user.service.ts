import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';
import {User} from '../model/user';

@Injectable({providedIn: 'root'})
export class UserService {

  private currentUserSubject: BehaviorSubject<User>;
  public currentUser: Observable<User>;

  constructor(private http: HttpClient) {
    const parse = JSON.parse(localStorage.getItem('currentUser'));
    if (parse) {
      this.init(parse);
    } else {
      this.currentUserSubject = new BehaviorSubject<User>(null);
      this.currentUser = this.currentUserSubject.asObservable();
    }
  }

  private init(parse) {
    if (this.currentUserSubject) {
      return;
    }

    this.currentUserSubject = new BehaviorSubject<User>(Object.assign(
      new User(),
      parse
    ));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  isLoggedIn() {
    const currentUser = localStorage.getItem('currentUser');
    console.log('isLoggedIn - currentUser: ' + currentUser);
    return currentUser != null;
  }

  getUser() {
    return this.currentUserSubject.asObservable();
  }

  whoami() {

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const options = {headers: headers};

    return this.http.get<User>(`${environment.backend}/users/`,
      options)
      .pipe(map(user => {
        console.log('whoami', user);
        const userString = JSON.stringify(user);
        localStorage.setItem('currentUser', userString);
        this.init(userString);
        this.currentUserSubject.next(Object.assign(
          new User(),
          user
        ));
        return user;
      }));
  }
  getActiveUsers() {

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const options = {headers: headers};

    return this.http.get<User[]>(`${environment.backend}/users/active/`, options);
  }

  logout() {
    console.log('logout - removing all local data');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
}
