<?xml version="1.0"?>
<vxml version="2.0">

<%
String contactid = request.getParameter("username");

String token = request.getParameter("token");

%>


<var name='contactid' expr="<%=username%>"/>
<var name='token' expr="'<%=token%>'"/>

<block>
Welcome to message box.  Token is <value expr="token"/>.  Contact is <value expr="contactid">
</block>
<disconnect/>
</vxml>