<html>
<head>
	<link rel="stylesheet" href="./static/style/master.css" />
	<script src="./static/jquery/jquery.min.js"></script>
	<script src="./static/jquery/jquery.dynatable.js"></script>
	<script src="static/js/master.js"></script>
</head>
<body>
	<h2>Database Access Service Press test</h2>
	<p>
		<table id="sql-table" class="bordered">
			<thead>
			<tr>
				<th>Database Access Type</th>
				<th>sql</th>
			</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</p>

	<p>
		SQL count: <input id="count" value="1000" />
		<button id="updateCount">update</button>
	</p>
	<p>
		Das service: <select id ="dasService">
  			<option value ="dal.dev.nt.ctripcorp.com:8080">dal.dev.nt.ctripcorp.com:8080</option>
  			<option value ="10.3.6.92:8080">10.3.6.92:8080</option>
  			<option value="10.3.6.114:9090">10.3.6.114:9090</option>
		</select>
	</p>
	<p>
		Das query Cache:<input id="fetchSize" value="20000" /><input id="querySize" value="500" /><input id="cache" value="true" /><button id="updateCache">submit</button>
	</p>
	<p>Das client cache:<input id="clientCache" type ="checkbox"></p>
	<h2>Statistic log:</h2>
	<table id="my-ajax-table" class="bordered">
		<thead>
			<tr>
				<th>Database Access Type</th>
				<th>Request Count</th>
				<th>All-all Time</th>
				<th>Service Handle Time</th>
				<th>Result Decode Time</th>
				<th>Result Size</th>
				<th>Binary Size</th>
				<th>Binary Avg Size</th>
				<th>Network Time</th>
				<th>Create client</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</body>
</html>
