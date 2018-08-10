-- 用户表
drop table if exists user;
create table user
(
  id int not null primary key auto_increment,
  username varchar(40) comment '用户名' not null,
  password varchar(40) comment '密码' not null,
  nickname varchar(40) comment '昵称',
  signature varchar(40) comment '签名',
  avatar varchar(200) comment '头像',
  create_at timestamp comment '创建时间' default current_timestamp
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

-- 好友
drop table if exists friend;
create table friend
(
  id bigint not null primary key auto_increment,
  user_id bigint comment '所属用户id'  not null,
  friend_user_id bigint comment '好友id' not null,
  friend_group_id bigint comment '分组id',
  create_at timestamp comment '创建时间' default current_timestamp,
  remark varchar(40) comment '好友备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

-- 好友分组
drop table if exists friend_group;
create table friend_group
(
  id bigint not null primary key auto_increment,
  user_id bigint comment '所属用户id' not null,
  name varchar(40) comment '分组名称' not null,
  order_index bigint comment '分组排序' default 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

-- 通知
drop table if exists notify;
create table notify
(
 id bigint not null primary key auto_increment,
 type int comment '类型 1-请求加好友，2-请求加群' not null,
 from_id bigint comment '发送者' not null,
 to_id bigint comment '接收者' not null,
 group_id bigint comment '分组id' not null,
 status int comment '状态 0-待处理 1-已同意 2-已拒绝' default 0,
 remark varchar(40) comment '附加信息',
 belong_to bigint comment '所属',
 unread int comment '未读标记',
 create_at timestamp default current_timestamp,
 expire bigint comment '失效天数' default 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8
;

