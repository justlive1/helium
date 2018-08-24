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
layui.define("jquery", function () {

  var $ = layui.$;

  var pluginName = 'contextify',
    defaults = {
      items: [],
      action: "contextmenu",
      menuId: "contextify-menu",
      menuClass: "dropdown-menu",
      headerClass: "dropdown-header",
      dividerClass: "dropdown-divider",
      before: false
    },
    contextifyId = 0,
    $window = $(window);

  function Plugin(element, options) {
    this.element = element;
    this.options = $.extend({}, defaults, options);
    this.init();
  }

  Plugin.prototype.init = function () {
    var options = $.extend({}, this.options, $(this.element).data());
    options.id = contextifyId;

    var _element = this.element;

    $(this.element).attr('data-contextify-id', options.id).on('contextmenu',
      function (e) {
        e.preventDefault();

        // run before
        if (typeof(options.before) === 'function') {
          options.before(this, options);
        }

        var menu = $('<ul class="' + options.menuClass + '" role="menu" id="'
          + options.menuId + '" data-contextify-id="' + options.id + '"></ul>');

        menu.data(options);

        var l = options.items.length;
        var i;

        for (i = 0; i < l; i++) {
          var item = options.items[i];
          var el = $('<li></li>');

          if (item['divider']) {
            el.addClass(options.dividerClass);
          } else if (item.header) {
            el.addClass(options.headerClass);
            el.html(item.header);
          } else {
            if (item['icon']) {
              el.append(item['icon']);
            }

            el.append('<a></a>');
            var a = el.find('a');

            if (item.href) {
              a.attr('href', item.href);
            }
            if (item.onclick) {
              (function (func) {
                a.on('click', function () {
                  func(_element);
                });
              })(item.onclick);
              a.css('cursor', 'pointer');
            }
            if (item.data) {
              for (var data in item.data) {
                menu.attr('data-' + data, item.data.data);
              }
              a.data(item.data);
            }
            a.html(item.text);
          }

          menu.append(el);
        }

        var currentMenu = $("#" + options.menuId);

        if (currentMenu.length > 0) {
          if (currentMenu !== menu) {
            currentMenu.replaceWith(menu);
          }
        }
        else {
          $('body').append(menu);
        }

        var windowWidth = $window.width(),
          windowHeight = $window.height(),
          menuWidth = menu.outerWidth(),
          menuHeight = menu.outerHeight(),
          x = (menuWidth + e.clientX < windowWidth) ? e.clientX : windowWidth
            - menuWidth,
          y = (menuHeight + e.clientY < windowHeight) ? e.clientY : windowHeight
            - menuHeight;

        menu.css('top', y).css('left', x).css('position', 'fixed').show();
      })
    .parents().on('mouseup', function () {
      $("#" + options.menuId).hide();
    });

    $window.on('scroll', function () {
      $("#" + options.menuId).hide();
    });

    contextifyId++;
  };

  Plugin.prototype.destroy = function () {
    var el = $(this.element), options = $.extend({}, this.options, el.data());

    el.removeAttr('data-contextify-id').off('contextmenu').parents().off(
      'mouseup', function () {
        $("#" + options.menuId).hide();
      });

    $window.off('scroll', function () {
      $("#" + options.menuId).hide();
    });

    $("#" + options.menuId).remove();
  };

  $.fn[pluginName] = function (options) {
    return this.each(function () {
      if ($.data(this, 'plugin_' + pluginName)
        && Object.prototype.toString.call(options) === '[object String]') {
        $.data(this, 'plugin_' + pluginName)[options]();
      }
      else if (!$.data(this, 'plugin_' + pluginName)) {
        $.data(this, 'plugin_' + pluginName, new Plugin(this, options));
      }
    });
  };

});
