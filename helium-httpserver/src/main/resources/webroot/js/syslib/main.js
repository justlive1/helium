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

layui.define(['jquery', 'layer', 'layim'], function () {
  Win10.onReady(function () {
    Win10.setAnimated(['animated flip', 'animated bounceIn'], 0.01);
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
    Win10.setBgUrl(bgUrl);

    setTimeout(function () {
      Win10.newMsg('官方QQ', '1106088328')
    }, 2500);

    setTimeout(function () {
      Win10.newMsg('分布式任务调度', '登录用户名: frost, 登录密码: frost');
    }, 4000);
  });
  Win10.init();

  window.openIm = function () {
    var token = sessionStorage.getItem("token");
    var uid = sessionStorage.getItem("uid");
    if (token && uid) {
      if (window.Him) {
        initHim(token, uid);
      } else {
        window.initIM();
      }
    } else {
      openLoginPage();
    }
  };

  function openLoginPage() {
    sessionStorage.clear();
    Win10.openUrl('/login.html', 'IM', [['500px', '420px'], 'auto'], '0001');
  }

  function initHim(token, uid) {
    var him = new window.Him(token, uid);
    him.init();
    window.him = him;
  }

  function countMineUnreadNotifies() {
    layui.$.ajax({
      url: '/interface/friend/countMineUnreadNotifies',
      method: 'post',
      success: function (resp) {
        if (resp.success && resp.data > 0) {
          layui.layim.msgbox(resp.data);
        } else if (!resp.success) {
          layui.layer.msg(resp.message);
        }
      },
      error: function () {
        layui.layer.msg("获取未读通知失败！");
      }
    });
  }

  function initHimUi() {
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
          msgbox: '/interface/msgbox.html',
          find: '/interface/find.html',
          chatLog: '/interface/chatlog.html'
        });
        countMineUnreadNotifies();
      },
      error: function () {
        layui.layer.msg("服务器繁忙，请稍后重试！");
      }
    });
  }

  window.initIM = function () {
    layui.$.ajax({
      url: "/interface/him.js",
      dataType: "script",
      success: function () {
        initHim(sessionStorage.getItem("token"), sessionStorage.getItem("uid"));
        initHimUi();
      },
      error: function (rsp) {
        if (rsp.status === 401) {
          openLoginPage();
        } else {
          layui.layer.msg("服务器繁忙，请稍后重试！");
        }
      }
    });
  };

});


