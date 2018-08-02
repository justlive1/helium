-- 用户表
drop table if exists user;
create table user
(
  id integer not null primary key autoincrement,
  username varchar comment '用户名' not null,
  password varchar comment '密码' not null,
  nickname varchar comment '昵称',
  signature varchar comment '签名',
  create_at timestamp comment '创建时间' default current_date
)
;

-- 好友
drop table if exists friend;
create table friend
(
  id integer not null primary key autoincrement,
  user_id integer comment '所属用户id'  not null,
  friend_user_id integer comment '好友id' not null,
  friend_group_id integer comment '分组id',
  create_at timestamp comment '创建时间' default current_date,
  remark varchar comment '好友备注'
)
;

-- 好友分组
drop table if exists friend_group;
create table friend_group
(
  id integer not null primary key autoincrement,
  user_id integer comment '所属用户id' not null,
  name varchar comment '分组名称' not null,
  order_index integer comment '分组排序' default 0
)
;

-- 通知
drop table if exists friend_notify;
create table friend_notify
(
  id integer not null primary key autoincrement,
  type integer comment '类型 1-请求加好友，2-请求加群' not null,
  user_id integer comment '通知id' not null,
  target_id integer comment '对象id' not null,
  group_id integer comment '分组id' not null,
  status integer comment '状态 0-待处理 1-成功 2-拒绝' default 0,
  remark varchar comment '附加信息',
  create_at timestamp default current_date,
  expire integer comment '失效天数' default 3
)
;
