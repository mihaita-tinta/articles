import { Component, OnInit } from '@angular/core';
import {ConversationService} from '../authentication/conversation.service';
import {Conversation} from '../model/Conversation';

@Component({
  selector: 'app-conversation-list',
  templateUrl: './conversation-list.component.html',
  styleUrls: ['./conversation-list.component.css']
})
export class ConversationListComponent implements OnInit {

  conversations: Conversation[];

  constructor(private conversationService: ConversationService) {
    conversationService.getConversations().subscribe(conversations => {
      this.conversations = conversations;
    });
  }

  ngOnInit(): void {
  }

  getNames = (conversation: Conversation) => {
    return conversation.participants.map(p => p.username).join(', ');
  }
}
