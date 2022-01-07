import { Component } from '@angular/core';
import {UserService} from './authentication/user.service';
import {Observable} from 'rxjs';
import {User} from './model/user';
import {WebsocketService} from './websocket/websocket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'messenger-ui';
  user: Observable<User>;
  constructor(private userService: UserService, private websocketService: WebsocketService) {
    this.user = this.userService.getUser();
  }

  logout() {
    this.userService.logout();
  }
}
