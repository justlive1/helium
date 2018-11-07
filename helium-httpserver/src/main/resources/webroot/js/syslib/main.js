/*
 *  Copyright (C) 2018 justlive1
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

layui.define(['jquery', 'layer', 'layim'], function (exports) {

  var main = {

    _init: function () {
      layui.Win10.onReady(function () {
        layui.Win10.setAnimated(['animated flip', 'animated bounceIn'], 0.01);
        var bgUrl = {
          main: '/static/image/main.jpg',
          mobile: '/static/image/mobile.jpg'
        };
        if (window.localStorage) {
          var url = window.localStorage.getItem("bgUrl");
          if (url != null) {
            bgUrl.main = url;
          }
        }
        layui.Win10.setBgUrl(bgUrl);

        setTimeout(function () {
          layui.Win10.newMsg('官方QQ', '1106088328')
        }, 2500);

        setTimeout(function () {
          layui.Win10.newMsg('分布式任务调度', '登录用户名: frost, 登录密码: frost');
        }, 4000);
      });
      layui.Win10.init();
    },

    openHimView: function () {
      var token = sessionStorage.getItem("token");
      var uid = sessionStorage.getItem("uid");
      if (token && uid) {
        if (window.Him) {
          this.initHim(token, uid);
        } else {
          this.initHimWithJs();
        }
      } else {
        this.openLoginPage();
      }
    },

    openLoginPage: function () {
      sessionStorage.clear();
      layui.Win10.openUrl('/login.html', 'IM', [['500px', '420px'], 'auto'],
        '0001');
    },

    initHim: function (token, uid) {
      if (this.him) {
        this.him.close();
      }
      var him = new window.Him(token, uid);
      him.init();
      this.him = him;
    },

    countMineUnreadNotifies: function () {
      layui.$.ajax({
        url: '/interface/friend/countMineUnreadNotifies',
        method: 'post',
        success: function (resp) {
          if (resp.success && resp.data > 0) {
            main.addMsgboxCount(resp.data);
          } else if (!resp.success) {
            layui.layer.msg(resp.message);
          }
        },
        error: function () {
          layui.layer.msg("获取未读通知失败！");
        }
      });
    },

    initHimUi: function () {
      layui.$.ajax({
        url: "/interface/friend/mine",
        type: 'post',
        success: function (resp) {
          if (!resp.success) {
            layui.layer.msg(resp.message);
            return;
          }
          var friendCache = {};
          layui.$('body').data('friends', friendCache);
          layui.$.each(resp.data.friend, function (i, g) {
            layui.$.each(g.list, function (ii, t) {
              t.name = t.username;
              if (t.nickname) {
                t.name = t.nickname + "(" + t.username + ")";
              }
              friendCache[t.id] = t;
            });
          });
          layui.layim.config({
            //初始化接口
            init: resp.data,
            //查看群员接口
            members: {
              url: '',
              data: {}
            },
            isAudio: false,
            isVideo: false,
            title: 'WebIM',
            isfriend: true,
            isgroup: true,
            initSkin: '2.jpg',
            min: false,
            notice: false,
            msgbox: '/interface/msgbox.html',
            find: '/interface/find.html',
            chatLog: '/interface/chatlog.html',
            avatar: 'avatar.html'
          });
          main.countMineUnreadNotifies();
          main.initHimEvent();
        },
        error: function () {
          layui.layer.msg("服务器繁忙，请稍后重试！");
        }
      });
    },

    initHimEvent: function () {
      // 右键菜单
      main.bindFriendGroupContentify();
      main.bindFriendContentify();
      // 监听修改签名
      layui.layim.on('sign', function (value) {
        layui.$.ajax({
          url: "/interface/user/updateSignature",
          method: 'post',
          data: {
            signature: value
          },
          success: function (resp) {
            if (resp.success) {
              layui.layer.msg("签名修改成功！")
            } else {
              layui.layer.msg(resp.message)
            }
          },
          error: function () {
            layui.layer.msg("服务器繁忙，请稍后重试！");
          }
        });
      });

      layui.layim.on('chatChange', function (res) {
        var type = res.data.type;
        if (type === 'friend') {
          layui.$.get("/interface/user/online", {id: res.data.id},
            function (resp) {
              if (resp.success) {
                layui.layim.changeChatStatus(resp.data.status);
              } else {
                layui.layer.msg(resp.message);
              }
            });
        } else if (type === 'group') {
        }
      });
      // 监听发送的消息
      layui.layim.on('sendMessage', function (res) {
        main.him.sendMsgToFriend(res.to.id, res.mine.content,
          function (err, reply) {
            // TODO send failed
            console.log(err);
            console.log(reply)
          });
      });

      // 监听接收消息
      main.him.addReceiveMsgHandler(main.receiveMsgHandler);
      // 监听接收通知
      main.him.addReceiveNotifyHandler(main.receiveNotifyHandler);
      // 离线消息处理
      main.him.addOfflineMsgHandler(main.offlineMsgHandler);
    },

    initHimWithJs: function () {
      layui.$.ajax({
        url: "/interface/him.js",
        dataType: "script",
        success: function () {
          main.initHim(sessionStorage.getItem("token"),
            sessionStorage.getItem("uid"));
          main.initHimUi();
        },
        error: function (rsp) {
          if (rsp.status === 401) {
            main.openLoginPage();
          } else {
            layui.layer.msg("服务器繁忙，请稍后重试！");
          }
        }
      });
    },

    receiveMsgHandler: function (data) {
      var friend = layui.$('body').data('friends')[data.body.from];
      layui.layim.getMessage({
        name: friend.name,
        username: friend.username,
        avatar: friend.avatar,
        id: friend.id,
        type: "friend",
        content: data.body.data,
        cid: data.headers.mid,
        mine: false,
        fromid: friend.id,
        timestamp: Number(data.headers.timestamp)
      });
      main.recordReceiveMsgId(data.body.to, data.headers.mid);
    },

    recordReceiveMsgId: function (uid, mid) {
      var msgids = localStorage.getItem("msgids" + uid);
      if (!msgids) {
        msgids = [];
      } else {
        msgids = JSON.parse(msgids);
      }
      if (msgids.indexOf(mid) > -1) {
        return false;
      }
      msgids.push(mid);
      localStorage.setItem("msgids" + uid, JSON.stringify(msgids));
      return true;
    },

    receiveNotifyHandler: function (data) {
      console.log(data)
      main.addMsgboxCount(1);
      if (data.body.type === -1) {
        main.systemNotify(data);
      } else if (data.body.type === 0) {
        main.addFriendNotify(data);
      }
    },

    systemNotify: function (data) {
      var msg = data.body.from.username + (data.body.status === 1
        ? ' 同意 ' : ' 拒绝 ') + "了你的申请";
      layui.layer.msg(msg);
      if (data.body.status === 1) {
        var friend = data.body.from;
        friend.groupid = data.body.groupId;
        friend.type = 'friend';
        layui.$('body').data("friends")[friend.id] = friend;
        layui.layim.addList(friend);
      }
    },

    addFriendNotify: function (data) {
      var msg = data.body.from.username + " 申请添加你为好友";
      layui.layer.msg(msg);
    },

    addMsgboxCount: function (count) {
      if (layui.layim.cache().msgbox) {
        count += layui.layim.cache().msgbox;
      }
      layui.layim.msgbox(count);
      layui.layim.cache().msgbox = count;
    },

    addFriendGroup: function (name) {
      layui.$.ajax({
        url: '/interface/friend/addFriendGroup',
        method: 'post',
        data: {name: name},
        success: function (resp) {
          if (resp.success) {
            layui.layer.msg("分组创建成功");
            layui.layim.cache().friend.push(
              {groupname: name, id: resp.data, list: []});
            layui.$('.layim-list-friend').append(
              '<li><h5 layim-event="spread" lay-type="false" data-id="'
              + resp.data + '"><i class="layui-icon">&#xe602;</i><span>' + name
              + '</span><em>(<cite class="layim-count"> 0</cite>)</em></h5><ul class="layui-layim-list"><li class="layim-null">该分组下暂无好友</li></ul></li>');
            main.bindFriendGroupContentify();
          } else {
            layui.layer.msg(resp.message);
          }
        },
        error: function () {
          layui.layer.msg("服务器繁忙，请稍后重试！");
        }
      });
    },

    updateFriendGroup: function (name, id, ele) {
      layui.$.ajax({
        url: '/interface/friend/updateFriendGroupName',
        method: 'post',
        data: {id: id, name: name},
        success: function (resp) {
          if (resp.success) {
            layui.layer.msg("分组修改成功");
            layui.$(ele).data('name', name).find('span').text(name);
          } else {
            layui.layer.msg(resp.message);
          }
        },
        error: function () {
          layui.layer.msg("服务器繁忙，请稍后重试！");
        }
      });
    },

    delFriendGroup: function (id, ele) {
      layui.$.ajax({
        url: '/interface/friend/delFriendGroup',
        method: 'post',
        data: {id: id},
        success: function (resp) {
          if (resp.success) {
            var friends = layui.layim.cache().friend;
            for (var i = 0; i < friends.length; i++) {
              if (friends[i].id === id) {
                friends.splice(i, 1);
                break;
              }
            }
            layui.$(ele).parent().remove();
            layui.layer.msg("分组删除成功");
          } else {
            layui.layer.msg(resp.message);
          }
        },
        error: function () {
          layui.layer.msg("服务器繁忙，请稍后重试！");
        }
      });
    },

    bindFriendGroupContentify: function () {
      layui.$('.layim-list-friend>li>h5').contextify({
        items: [
          {
            text: '新建分组',
            href: 'javascript:void(0);',
            icon: '<i class="layui-icon">&#xe654</i>',
            onclick: function () {
              layui.layer.prompt({
                title: '新建分组',
                formType: 0,
                maxlength: 7,
                shade: 0,
                btnAlign: 'c'
              }, function (value, index) {
                main.addFriendGroup(value);
                layui.layer.close(index);
              });
            }
          },
          {
            text: '修改分组',
            href: 'javascript:void(0);',
            icon: '<i class="layui-icon">&#xe642</i>',
            onclick: function (ele) {
              var groupId = layui.$(ele).data('id');
              layui.layer.prompt({
                title: '修改分组',
                formType: 0,
                maxlength: 7,
                shade: 0,
                btnAlign: 'c'
              }, function (value, index) {
                main.updateFriendGroup(value, groupId, ele);
                layui.layer.close(index);
              });
            }
          },
          {
            text: '删除分组',
            href: 'javascript:void(0);',
            icon: '<i class="layui-icon">&#x1006</i>',
            onclick: function (ele) {
              var groupId = layui.$(ele).data('id');
              layui.layer.confirm(
                '确定要删除分组 ' + layui.$(ele).data('name') + ' 吗?',
                function (index) {
                  main.delFriendGroup(groupId, ele);
                  layer.close(index);
                });
            }
          }
        ]
      });
    },

    bindFriendContentify: function () {
      layui.$('.layim-list-friend>li>ul>li:not(".layim-null")').contextify({
        items: [
          {
            text: '修改备注',
            href: 'javascript:void(0);',
            icon: '<i class="layui-icon">&#xe642</i>',
            onclick: function (ele) {
              var friendId = layui.$(ele).data('id');
              layui.layer.prompt({
                title: '修改备注',
                formType: 0,
                maxlength: 7,
                shade: 0,
                btnAlign: 'c'
              }, function (value, index) {
                main.updateFriendMemo(value, friendId, ele);
                layui.layer.close(index);
              });
            }
          },
          {
            text: '删除好友',
            href: 'javascript:void(0);',
            icon: '<i class="layui-icon">&#x1006</i>',
            onclick: function (ele) {
              var id = layui.$(ele).data('id');
              layui.layer.confirm(
                '确定要删除好友 ' + layui.$(ele).data('name') + ' 吗?',
                function (index) {
                  main.delFriend(id);
                  layer.close(index);
                });
            }
          }
        ]
      });
    },

    delFriend: function (id) {
      layui.$.ajax({
        url: '/interface/friend/delFriend',
        method: 'post',
        data: {id: id},
        success: function (resp) {
          if (resp.success) {
            layui.layim.removeList({type: 'friend', id: id});
            layui.layer.msg("删除好友成功");
          } else {
            layui.layer.msg(resp.message);
          }
        },
        error: function () {
          layui.layer.msg("服务器繁忙，请稍后重试！");
        }
      });
    },

    updateFriendMemo: function (memo, friendId, ele) {
      layui.$.ajax({
        url: '/interface/friend/updateFriendMemo',
        method: 'post',
        data: {friendId: friendId, memo: memo},
        success: function (resp) {
          if (resp.success) {
            layui.$(ele).find('span').text(
              memo + "(" + layui.$(ele).data("name") + ")");
            var friend = layui.layim.cache().friend[layui.$(
              ele).parent().siblings('h5').data("index")].list[layui.$(
              ele).data('index')];
            friend.name = memo + "(" + friend.username + ")";
            layui.layer.msg("修改备注成功");
          } else {
            layui.layer.msg(resp.message);
          }
        },
        error: function () {
          layui.layer.msg("服务器繁忙，请稍后重试！");
        }
      });
    },

    offlineMsgHandler: function (err, reply) {
      if (reply && reply.body && reply.body.success) {
        layui.$.each(reply.body.data, function (i, t) {
          if (main.recordReceiveMsgId(t.toId, t.id)) {
            var friend = layui.$("body").data("friends")[t.fromId];
            layui.layim.getMessage({
              name: friend.name,
              username: friend.username,
              avatar: friend.avatar,
              id: friend.id,
              type: "friend",
              content: t.content,
              cid: t.id,
              mine: false,
              fromid: friend.id,
              timestamp: t.timestamp
            });
          }
        });
      }
    },

  };

  main._init();
  exports("main", main);

});


