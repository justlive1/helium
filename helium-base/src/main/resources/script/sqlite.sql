-- 用户表
drop table if exists user;
create table user
(
  id integer not null primary key autoincrement,
  username varchar comment '用户名' not null,
  password varchar comment '密码' not null,
  nickname varchar comment '昵称',
  signature varchar comment '签名',
  avatar varchar coment '头像',
  create_at timestamp comment '创建时间' default current_timestamp
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
  create_at timestamp comment '创建时间' default current_timestamp,
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
  order_index integer comment '分组排序' default 1
)
;

-- 通知
drop table if exists notify;
create table notify
(
 id integer not null primary key autoincrement,
 type integer comment '类型 1-请求加好友，2-请求加群' not null,
 from_id integer comment '发送者' not null,
 to_id integer comment '接收者' not null,
 group_id integer comment '分组id' not null,
 status integer comment '状态 0-待处理 1-已同意 2-已拒绝' default 0,
 remark varchar comment '附加信息',
 belong_to integer comment '所属',
 unread integer comment '未读标记',
 create_at timestamp default current_timestamp,
 expire integer comment '失效天数' default 0
)
;
