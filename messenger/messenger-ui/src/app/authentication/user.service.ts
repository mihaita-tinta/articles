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
        this.currentUserSubject = new BehaviorSubject<User>(Object.assign(
            new User(),
            parse
        ));
        this.currentUser = this.currentUserSubject.asObservable();
    }

    saveToken(token: string) {
        localStorage.setItem('token', token);
    }

    isLoggedIn() {
        const item = localStorage.getItem('token');
        console.log('isLoggedIn - token: ' + item);
        return item != null;
    }
    getUser() {
        return this.currentUserSubject.asObservable();
    }

    whoami() {
        const token = localStorage.getItem('token');

        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        });

        const options = {headers: headers};

        return this.http.get<User>(`${environment.backend}/whoami`,
            options)
            .pipe(map(user => {
                console.log('whoami', user);
                // store user details and jwt token in local storage to keep user logged in between page refreshes
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

    availableRoles() {
        const token = localStorage.getItem('token');

        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        });

        const options = {headers: headers};

        return this.http.get<string[]>(`${environment.backend}/users/roles`,
            options);
    }

    getAll() {
        const token = localStorage.getItem('token');

        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        });

        const options = {headers: headers};

        return this.http.get<User[]>(`${environment.backend}/users`, options);
    }

    save(user: User) {
        const token = localStorage.getItem('token');

        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        });

        const options = {headers: headers};

        return this.http.post<User>(`${environment.backend}/users`, user, options);
    }

    update(user: User) {
        const token = localStorage.getItem('token');

        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        });

        const options = {headers: headers};

        return this.http.put<User>(`${environment.backend}/users/${user.id}`, user, options);
    }

    delete(user: User) {
        const token = localStorage.getItem('token');

        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        });

        const options = {headers: headers};

        return this.http.delete<User>(`${environment.backend}/users/${user.id}`, options);
    }

    updateUser(firstName, lastName, phone) {
        const token = localStorage.getItem('token');

        const headers = new HttpHeaders({
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        });

        const options = {headers: headers};

        return this.http.patch<User>(`${environment.backend}/whoami`,
            {firstName: firstName, lastName: lastName, phone: phone},
            options)
            .pipe(map(user => {
                // store user details and jwt token in local storage to keep user logged in between page refreshes
                localStorage.setItem('currentUser', JSON.stringify(user));
                this.currentUserSubject.next(user);
                return user;
            }));
    }

    logout() {
        console.log('logout - removing all local data');
        // remove user from local storage to log user out
        localStorage.removeItem('currentUser');
        localStorage.removeItem('token');
        localStorage.removeItem('deviceId');
        this.currentUserSubject.next(null);
    }
}
