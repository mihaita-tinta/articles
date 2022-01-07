import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Conversation} from '../model/Conversation';
import {Message} from '../model/Message';

@Injectable({providedIn: 'root'})
export class ConversationService {

  constructor(private http: HttpClient) {
  }

  getConversation = (id) => {

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const options = {headers: headers};

    return this.http.get<Message[]>(`${environment.backend}/conversations/${id}/messages`,
      options);
  }

  getConversations() {

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const options = {headers: headers};

    return this.http.get<Conversation[]>(`${environment.backend}/conversations/`,
      options);
  }

  postConversation(currentUserId, otherUserId) {

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-XSRF-TOKEN': this.getCookie('XSRF-TOKEN')
    });

    const options = {headers: headers};

    return this.http.post<Conversation>(`${environment.backend}/conversations/`,
      {
        participants: [{
          id: currentUserId
        },
          {
            id: otherUserId
          }
        ]
      },
      options);
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
