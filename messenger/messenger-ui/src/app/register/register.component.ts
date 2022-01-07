import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit, AfterViewInit {
  username = new FormControl('', [Validators.required]);

  @ViewChild('register') register: any;

  constructor() {
  }

  ngAfterViewInit() {

    const nativeElement = this.register.nativeElement;
    nativeElement.addEventListener('registration-started', e => {
      console.log('registration-started - event', e);
      nativeElement.fetchOptions.headers = { ...nativeElement.fetchOptions.headers, 'X-XSRF-TOKEN': this.getCookie('XSRF-TOKEN')};
    }, false);

    nativeElement.addEventListener('registration-created', e => {
      console.log('registration-created - event', e);
    }, false);
    nativeElement.addEventListener('registration-finished', e => {
      console.log('registration-finished - event', e);
    }, false);
    nativeElement.addEventListener('registration-error', e => {
      console.log('registration-error - event', e);
    }, false);


  }

  ngOnInit(): void {
  }
  getErrorMessage = () => {
    return this.username.hasError('required') ? 'You must enter a value' : '';
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
