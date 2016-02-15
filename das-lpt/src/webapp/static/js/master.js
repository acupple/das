/**
 * Created by wcyuan on 2015/3/16.
 */
$(document).ready(
    function() {
        var sqltable = $('#sql-table');
        var mytable = $('#my-ajax-table');
        var restful_base = './webresources/myresource/';
        var restful_sql = restful_base + 'sql';
        var resftul_stats = restful_base + 'stats';

        function updateTable(table, restful_url) {
            $.ajax({
                url: restful_url,
                success: function(data){
                    table.dynatable({
                        dataset: {
                            records: data.records
                        },features: {
                            paginate: false,
                            search: false,
                            recordCount: false,
                            perPageSelect: false,
                            sort: false
                        }
                    });
                    table.data('dynatable').settings.dataset.records = data.records;
                    table.data('dynatable').dom.update();
                }
            });
        }
        updateTable(mytable, resftul_stats);
        updateTable(sqltable, restful_sql);

        setInterval(function(){
            updateTable(mytable, resftul_stats);
        }, 3000);

        $.ajax({
            url:restful_base + 'qcm',
            success:function(data){
                $("#count").val(data.resultCount + '');
                $("#dasService").val(data.host + ":" + data.port);
                $("#fetchSize").val(data.fetchSize + '');
                $("#querySize").val(data.querySize + '');
                $("#cache").val(data.enableDasServiceCache + '');
                document.getElementById("clientCache").checked = data.enableDasClientCache;
            }
        });

        $("#updateCache").click(function(){
            var cache = $("#cache").val();
            var fetchSize = $("#fetchSize").val();
            var querySize = $("#querySize").val();
            $.get(restful_base + "qcf?fetchSize=" + fetchSize + "&querySize=" + querySize + "&cache=" + cache,
                function(data, status){
                    alert("update query feature: " + data);
                });
        });

        $("#updateCount").click(
            function() {
                var count = $("#count").val();
                $.get(restful_base + "count?count=" + count, function(data, status) {
                    updateTable(sqltable, restful_sql);
                    updateTable(mytable, resftul_stats);
                });
            });
        $("#clientCache").click(function(){
            var enable = document.getElementById("clientCache").checked;
            $.get(restful_base + "qcfx?cache=" + enable, function(data, status) {
                alert(data);
            });
        });
        $("#dasService").change(function(){
            var address = $("#dasService").val();
            $.get(restful_base + "dsh?address=" + address, function(data, status){
                alert(data);
            });
        });
    });