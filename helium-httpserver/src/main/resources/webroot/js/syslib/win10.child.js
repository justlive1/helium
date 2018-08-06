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

window.Win10_parent = parent.Win10;
window.Win10_child = {
  close: function () {
    var index = parent.layer.getFrameIndex(window.name);
    Win10_parent._closeWin(index);
  },
  newMsg: function (title, content, handle_click) {
    Win10_parent.newMsg(title, content, handle_click)
  },
  openUrl: function (url, title, max) {
    var click_lock_name = Math.random();
    Win10_parent._iframe_click_lock_children[click_lock_name] = true;
    var index = Win10_parent.openUrl(url, title, max);
    setTimeout(function () {
      delete Win10_parent._iframe_click_lock_children[click_lock_name];
    }, 1000);
    return index;
  }
};


