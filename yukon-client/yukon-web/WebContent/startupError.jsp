<html>
<head>
<title>Server Startup Error</title>
</head>

<body>

The web server failed to startup. This is almost always related to a database connection problem.
<br><br>
Please check that the hostname (or IP address), username and password for the database
are properly defined in the master.cfg file.
<br><br>
Error msg:
<pre>${comcannontechSERVLET_STARTUP_ERROR_ROOT_MESSAGE}</pre>

<br><br>
<h3>Notes</h3>
<ul>
  <li>"Hibernate Dialect must be explicitly set": This is related to a database connection problem. Check the log for the exact error.</li>
</ul>
</body>
</html>