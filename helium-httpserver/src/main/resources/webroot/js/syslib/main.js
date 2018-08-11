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
            copyright: true,
            msgbox: '/interface/msgbox.html',
            find: '/interface/find.html',
            chatLog: '/interface/chatlog.html'
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
      // 监听修改签名
      layui.layim.on('sign', function (value) {
      });

      // 监听发送的消息
      layui.layim.on('sendMessage', function (res) {
        var msg = {
          from: {
            id: res.mine.id,
            username: res.mine.username,
            avatar: res.mine.avatar
          },
          to: {
            id: res.to.id,
            username: res.to.username,
            avatar: res.to.avatar
          },
          content: res.mine.content
        };
        main.him.sendMsgToFriend(res.to.id, msg, function (err, reply) {
          // TODO send failed
          console.log(err);
          console.log(reply)
        });
      });

      // 监听接收消息
      main.him.addReceiveMsgHandler(main.receiveMsgHandler);
      // 监听接收通知
      main.him.addReceiveNotifyHandler(main.receiveNotifyHandler);
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
      layui.layim.getMessage({
        username: data.body.data.from.username,
        avatar: data.body.data.from.avatar,
        id: data.body.data.from.id,
        type: "friend",
        content: data.body.data.content,
        cid: 0,
        mine: false,
        fromid: data.body.data.from.id,
        timestamp: Number(data.headers.timestamp)
      });
    },

    receiveNotifyHandler: function (data) {
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
    }
  };

  main._init();
  exports("main", main);

});


