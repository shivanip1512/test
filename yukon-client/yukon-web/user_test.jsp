<!-- 
	Example of using checkRole and text custom tags
-->	

<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<jsp:useBean id="YUKON_USER" scope="session" class="com.cannontech.database.data.lite.LiteYukonUser"/>
<html>
<cti:checkRole name="WEB_USER">
You are a web user<br>
user id: <b><jsp:getProperty name="YUKON_USER"  property="userID" /></b><br>
username:  <b><jsp:getProperty name="YUKON_USER"  property="username" /></b><br>
home url:  <b><cti:text key="HOME_URL"/></b>
</cti:checkRole><br>

<cti:checkRole name="WEB_OPERATOR">
You are a web operator<br>
user id: <b><jsp:getProperty name="YUKON_USER"  property="userID" /></b><br>
username:  <b><jsp:getProperty name="YUKON_USER"  property="username" /></b><br>
home url:  <b><cti:text key="HOME_URL"/></b>
</cti:checkRole><br>
</html>

