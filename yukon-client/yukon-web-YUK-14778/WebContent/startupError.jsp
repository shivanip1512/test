<html>
<head>
<title>Server Startup Error</title>

<style type="text/css">

  body {
    background: #eee;
  }
  #error {
    background: white;
    width: 90%;
    margin-left: auto;
    margin-right: auto;
    margin-top: 30px;
    padding: 20px;
    border: solid 2px #888;
  }
  
  #errorMain {
    text-align: center;
    font-weight: bold;
    font-size: 150%;
  }
  
</style>

</head>

<body>
<div id="error">
<div id="errorMain">
The web server failed to startup.<br>
This is almost always related to a database connection problem.
</div>
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
  <li>"signer information does not match signer information of other classes in the same package": A JAR was added to the classpath that wasn't correctly signed. If the system was recently patched, ensure that the creator of the patch ran the sign task.</li>
</ul>
</div>
</body>
</html>