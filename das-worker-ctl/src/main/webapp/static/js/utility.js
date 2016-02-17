//工具类大全
utility = {

    //显示操作成功tip（屏幕右上角，特定于gebo模板，并引入sticky组件）
    showSuccess: function (msg) {
        $.sticky(msg, {autoclose: 2000, position: "top-right", type: "st-success", duplicates: true});
    },

    showError: function (msg) {
        $.sticky(msg, {autoclose: 2000, position: "top-right", type: "st-error", duplicates: true});
    },

    //重置表单内容和验证样式（特定于gebo模板）
    resetForm: function (selector) {
        $(selector).find('input:text, input:password, input:file, input, select, textarea')
            .val('').closest('div').removeClass("f_error").find("label").remove();
        $(selector).find('input:radio, input:checkbox').removeAttr('checked').removeAttr('selected');
    },

    //重置验证样式（特定于gebo模板）
    resetValidation: function (selector) {
        $(selector).find('input:text, input:password, input:file, input, select, textarea')
            .closest('div').removeClass("f_error").find("label").remove();
    },

    //注册设置高度事件（如需要根据窗口大小变化自动调整，则调用这个）
    autoFit: function (objId, subHight) {
        if (window.addEventListener) {  // firefox  , w3c
            window.addEventListener("load", function () {
                setHeight(objId, subHight)
            });
            window.addEventListener("resize", function () {
                setHeight(objId, subHight)
            });
        }
        else if (window.attachEvent) {  //ie
            window.attachEvent("onload", function () {
                setHeight(objId, subHight);
            }, false);
            window.attachEvent("onresize", function () {
                setHeight(objId, subHight);
            }, false);
        }
    },

    //设置元素高度，subHeight参数为 "元素底部" 距离 "页面底部" 的高度
    setHeight: function (objId, subHight) {
        //debugger;
        var elem = document.getElementById(objId);
        var elemTop = elem.offsetTop;
        var e = elem;
        while (e = e.offsetParent) {
            elemTop += e.offsetTop;
        }

        var wHeight = (window.innerHeight || (window.document.documentElement.clientHeight || window.document.body.clientHeight));
        var newHeight = wHeight - subHight - elemTop;

        //以防高度结算结果为负数时报错
        if (newHeight > 10) {
            elem.style.height = newHeight + "px";
        }
    },

    //绑定收缩控制（objId为触发控件的id，collapse指定默认是否收缩，所有标记ref属性的元素为受控元素）
    bindCollapse: function (objId, visible) {
        if (visible == undefined) {
            visible = false;
        }
        var obj = $("#" + objId);
        var ref = $("[ref='" + objId + "']");

        obj.css("cursor", "pointer");

        if (visible == false) {
            ref.css("display", "none");
            obj.attr("title", "展开选项");
        }
        else {
            obj.attr("title", "收起选项");
        }

        obj.click(function () {
            ref.toggle();
            if (ref.css("display") == "none") {
                obj.attr("title", "展开选项");
            }
            else {
                obj.attr("title", "收起选项");
            }
        });
    },

    //根据下拉框的选择，决定元素的显示，ojbId为下拉框的ID，控制方式为受控元素设置 ref属性=objId，showValue = 下拉框选中时要显示的值，可以为多个值（多个下拉选项对应一个显示元素）如 showValue="1,2,3,5"
    bindSelectCollapse: function (objId) {
        var ddl = $("#" + objId);
        var fun = function (sender, eventArgs) {
            var value = ddl.val();
            var refs = $("[ref='" + objId + "']");
            refs.css("display", "none");
            refs.each(function () {
                if ($(this).attr("showValue").indexOf(value) != -1) {
                    $(this).css("display", "");
                }
            });
        }
        ddl.change(fun);
        fun(ddl);
    },

    //判断是否是未定义
    isUndefined: function (variable) {
        return typeof variable == 'undefined' ? true : false;
    },

    //格式化数字，保留N位小数(参数列表:dight----原始数字, how----保留的小数位数)
    formatDigit: function (dight, how) {
        dight = Math.round(dight * Math.pow(10, how)) / Math.pow(10, how);
        var str = dight.toString();
        if (str.indexOf(".") == -1 && how != 0) {
            str += ".";
            for (var i = 1; i <= how; i++) {
                str += "0";
            }
        }
        else {
            if (str.split(".")[1] == null) {
                for (var i = 1; i <= how; i++) {
                    str += "0";
                }
            }
            else {
                var length = str.split(".")[1].length;
                for (var i = 1; i < how; i++) {
                    if (length == i) {
                        for (var j = 1; j <= (how - length); j++) {
                            str += "0";
                        }
                        break;
                    }
                }
            }
        }
        return str;
    },

    //格式化数组，千分位
    formatThousand: function (number) {
        var num = number + "";
        num = num.replace(new RegExp(",", "g"), "");
        // 正负号处理
        var symble = "";
        if (/^([-+]).*$/.test(num)) {
            symble = num.replace(/^([-+]).*$/, "$1");
            num = num.replace(/^([-+])(.*)$/, "$2");
        }
        if (/^[0-9]+(\.[0-9]+)?$/.test(num)) {
            var num = num.replace(new RegExp("^[0]+", "g"), "");
            if (/^\./.test(num)) {
                num = "0" + num;
            }
            var decimal = num.replace(/^[0-9]+(\.[0-9]+)?$/, "$1");
            var integer = num.replace(/^([0-9]+)(\.[0-9]+)?$/, "$1");
            var re = /(\d+)(\d{3})/;
            while (re.test(integer)) {
                integer = integer.replace(re, "$1,$2");
            }
            return symble + integer + decimal;
        } else {
            return number;
        }
    },

    //将base64里的特殊字符 / 和 + 替换掉
    encodeBase64:function(str){
        return str.replace(/\//g, "-").replace(/\+/g, "_");
    },

    //将base64里的特殊字符 / 和 + 替换回来
    decodeBase64:function(str){
        return str.replace(/-/g, "\/").replace(/_/g, "\+");
    }
}

//var a = '/a+aaa';
//var b = utility.encodeBase64(a);
//var c = utility.decodeBase64(b)
//alert(a);
//alert(b);
//alert(c);

//String扩展方法，去除空格
String.prototype.trim = function () {
    return this.replace(/(^[\s\t　]+)|([　\s\t]+$)/g, "");
};

//String扩展方法，去除末尾指定字符
String.prototype.trimEnd = function (str) {
    //var re = /,{1,}/g;
    //this = this.replace(re, ',');       //把多个逗号换成一个逗号

    var re = eval("/" + str + "$/");
    return this.replace(re, "");
};

//String扩展方法，去除两头指定字符
String.prototype.trim = function (str) {
    var re = eval("/(^[\s\t"+str+"]+)|(["+str+"\s\t]+$)/g");
    return this.replace(re, "");
};

//判断字符串是不是以指定字符开始
String.prototype.startWith = function (str) {
    var reg = new RegExp("^" + str);
    return reg.test(this);
}

//判断字符串是不是以指定字符结尾
String.prototype.endWith = function (str) {
    var reg = new RegExp(str + "$");
    return reg.test(this);
}

//仿C#String.Format功能，调用方式：
//String.format('{0}+{0}+{1}={2}',1,2,1+1+2)
//输出: "1+1+2=4"
//String.format({name:'leonwang'},'hello,world')        //如果第一个参数不是string类型， 就简单返回空string， 不做进一步处理。
//输出: ""
String.format = function () {
    var formatStr = arguments[0];
    if (typeof formatStr === 'string') {
        var pattern,
            length = arguments.length;
        for (var i = 1; i < length; i++) {
            pattern = new RegExp('\\{' + (i - 1) + '\\}', 'g');
            formatStr = formatStr.replace(pattern, arguments[i]);
        }
    } else {
        formatStr = '';
    }
    return formatStr;
};




