/*
 * Copyright (C) 2018 justlive1
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
layui.define(function (exports) {

  var Win10_child = {
    _init: function () {
      this.Win10_parent = parent.layui.Win10;
    },
    close: function () {
      var index = parent.layui.layer.getFrameIndex(window.name);
      this.Win10_parent._closeWin(index);
    },
    newMsg: function (title, content, handle_click) {
      this.Win10_parent.newMsg(title, content, handle_click)
    },
    openUrl: function (url, title, max) {
      var click_lock_name = Math.random();
      this.Win10_parent._iframe_click_lock_children[click_lock_name] = true;
      var index = this.Win10_parent.openUrl(url, title, max);
      setTimeout(function () {
        delete this.Win10_parent._iframe_click_lock_children[click_lock_name];
      }, 1000);
      return index;
    }
  };

  Win10_child._init();
  exports('Win10_child', Win10_child);
});


