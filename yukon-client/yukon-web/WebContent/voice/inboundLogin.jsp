<?xml version="1.0"?>
<vxml version="2.0">

<%
String tries = request.getParameter("TRIES");
if (tries == null) {
  tries = "0";
}
%>
	
	<form id="welcome">

		<var name='ACTION' expr="'INBOUNDVOICELOGIN'"/>
		<var name='INTRO' expr="'Login failed'"/>
		<var name='TRIES' expr="<%=tries%>"/>

        <!-- Welcome the user on the first login attempt.  After 3 login attempts, end the call -->
		<block>
			<if cond ="TRIES == 0">
				<assign name="INTRO" expr="'Welcome to the opt out system.'"/>
			</if>

			<if cond ="TRIES == 3">
				Your login attempts have exceeded the allotted amount of retries, goodbye 
				<disconnect/>
			</if> 
		</block>
		
		<block>
			<value expr="INTRO"/>
		</block>

        <!-- Prompt for phone number -->
		<field name="PHONE" type="digits?length=10">
			<prompt count="1" timeout="5s">Please enter your ten digit phone number followed by the pound sign</prompt>			
			<noinput count="1">Please enter your ten digit phone number using your phone key pad</noinput>
			<noinput count="2">Please enter your ten digit phone number</noinput>
			<noinput count="3">goodbye<exit/></noinput>
			<nomatch>Please enter your ten digit phone number</nomatch>
		</field>
		
        <!-- Prompt for pin -->
		<field name="PIN" type="digits">
			
			<prompt count="1" timeout="5s">Please enter your pin followed by the pound sign</prompt>
			
			<noinput count="1">Please enter your pin using your phone key pad</noinput>
			<noinput count="2">Please enter your pin</noinput>
			<noinput count="3">goodbye<exit/></noinput>
			
		</field>
   
 		<block>
 			<assign name='TRIES' expr="TRIES + 1"/>
    
 			<filled namelist="PHONE PIN ACTION">   
    
                <!-- Submit to the LoginController servlet -->
 				<submit next="/servlet/LoginController" method="post" namelist="PHONE PIN ACTION TRIES" fetchtimeout="300s" fetchhint="safe"/>	
       
 			</filled>	   

 		</block> 
   
 	</form>     
	
</vxml>   
