import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UserService} from '../authentication/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, AfterViewInit {
  username = new FormControl('', [Validators.required]);

  @ViewChild('login') login: any;

  constructor(private router: Router, private userService: UserService) {
    if (this.userService.isLoggedIn()) {
      this.router.navigate(['/users']);
    }
  }

  ngOnInit(): void {
  }

  ngAfterViewInit() {
    console.log('login-started - ngAfterViewInit');
    const nativeElement = this.login.nativeElement;
    nativeElement.addEventListener('login-started', e => {
      console.log('login-started - event', e);
      nativeElement.fetchOptions.headers = { ...nativeElement.fetchOptions.headers, 'X-XSRF-TOKEN': this.getCookie('XSRF-TOKEN')};

    }, false);

    nativeElement.addEventListener('login-retrieved', e => {
      console.log('login-retrieved - event', e);
    }, false);
    nativeElement.addEventListener('login-finished', e => {
      console.log('login-finished - event', e);
      this.userService.whoami().subscribe(user => {
        console.log('logged in as: ', user);
        this.router.navigate(['/users']);
      });
    }, false);
    nativeElement.addEventListener('login-error', e => {
      console.log('login-error - event', e);
    }, false);

  }

  getCookie = (name) => {
    const cookies = document.cookie;
    const parts = cookies.split(name + '=');
    let cookieValue = '';
    if (parts.length === 2) {
      cookieValue = parts.pop().split(';').shift();
    }
    return cookieValue;
  }

}
