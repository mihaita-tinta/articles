import {Component, OnInit} from '@angular/core';
import {UserService} from '../authentication/user.service';
import {ConversationService} from '../authentication/conversation.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {

  users;
  currentUser;

  constructor(private userService: UserService, private conversationService: ConversationService, private router: Router) {
    this.refresh();
    this.userService.getUser().subscribe(u => this.currentUser = u);
  }

  ngOnInit(): void {
  }

  startConversation = (user) => {
    this.conversationService.postConversation(this.currentUser.id, user.id)
      .subscribe(conversation => {
        this.router.navigate(['conversation', conversation.id]);
    });
  }

  refresh = () => {
    this.userService.getActiveUsers().subscribe(users => {
      this.users = users;
    });
  }
}
