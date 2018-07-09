create table user
(
  id integer not null primary key autoincrement,
  username varchar not null,
  password varchar not null,
  password_salt varchar default 'salt'

)
;

insert into user (username, password, password_salt)
values ('user',	'EE37A2F7A585BD59F7E042FA97655BD4DC31B04FA4AFDBE718BE719B7F523F3A1FB8AB8F67070FD1DE10809AC5B9433CEC3ECC9054C69C683A296237DF181895','salt')
