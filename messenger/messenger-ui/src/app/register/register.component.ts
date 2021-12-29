import { Component, OnInit } from '@angular/core';
import {FormControl, Validators} from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  username = new FormControl('', [Validators.required]);

  constructor() { }

  ngOnInit(): void {
  }
  getErrorMessage() {
    return this.username.hasError('required') ? 'You must enter a value' : '';
  }

}
