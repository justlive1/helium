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
layui.define(['layim', 'layer', 'flow', 'util'], function (exports) {

  var msgbox = {

    cacheData: {},

    load: function () {
      layui.$.ajax({
        url: '/interface/friend/findMineNotifies',
        method: 'post',
        success: function (resp) {
          if (resp.success) {
            msgbox.cacheData = {};
            layui.$.each(resp.data, function (index, value) {
              value.timeAgo = layui.util.timeAgo(new Date(value['createAt']),
                true);
              msgbox.cacheData[value.id] = value;
            });
            var tpl = layui.$("#msgbox-tpl").html();
            layui.laytpl(tpl).render({notifies: resp.data}, function (html) {
              layui.$("#msgbox-view").html(html);
            });
          } else {
            layui.layer.msg(resp.message);
          }
        },
        error: function () {
          layui.layer.msg("获取通知失败");
        }
      });
    },

    bindEvent: function () {
      layui.$("#msgbox-view").on('click', '.msgbox-type-agree', function () {
        var id = layui.$(this).parents('li').data('id');
        var data = msgbox.cacheData[id];
        var _this = layui.$(this);
        if (data.type === 0) {
          //选择分组
          parent.layui.layim.setFriendGroup({
            type: 'friend',
            username: data.from.username,
            avatar: data.from.avatar,
            group: parent.layui.layim.cache().friend,
            submit: function (group, index) {
              layui.$.ajax({
                url: '/interface/friend/agreeAddFriend',
                method: 'post',
                data: {
                  id: id,
                  groupId: group
                },
                success: function (resp) {
                  if (resp.success) {
                    //将好友追加到主面板
                    parent.layui.layim.addList({
                      type: 'friend',
                      avatar: data.from.avatar,
                      username: data.from.username,
                      groupid: group,
                      id: data.from.id,
                      sign: data.from.sign
                    });
                    parent.layer.close(index);
                    _this.parent().html("已同意");
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
      }).on('click', '.msgbox-type-refuse', function () {
        var id = layui.$(this).parents('li').data('id');
        var _this = layui.$(this);
        layer.confirm('确定拒绝吗？', function (index) {
          layui.$.ajax({
            url: '/interface/friend/refuseAddFriend',
            method: 'post',
            data: {
              id: id
            },
            success: function (resp) {
              if (resp.success) {
                _this.parent().html("已拒绝");
                layui.layer.close(index);
              } else {
                layui.layer.msg(resp.message);
              }
            },
            error: function () {
              layui.layer.msg("服务器繁忙，请稍后重试！");
            }
          });
        });
      });
    },

    _init: function () {
      this.load();
      this.bindEvent();
    }
  };

  msgbox._init();
  exports('msgbox', msgbox);
});
