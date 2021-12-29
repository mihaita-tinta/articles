import { Component, OnInit } from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-conversation',
  templateUrl: './conversation.component.html',
  styleUrls: ['./conversation.component.css']
})
export class ConversationComponent implements OnInit {
  message = new FormControl('', [Validators.required]);

  conversation = {
    members: ['user1', 'user2']
  };

  messages = [];
  constructor(private router: Router) { }

  getErrorMessage() {
    return this.message.hasError('required') ? 'You must enter a value' : '';
  }

  send = () => {
    this.messages.push({
      content: this.message.value,
      from: 'user1',
      type: 'text'
    });
    this.message.setValue('');
  }

  ngOnInit(): void {
  }

}
