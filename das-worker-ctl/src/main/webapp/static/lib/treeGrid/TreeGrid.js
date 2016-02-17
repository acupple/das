/**
 * @author 陈举民
 * @version 1.0
 * @link http://chenjumin.javaeye.com
 */
TreeGrid = function (_config) {
    _config = _config || {};

    var s = "";
    var rownum = 0;
    var __root;

    var __selectedData = null;
    var __selectedId = null;
    var __selectedIndex = null;

    var folderOpenIcon = (_config.folderOpenIcon || TreeGrid.FOLDER_OPEN_ICON);
    var folderCloseIcon = (_config.folderCloseIcon || TreeGrid.FOLDER_CLOSE_ICON);
    var defaultLeafIcon = (_config.defaultLeafIcon || TreeGrid.DEFAULT_LEAF_ICON);

    //显示表头行
    drowHeader = function () {
        s += "<tr class='header' height='" + (_config.headerHeight || "25") + "'>";
        var cols = _config.columns;
        for (var i = 0; i < cols.length; i++) {
            var col = cols[i];
            s += "<td align='" + (col.headerAlign || _config.headerAlign || "center") + "' width='" + (col.width || "") + "'>" + (col.headerText || "") + "</td>";
        }
        s += "</tr>";
    }

    //递归显示数据行
    drowData = function () {
        var rows = _config.data;
        var cols = _config.columns;
        drowRowData(rows, cols, 1, "");
    }

    //局部变量i、j必须要用 var 来声明，否则，后续的数据无法正常显示
    drowRowData = function (_rows, _cols, _level, _pid) {
        var folderColumnIndex = (_config.folderColumnIndex || 0);

        for (var i = 0; i < _rows.length; i++) {
            var id = _pid + "_" + i; //行id
            var row = _rows[i];

            s += "<tr id='TR" + id + "' pid='" + ((_pid == "") ? "" : ("TR" + _pid)) + "' opened='Y' data=\"" + TreeGrid.json2str(row) + "\" rowIndex='" + rownum++ + "'>";
            for (var j = 0; j < _cols.length; j++) {
                var col = _cols[j];
                s += "<td align='" + (col.dataAlign || _config.dataAlign || "left") + "'";

                //层次缩进
                if (j == folderColumnIndex) {
                    s += " style='text-indent:" + (parseInt((_config.indentation || "20")) * (_level - 1)) + "px;'> ";
                } else {
                    s += ">";
                }

                //节点图标
                if (j == folderColumnIndex) {
                    if (row.children && row.children.length > 0) { //有下级数据
                        s += "<img folder='Y' trid='TR" + id + "' src='" + folderOpenIcon + "' class='image_hand'>";
                    } else {
                        s += "<img src='" + defaultLeafIcon + "' class='image_nohand'>";
                    }
                }

                //单元格内容
                if (col.handler) {
                    s += (eval(col.handler + ".call(new Object(), row, col)") || "") + "</td>";
                } else {
                    s += (row[col.dataField] || "") + "</td>";
                }
            }
            s += "</tr>";

            //递归显示下级数据
            if (row.children) {
                drowRowData(row.children, _cols, _level + 1, id);
            }
        }
    }

    //主函数
    this.show = function () {
        this.id = _config.id || ("TreeGrid" + TreeGrid.COUNT++);

        s += "<table id='" + this.id + "' cellspacing=0 cellpadding=0 width='" + (_config.width || "100%") + "' class='table table-striped TreeGrid'>";
        drowHeader();
        drowData();
        s += "</table>";

        __root = jQuery("#" + _config.renderTo);
        __root.append(s);

        //初始化动作
        init();
    }

    init = function () {
        //展开、关闭下级节点
        __root.find("img[folder='Y']").on("click", function () {
            var trid = this.trid || this.getAttribute("trid");
            var isopened = __root.find("#" + trid).attr("opened");
            isopened = (isopened == "Y") ? "N" : "Y";
            __root.find("#" + trid).attr("opened", isopened);
            showHiddenNode(trid, isopened);
        });
    }

    //显示或隐藏子节点数据
    showHiddenNode = function (_trid, _opened) {
        if (_opened == "N") { //隐藏子节点
            __root.find("#" + _trid).find("img[folder='Y']").attr("src", folderCloseIcon);
            __root.find("tr[id^=" + _trid + "_]").css("display", "none");
        } else { //显示子节点
            __root.find("#" + _trid).find("img[folder='Y']").attr("src", folderOpenIcon);
            showSubs(_trid);
        }
    }

    //递归检查下一级节点是否需要显示
    showSubs = function (_trid) {
        var isopened = __root.find("#" + _trid).attr("opened");
        if (isopened == "Y") {
            var trs = __root.find("tr[pid=" + _trid + "]");
            trs.css("display", "");

            for (var i = 0; i < trs.length; i++) {
                showSubs(trs[i].id);
            }
        }
    }

    //展开或收起所有节点
    this.expandAll = function (isopened) {
        var trs = __root.find("tr[pid='']");
        for (var i = 0; i < trs.length; i++) {
            var trid = trs[i].id || trs[i].getAttribute("id");
            showHiddenNode(trid, isopened);
        }
    }

    //取得当前选中的行记录
    this.getSelectedItem = function () {
        return new TreeGridItem(__root, __selectedId, __selectedIndex, TreeGrid.str2json(__selectedData));
    }

};

//公共静态变量
TreeGrid.FOLDER_OPEN_ICON = "static/lib/treeGrid/folderOpen.png";
TreeGrid.FOLDER_CLOSE_ICON = "static/lib/treeGrid/folderClose.png";
TreeGrid.DEFAULT_LEAF_ICON = "static/lib/treeGrid/defaultLeaf.png";
TreeGrid.COUNT = 1;

//将json对象转换成字符串
TreeGrid.json2str = function (obj) {
    var arr = [];

    var fmt = function (s) {
        if (typeof s == 'object' && s != null) {
            if (s.length) {
                var _substr = "";
                for (var x = 0; x < s.length; x++) {
                    if (x > 0) _substr += ", ";
                    _substr += TreeGrid.json2str(s[x]);
                }
                return "[" + _substr + "]";
            } else {
                return TreeGrid.json2str(s);
            }
        }
        return /^(string|number)$/.test(typeof s) ? "'" + s + "'" : s;
    }

    for (var i in obj) {
        if (typeof obj[i] != 'object') { //暂时不包括子数据
            arr.push(i + ":" + fmt(obj[i]));
        }
    }

    return '{' + arr.join(', ') + '}';
}

TreeGrid.str2json = function (s) {
    var json = null;
    json = new Function("return " + s)();
    return json;
}

//数据行对象
function TreeGridItem(_root, _rowId, _rowIndex, _rowData) {
    var __root = _root;

    this.id = _rowId;
    this.index = _rowIndex;
    this.data = _rowData;

    this.getParent = function () {
        var pid = jQuery("#" + this.id).attr("pid");
        if (pid != "") {
            var rowIndex = jQuery("#" + pid).attr("rowIndex");
            var data = jQuery("#" + pid).attr("data");
            return new TreeGridItem(_root, pid, rowIndex, TreeGrid.str2json(data));
        }
        return null;
    }

    this.getChildren = function () {
        var arr = [];
        var trs = jQuery(__root).find("tr[pid='" + this.id + "']");
        for (var i = 0; i < trs.length; i++) {
            var tr = trs[i];
            arr.push(new TreeGridItem(__root, tr.id, tr.rowIndex, TreeGrid.str2json(tr.data)));
        }
        return arr;
    }
};