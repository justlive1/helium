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
layui.define(['layim', 'laypage', 'form'], function (exports) {

  var find = {

    findData: {
      totalNumber: 0,
      items: []
    },

    findFriends: function (title, pageIndex) {
      var params = {
        keyword: layui.$("#keyword").val(),
        pageIndex: pageIndex,
        pageSize: 8,
        type: layui.$("#type").val()
      };
      layui.$.ajax({
        url: "/interface/friend/find",
        method: 'post',
        data: params,
        cache: false,
        async: false,
        success: function (resp) {
          if (resp.success) {
            this.findData = resp.data;
            var html = layui.laytpl(layui.$("#find-view-tpl").html()).render({
              legend: title,
              type: layui.$("#type").val(),
              data: this.findData.items
            });
            layui.$('#find-result-view').html(html);
          } else {
            layui.layer.msg("查找失败！");
          }
        },
        error: function () {
          layui.layer.msg("服务器繁忙，请稍后重试！");
        }
      });
    },

    bindEvent: function () {
      var _this = this;
      //监听查找按钮
      layui.$("#find").click(function () {
        layui.laypage.render({
          elem: 'page-view',
          count: _this.findData.totalNumber,
          prev: '<i class="layui-icon">&#58970;</i>',
          next: '<i class="layui-icon">&#xe65b;</i>',
          layout: ['prev', 'next'],
          limit: _this.findData.pageSize,
          jump: function (obj) {
            _this.findFriends("查找结果", obj.curr);
          }
        });
        return false;
      });
      //监听新建群组按钮
      layui.$("#add-group").click(function () {
        parent.layui.layer.open({
          type: 2,
          title: '新建群组',
          closeBtn: 1,
          fixed: false,
          maxmin: true,
          shade: 0,
          offset: 'auto',
          area: ['370px', '410px'],
          content: ['/interface/createGroup.html', 'no'],
          end: function () {

          }
        });
        return false;
      });
      // 添加好友
      layui.$("#find-result-view").on('click', '.add', function () {
        var add = layui.$(this);
        var type = add.data("type");
        var username = add.data("name");
        var avatar = add.data("avatar");
        var uid = add.data("uid");

        if (type === 0) {
          layui.layim.add({
            type: 'friend',
            username: username,
            avatar: avatar,
            submit: function (group, remark, index) {
              layui.$.ajax({
                url: "/interface/friend/addFriend",
                method: 'post',
                data: {
                  groupId: group,
                  remark: remark,
                  to: uid
                },
                success: function (resp) {
                  layui.layer.close(index);
                  if (resp.success) {
                    layui.layer.msg("申请已发送，等待对方同意。");
                  } else {
                    layui.layer.msg(resp.message);
                  }
                },
                error: function () {
                  layui.layer.msg("服务器繁忙，请稍后重试！");
                }
              });
            }
          });
        } else {
          layui.layim.add({
            type: 'group',
            groupname: username,
            avatar: avatar,
            submit: function (group, remark, index) {
              layui.$.ajax({
                url: "/interface/friend/addFriendGroup",
                method: 'post',
                data: {
                  groupid: group,
                  remark: remark,
                  to: uid
                },
                success: function (resp) {
                  layui.layer.close(index);
                  if (resp.success) {
                    layui.layer.msg("申请已发送，等待管理员同意。");
                  } else {
                    layui.layer.msg(resp.message);
                  }
                },
                error: function () {
                  layui.layer.msg("服务器繁忙，请稍后重试！");
                }
              });
            }
          });
        }
      });
    },

    _init: function () {
      this.bindEvent();
      this.findFriends("好友推荐", 1);
    }

  }

  find._init();
  exports('find', find);

});
