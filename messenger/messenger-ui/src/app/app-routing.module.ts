import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {UsersComponent} from './users/users.component';
import {RegisterComponent} from './register/register.component';
import {LoginComponent} from './login/login.component';
import {ConversationComponent} from './conversation/conversation.component';
import {ConversationListComponent} from './conversation-list/conversation-list.component';
import {AuthenticationFilter} from './authentication/authentication.filter';

const routes: Routes = [
  {
    path: 'users',
    component: UsersComponent,
    canActivate: [AuthenticationFilter]
  },
  {
    path: 'conversation',
    component: ConversationListComponent,
    canActivate: [AuthenticationFilter]
  },
  {
    path: 'conversation/:conversationId',
    component: ConversationComponent,
    canActivate: [AuthenticationFilter]
  },
  {path: 'register', component: RegisterComponent},
  {path: '', component: LoginComponent},
  {path: 'login', component: LoginComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
