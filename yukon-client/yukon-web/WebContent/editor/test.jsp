<%@ page import="com.cannontech.database.data.point.*" %>

<html>

<head>
  <meta HTTP-EQUIV="Content-Type" CONTENT="text/html;charset=UTF-8">
  <title>Faces Impl</title>
  <link rel="stylesheet" type="text/css" href="base.css">
</head>


<body>

<% 
request.getSession(true).invalidate();
request.getSession(true);
%>
<form method="post" action="pointEditor.jsf">
	<input type="hidden" name="ptType" value="<%=PointTypes.ANALOG_POINT%>" />
	<input type="hidden" name="ptId" value="2" />

	<input type="submit" name="Go To" value="submit" />

</form>

</body>
</html>