/*!
 * jquery.search.js
 * @author ydr.me
 * @version 1.1
 * 2014年7月1日15:25:59
 */

//去掉require.js的模块化方式，改为直接扩展jquery   BigQ 2015-07-03 21:15



(function($) {
    'use strict';

    var udf,
        defaults = {
            // 是否严格模式，默认true
            // 严格模式下，将在读取后、写入前分别进行encodeURIComponent、decodeURIComponent操作
            isStrict: !0,
            // 传入search部分，为空默认为window.location.search
            search: ''
        },
        regSearchReplace = /^[^?]*\?/;

    $.search = function(settings) {
        if ($.type(settings) === 'string') settings = {
            search: settings
        };
        return new Search($.extend({}, defaults, settings))._parse();
    };
    $.search.defaults = defaults;






    function Search(options) {
        options.search = options.search || location.search;
        this.options = options;
    }

    Search.prototype = {
        /**
         * 解析search部分为键值对
         * @return this
         * @version 1.0
         * 2014年7月1日15:54:34
         */
        _parse: function() {
            var that = this,
                options = that.options,
                arrayKeys = {},
                a, ret = {};
            that._querystring = options.search.replace(regSearchReplace, '');

            // a=1&a=2&a=3&a=4&b=5&c=6
            a = that._querystring.split('&');
            $.each(a, function(index, part) {
                var pos = part.indexOf('='),
                    key = part.slice(0, pos),
                    val = part.slice(pos + 1);

                if (options.isStrict) val = decodeURIComponent(val);
                if (key) {
                    // 已经存在的值，说明是数组
                    if (ret[key]) {
                        // 已经被判断了
                        if (arrayKeys[key]) {
                            ret[key].push(val);
                        } else {
                            arrayKeys[key] = !0;
                            ret[key] = [ret[key], val];
                        }
                    } else {
                        ret[key] = val;
                    }
                }
            });

            that._result = ret;
            return that;
        },





        /**
         * 字符串化，同时改变浏览器的location.search部分
         * @version 1.0
         * 2014年7月1日16:37:27
         */
        _stringify: function() {
            var key, a = [],
                tmp,
                val, that = this,
                options = that.options;

            for (key in that._result) {
                val = that._result[key];
                if (options.isStrict) key = encodeURIComponent(key);

                // 数组
                if ($.isArray(val)) {
                    tmp = _queryInArray(key, val, options.isStrict);
                    if (tmp) a.push(tmp);
                }
                // 字符串
                else {
                    if (options.isStrict) val = encodeURIComponent(val);
                    a.push(key + '=' + val);
                }
            }
            that._search = a.join('&');
            location.search = '?' + that._search;

            return that;
        },






        /**
         * 获取1个或多个或全部search部分值
         * @param  {String/Array} key 单个或多个键
         * @return {String}           单个值或键值对
         * @version 1.0
         * 2014年7月1日15:55:57
         */
        get: function(key) {
            if (key === udf) return this._result;

            var isMulti = $.isArray(key),
                keys = isMulti ? key : [key],
                ret = {},
                that = this,
                _result = that._result;

            $.each(keys, function(index, key) {
                ret[key] = _result[key];
            });

            return isMulti ? ret : ret[key];
        },






        /**
         * 设置1个或多个search值，并改变window.location.search
         * @param {String/Object} key 键或键值对
         * @param {String/Array}  val 值
         * @version 1.0
         * 2014年7月1日16:38:45
         */
        set: function(key, val) {
            var map = {},
                hasChange, that = this;

            if (val === udf) {
                map = key;
            } else {
                map[key] = val;
            }

            $.each(map, function(k, v) {
                var original = that._result[k];
                if ($.isArray(v)) {
                    if (!_isSameArray(v, original)) {
                        hasChange = 1;
                        that._result[k] = v;
                    }
                } else {
                    v += '';
                    if (original !== v) {
                        hasChange = 1;
                        that._result[k] = v;
                    }
                }
            });

            if (hasChange) that._stringify();
            return that;
        },




        /**
         * 支持push、pop、unshift、shift、concat、slice、splice等数组原型方法
         * @param  {String} method 方法名称
         * @param  {String} key    操作键
         * @param  {*}      val    操作值
         * @return undefined
         * @version 1.0
         * 2014年7月2日13:49:25
         */
        _transport: function(method, key, val /**/ ) {
            var that = this,
                a = that._result[key],
                args = [].slice.call(arguments, 2);

            if (a === udf) {
                that._result[key] = [];
                a = [];
            } else if (!$.isArray(a)) {
                that._result[key] = [a];
                a = [a];
            } else {
                a = [];
                $.each(that._result[key], function(index, val) {
                    a.push(val);
                });
            }

            if (method === 'concat') {
                that._result[key] = that._result[key].concat(val);
            } else {
                that._result[key][method].apply(that._result[key], args);
            }

            if (!_isSameArray(a, that._result[key])) that._stringify();

            return that;
        },









        /**
         * 移除1个或多个或全部search部分值
         * @param  {String/Array} key 单个或多个键
         * @version 1.0
         * 2014年7月1日15:55:57
         */
        remove: function(key) {
            var that = this;

            if (key === udf) {
                if ($.isEmptyObject(that._result)) return that;
                that._result = {};
                return that._stringify();
            }

            var isMulti = $.isArray(key),
                keys = isMulti ? key : [key],
                _result = that._result,
                hasRemove;

            $.each(keys, function(index, key) {
                delete(_result[key]);
                hasRemove = 1;
            });

            if (hasRemove) that._stringify();
            return that;
        }
    };

    $.each(['push', 'pop', 'unshift', 'shift', 'splice', 'concat'], function(key, method) {
        Search.prototype[method] = function() {
            var args = [].slice.call(arguments, 0);
            args.unshift(method);
            return this._transport.apply(this, args);
        };
    });




    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////[ private API ]////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 判断两个一维数组的值是否全部相同，顺序无关
     * @param  {Array}  arr1  被匹配数组
     * @param  {Array}  arr2  匹配数组
     * @return {Boolean}
     * @version 1.0
     * 2014年7月1日16:23:42
     */

    function _isSameArray(arr1, arr2) {
        if (arr2 === udf || arr1.length !== arr2.length) return !1;

        if (arr1.length === arr2.length === 0) return !0;

        var i = 0,
            j = arr1.length;
        for (; i < j; i++) {
            if (!~$.inArray(arr1[i], arr2)) return !1;
        }

        return !0;
    }



    /**
     * query为数组解析为字符串
     * @param  {String} key   键
     * @param  {Array} array  数组值
     * @return {String}       解析后的字符串
     * @version 1.0
     * 2014年7月1日16:34:35
     */

    function _queryInArray(key, array, isStrict) {
        if (!array.length) return;

        var tmp = [];
        $.each(array, function(index, v) {
            if (isStrict) v = encodeURIComponent(v);
            tmp.push(key + '=' + v);
        });
        return tmp.join('&');
    }
})(jQuery);
