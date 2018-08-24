/*
 *  Copyright (C) 2018 justlive1
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
!function (factory) {
  if (typeof require === 'function' && typeof module !== 'undefined') {
    // CommonJS loader
    var EventBus = require('vertx3-eventbus-client');
    if (!EventBus) {
      throw new Error(
        'him.js requires vertx3-eventbus-client, see https://vertx.io');
    }
    factory(EventBus);
  } else if (typeof define === 'function' && define.amd) {
    // AMD loader
    define('him', ['vertx-eventbus'], factory);
  } else {
    // plain old include
    if (typeof this.EventBus === 'undefined') {
      throw new Error(
        'him.js requires vertx-eventbus, see https://vertx.io');
    }
    window.Him = factory(this.EventBus);
  }
}(function (EventBus) {

  var Him = function (token, id) {
    var _token = token;
    var _id = id;
    var _url = [[${webImConf.webImUrl}]];
    var _this = this;

    this.validate = function () {
      return _token && _id && _url;
    };

    this.addReceiveMsgHandler = function (callback) {
      this.receiveMsgHandler = callback;
    };

    this.addReceiveNotifyHandler = function (callback) {
      this.receiveNotifyHandler = callback;
    };

    this.addErrorHandler = function (callback) {
      this.errorHandler = callback;
    };

    this.addClosedHandler = function (callback) {
      this.closedHander = callback;
    };

    this.addOfflineMsgHandler = function (callback) {
      this.offlineMsgHandler = callback;
    };

    this.init = function () {
      if (this.validate()) {
        this._createEventBus();
      } else {
        layui.layer.alert("Him未初始化");
      }
    };

    this.close = function () {
      if (this.eventBus && this.eventBus.close) {
        this.eventBus.close();
      }
    };

    this._createEventBus = function () {
      this.close();
      var eb = new EventBus(_url + "?_token=" + _token, {
        vertxbus_ping_interval: 5000,
        vertxbus_reconnect_attempts_max: 10,
        vertxbus_reconnect_delay_min: 1000,
        vertxbus_reconnect_delay_max: 5000,
        vertxbus_reconnect_exponent: 2,
        vertxbus_randomization_factor: 0.5
      });

      eb.enableReconnect(true);
      eb.onopen = function () {
        // 接收用户聊天
        eb.registerHandler([[${msgUserToUser}]] + _id,
          function (error, message) {
            if (_this.receiveMsgHandler && typeof _this.receiveMsgHandler
              === 'function') {
              _this.receiveMsgHandler.call(window, message);
            } else {
              console.log(
                'no handler for this received  message: ' + JSON.stringify(
                message));
            }
          });
        // 接收服务器通知
        eb.registerHandler([[${notifyServerToUser}]] + _id,
          function (error, message) {
            if (_this.receiveNotifyHandler && typeof _this.receiveNotifyHandler
              === 'function') {
              _this.receiveNotifyHandler.call(window, message);
            } else {
              console.log(
                'no handler for this received  notify: ' + JSON.stringify(
                message));
            }
          });
        _this.eventBus = eb;
        _this.sendToServer('M_200', {}, _this.offlineMsgHandler);
      };

      eb.onerror = function (err) {
        if (_this.errorHandler && typeof _this.errorHandler === 'function') {
          _this.errorHandler.call(window, err);
        } else {
          console.log('no handler for this error; ' + err);
        }
      };

      eb.onclose = function (err) {
        if (eb.reconnectAttempts >= eb.maxReconnectAttempts
          && _this.closedHander && typeof _this.closedHander === 'function') {
          _this.closedHander.call(window, err);
        } else {
          console.log(err);
        }
      };
    };

    this._check = function () {
      if (this.eventBus == null || this.eventBus.state !== EventBus.OPEN) {
        layui.layer.alert("IM未成功连接到服务器！");
        return false;
      }
      return true;
    };

    this.sendToServer = function (code, data, callback) {
      if (this._check()) {
        this.eventBus.send([[${userToServer}]],
          {code: code, data: data},
          function (err, reply) {
            if (callback && typeof callback === 'function') {
              callback.call(window, err, reply);
            }
          });
      }
    };

    this.sendMsgToFriend = function (id, data, callback) {
      if (this._check()) {
        this.eventBus.send([[${msgUserToUser}]] + id,
          {code: 'M_300', from: _id, to: id, data: data},
          function (err, reply) {
            if (callback && typeof callback === 'function') {
              callback.call(window, err, reply);
            }
          });
      }
    }

  };

  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = Him;
    } else {
      exports.Him = Him;
    }
  } else {
    return Him;
  }

});
