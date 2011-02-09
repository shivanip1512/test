<?xml version="1.0"?>
<vxml version="2.0">
<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.core.roleproperties.YukonRoleProperty" %>
<%@ page import="com.cannontech.core.roleproperties.dao.RolePropertyDao" %>
<%@ page import="com.cannontech.stars.dr.optout.util.OptOutUtil" %>

<% 
	String action = "OptOutProgram";
	String redirect = request.getContextPath() + "/voice/inboundConfirm.jsp";
	String referer = request.getRequestURI();
    
	String today = datePart.format(new Date());
	String tomorrow = datePart.format(new Date(new Date().getTime()+(24*60*60*1000)));
            
%>
	
	<form id="optout">
    
    <!-- 
    If an error occurs while trying to opt out, the error message is played and the call is ended.
    -->
<% 
    if (errorMsg != null){ 
%>
        <block>
            <![CDATA[ We're sorry, the system has encountered the following error: <%=errorMsg%>. Goodbye.]]>
            <disconnect/>
        </block>
        
<%
    }
%>
		<var name='action' expr="'<%=action%>'"/>
		<var name='REDIRECT' expr="'<%=redirect%>'"/>
		<var name='REFERRER' expr="'<%=referer%>'"/>

        <!-- Prompt for start date - either today or tomorrow -->
		<field name="StartDate">
			<prompt count="1" timeout="5s">Please enter the day you would like to opt out of all programs followed by the pound sign.  Enter <enumerate/></prompt>			
			
			<option dtmf="1" value="<%=today%>"> 1 for today </option>
			<option dtmf="2" value="<%=tomorrow%>"> 2 for tomorrow </option>
			
			<noinput count="1">Please enter <enumerate/></noinput>
			<noinput count="2">Please enter <enumerate/></noinput>
			<noinput count="3">goodbye<exit/></noinput>

			<nomatch>Please enter <enumerate/></nomatch>
		</field>
		
        <!-- Prompt for duration using the customer's opt out selection list -->
		<field name="Duration">
			<prompt count="1" timeout="5s">Please enter the opt out time period followed by the pound sign.  Enter <enumerate/></prompt>

<%
	int option = 1;
    
    final RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
    String optOutPeriodString = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.RESIDENTIAL_OPT_OUT_PERIOD,  user.getYukonUser());
    
    List<Integer> optOutPeriodList = OptOutUtil.parseOptOutPeriodString(optOutPeriodString);
   
	for (Integer optOutPeriod : optOutPeriodList) {
		String period = (optOutPeriod == 1) ? "Day" : "Days";
%>
			<option dtmf="<%= option %>" value="<%= optOutPeriod %>"> <%= option++ %> for <%= optOutPeriod %> <%= period %> </option>
<%
	}
%>

			<noinput count="1">Please enter <enumerate/></noinput>
			<noinput count="2">Please enter <enumerate/></noinput>
			<noinput count="3">goodbye<exit/></noinput>
			
			<nomatch>Please enter <enumerate/></nomatch>
			
		</field>
   
 		<block>
 			<filled namelist="StartDate Duration">   
    
                <!-- 
                This prompt will confirm for the user what they have selected - the start day and 
                duration for their opt out.  The voice xml server executes this prompt AFTER the 
                following <submit> has occured.
                -->
    			<prompt>You have selected to opt out of all programs starting 
                    <if cond="StartDate == '<%=today%>'">
                    today
                    <else/>
                    tomorrow
                    </if>
                 for 

<%
    option = 1;
    
    for (Integer optOutPeriod : optOutPeriodList) {
        String period = (optOutPeriod == 1) ? "Day" : "Days";
%>
                    <if cond="Duration == <%= optOutPeriod %>"><%= optOutPeriod %> <%= period %></if>
<%
    }
%>
                <break time="300"/>
                </prompt>
                
                <!-- submit the opt out request to the SOAPClient servlet -->
 				<submit next="/servlet/SOAPClient" method="post" namelist="StartDate Duration action REDIRECT REFERRER" fetchhint="safe"/>	
    
 			</filled>	   

 		</block> 
   
 	</form>     
	
</vxml>   
