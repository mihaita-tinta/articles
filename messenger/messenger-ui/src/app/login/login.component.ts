import { Component, OnInit } from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  username = new FormControl('', [Validators.required]);

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  getErrorMessage() {
    return this.username.hasError('required') ? 'You must enter a value' : '';
  }

  login() {
    this.router.navigate(['/users']);
  }

}
