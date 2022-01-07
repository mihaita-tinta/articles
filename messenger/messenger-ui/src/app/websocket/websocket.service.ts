import {Injectable, OnDestroy} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {WebsocketEvent} from '../model/websocket.event';
import {environment} from '../../environments/environment';

@Injectable({providedIn: 'root'})
export class WebsocketService implements OnDestroy {

  private socket: WebSocket;
  private subject: BehaviorSubject<WebsocketEvent>;
  public events: Observable<WebsocketEvent>;
  private errorsSubject: BehaviorSubject<string>;
  public errors: Observable<string>;

  constructor() {
    console.log('websocketService - init');
    this.subject = new BehaviorSubject<WebsocketEvent>(null);
    this.events = this.subject.asObservable();
    this.errorsSubject = new BehaviorSubject<string>(null);
    this.errors = this.errorsSubject.asObservable();
    this.connect();
  }

  ngOnDestroy(): void {
    this.socket.close();
  }

  connect = () => {
    const self = this;
    this.socket = new WebSocket(environment.ws);
    this.socket.addEventListener('message', event => {
      console.log('onMessage - data: ' + event.data);
      self.subject.next(JSON.parse(event.data));
    });
    this.socket.onclose = e => {
      console.log('Socket is closed. Reconnect will be attempted in 2 seconds.', e.reason);
      self.errorsSubject.next('Server not available. Trying to connect ...');
      setTimeout(() => {
        self.connect();
      }, 2000);
    };

    this.socket.onerror = err => {
      console.error('Socket encountered error: ', err, 'Closing socket');
      self.socket.close();
      self.errorsSubject.next('Error: ' + err);
    };
  };

  isConnected = () => {
    return this.socket.OPEN;
  };
  send = (event: WebsocketEvent) => {
    this.socket.send(JSON.stringify(event));
  };

}
