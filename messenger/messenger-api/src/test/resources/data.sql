insert into user ( username, id) values ( 'junit-user', nextval('hibernate_sequence'));
insert into user ( username, id) values ( 'participant1', nextval('hibernate_sequence'));
insert into user ( username, id) values ( 'participant2', nextval('hibernate_sequence'));
insert into conversation (date_started, id) values ('2012-09-17 18:47:52.69', nextval('hibernate_sequence'));
insert into conversation_participants (conversation_id, participants_id) values (select id from conversation where date_started='2012-09-17 18:47:52.69', select id from user where username='participant1');
