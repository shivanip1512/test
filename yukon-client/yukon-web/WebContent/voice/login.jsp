<?xml version="1.0"?>
<vxml version="2.0">


<%
String contactid = request.getParameter("CONTACTID");

String token = request.getParameter("TOKEN");
%>
 <form id="welcome">

  <var name='ACTION' expr="'VOICELOGIN'"/>
  <var name='USERNAME' expr="'<%=contactid%>'"/>
  <var name='TOKEN' expr="'<%=token%>'"/>

  <block>
  An important message from your energy provider
  </block>

  <field name="PASSWORD" type="digits">
	<prompt count="1" timeout="5s">Please enter your pin followed by the pound sign</prompt>
    <noinput count="1">Please enter your pin using your phone key pad</noinput>
    <noinput count="2">Please enter your pin</noinput>
    <noinput count="3">goodbye<exit/></noinput>
  </field>

  <filled namelist="ACTION USERNAME TOKEN PASSWORD">
	    <submit next="/servlet/LoginController" method="post" namelist="ACTION USERNAME TOKEN PASSWORD" fetchtimeout="300s" fetchhint="safe"/>
  </filled>				
 </form>     
	
		
  
</vxml>
