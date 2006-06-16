<?xml version="1.0"?>
<vxml version="2.0">
<%@ page import="com.cannontech.database.cache.functions.ContactFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteContact" %>
<%@ page import="com.cannontech.database.cache.functions.AuthFuncs" %>
<%@ page import="com.cannontech.roles.notifications.IvrRole" %>
<%@ page import="com.cannontech.database.cache.functions.YukonUserFuncs" %>
<%@ page import="com.cannontech.database.cache.functions.EnergyCompanyFuncs" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>


<%
String contactid = request.getParameter("CONTACTID");
LiteContact contact = ContactFuncs.getContact(Integer.parseInt(contactid));
LiteYukonUser user = YukonUserFuncs.getLiteYukonUser(contact.getLoginID());
String introText = EnergyCompanyFuncs.getEnergyCompanyProperty(user, IvrRole.INTRO_TEXT);

String tries =request.getParameter("TRIES");
if (tries == null) {
  tries = "0";
}

String token = request.getParameter("TOKEN");

String noConfirmUrl = "/voice/confirm.jsp?TOKEN=" + token;
%>
 <form id="welcome">

  <var name='TRIES' expr = "<%=tries%>"/>
  <var name='USERNAME' expr="'<%=contactid%>'"/>
  <var name='ACTION' expr="'VOICELOGIN'"/>
  <var name='TOKEN' expr="'<%=token%>'"/>
  <var name='REDIRECT'/>
  <var name='INTRO'/>

  <block>
   <assign name="INTRO" expr="'Login failed'"/>
   <if cond ="TRIES == 0">
    <assign name="INTRO" expr="'<%=introText%>'"/>
   </if>

   <if cond ="TRIES == 3">
    Your login attempts have exceeded the allotted amount of retries, goodbye 
    <goto fetchint="safe" next="<%=noConfirmUrl%>"/>
    <disconnect/>
   </if> 
   </block>
   <block>
     <value expr="INTRO"/>
   </block>
     <field name="PASSWORD" type="digits">
	    <prompt count="1" timeout="5s">Please enter your pin followed by the pound sign to hear the message</prompt>
        <noinput count="1">Please enter your pin using your phone key pad</noinput>
        <noinput count="2">Please enter your pin</noinput>
        <noinput count="3">goodbye<exit/></noinput>
     </field>
   <block>
   <assign name='TRIES' expr="TRIES + 1"/>
   <assign name="REDIRECT" expr="'/voice/login.jsp?CONTACTID=' + USERNAME + '&amp;TOKEN=' + TOKEN + '&amp;TRIES=' + TRIES"/>
   
    
  <filled namelist="TOKEN USERNAME ACTION PASSWORD REDIRECT">   
    
     <submit next="/servlet/LoginController" method="post" namelist="TOKEN USERNAME ACTION REDIRECT PASSWORD" fetchtimeout="300s" fetchhint="safe"/>	
       
  </filled>	   

  </block> 
   
 </form>     
	
		
  
</vxml>
