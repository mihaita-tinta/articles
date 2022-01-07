import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {WebsocketService} from '../websocket/websocket.service';
import {ConversationService} from '../authentication/conversation.service';
import {UserService} from '../authentication/user.service';

@Component({
  selector: 'app-conversation',
  templateUrl: './conversation.component.html',
  styleUrls: ['./conversation.component.css']
})
export class ConversationComponent implements OnInit {
  message = new FormControl('', [Validators.required]);

  @ViewChild('scrollMe') private messagesElement: ElementRef;

  conversationId: number;
  currentUser;

  avatarIndex;
  messages = [];
  error;

  constructor(private router: ActivatedRoute, private conversationService: ConversationService,
              private websocketService: WebsocketService, private userService: UserService) {

    this.avatarIndex = Math.floor(Math.random() * 10);
    this.userService.getUser().subscribe(u => this.currentUser = u);
    this.router.params
      .subscribe(params => {
          this.conversationId = params.conversationId;
          this.conversationService.getConversation(params.conversationId).subscribe(messages => {
            this.messages = messages;
          });
        }
      );

    this.websocketService.events.subscribe(event => {
      console.log('received: ', event);
      this.messages.push({...event,
        type: 'text'
      });
      this.scrollToBottom();
    });

    this.error = this.websocketService.errors.subscribe(error => {
      if (error == null) {
        return;
      }
      console.log('error: ', error);
    });
  }

  getErrorMessage() {
    return this.message.hasError('required') ? 'You must enter a value' : '';
  }

  send = () => {

    this.websocketService.send({
      content: this.message.value,
      from: this.currentUser.username,
      conversationId: this.conversationId,
      avatar: this.avatarIndex
    });

    this.message.setValue('');
  }

  ngOnInit(): void {
  }


  scrollToBottom(): void {
    try {
      this.messagesElement.nativeElement.scrollTop = this.messagesElement.nativeElement.scrollHeight;
    } catch (err) { }
  }
}
