<%@ page import="com.cannontech.multispeak.client.*" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="multispeakBean" class="com.cannontech.multispeak.client.MultispeakBean" scope="session"/>
<jsp:useBean id="newMspVendor" class="com.cannontech.multispeak.client.MultispeakVendor" scope="session"/>
<c:if test="${param.init != null}">
  <c:set target="${multispeakBean}" property="selectedMspVendor" value="${newMspVendor}"/>
  <c:set var="MSP_RESULT_MSG" value="" scope="session" />
  <c:set var="ERROR_MESSAGE" value="" scope="session" />
</c:if>

<cti:standardPage title="MultiSpeak Setup" module="multispeak">
	<cti:standardMenu menuSelection="multispeak|interfaces"/>
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/msp_setup.jsp" title="MultiSpeak"  />
	    &gt; Create Interface
	</cti:breadCrumbs>

	<c:set var="interfaceListLength" value="${fn:length(multispeakBean.clientInterfaces)}" />
	
	<script language="JavaScript">
		function enableEndpointValue(selected, interface) { //v1.0
		    document.getElementById("mspEndpoint"+interface).disabled = !selected;
		}
		
		function serviceSubmit(service, value)
		{
		  document.form1.actionService.value=service;
		  document.form1.actionEndpoint.value=document.getElementById("mspEndpoint"+service).value;
		  document.form1.ACTION.value=value;
		  document.form1.submit();
		}
		
		function dispStatusMsg(msgStr) { //v1.0
		  status=msgStr;
		  document.statVal = true;
		}
		
	</script>
    
	<br>
	<h2>MultiSpeak Setup</h2>

	<h4 class='ErrorMsg'><c:out value="${sessionScope.ERROR_MESSAGE}" default=""/></h4>

	<div style="width:800px;">
		<tags:boxContainer title="MultiSpeak Interface Setup" hideEnabled="false">
			<form name="form1" method="post" action="<cti:url value="/servlet/MultispeakServlet"/>">
				<input type="hidden" name="ACTION" value="updateMSP">
				<input type="hidden" name="REDIRECT" value='<c:out value="${pageContext.request.requestURI}"/>' >
				<input type="hidden" name="actionEndpoint">
				<input type="hidden" name="actionService">
			
				<tags:nameValueContainer style="width: 400px;float: left;">
				
					<tags:nameValue name="Company Name">
				    	<input title="Enter the Company" type="text" name="mspCompanyName" value='<c:out value="${multispeakBean.selectedMspVendor.companyName}"/>'> *
					</tags:nameValue>
					<tags:nameValue name="App Name">
				    	<input title="Enter the Application Name" type="text" name="mspAppName" value='<c:out value="${multispeakBean.selectedMspVendor.appName}"/>'>
					</tags:nameValue>
					<tags:nameValue name="MSP UserName">
						<input title="Enter the Username" type="text" name="mspUserName" value='<spring:escapeBody htmlEscape="true"><c:out value="${multispeakBean.selectedMspVendor.userName}"/></spring:escapeBody>'>
					</tags:nameValue>
					<tags:nameValue name="MSP Password">
				    	<input title="Enter the Password" type="text" name="mspPassword" value='<c:out value="${multispeakBean.selectedMspVendor.password}"/>'>
					</tags:nameValue>
					<tags:nameValue name="MSP Max Return Records">
				    	<input title="Enter the Max Return Records" type="text" name="mspMaxReturnRecords" value='<c:out value="${multispeakBean.selectedMspVendor.maxReturnRecords}"/>'> *
					</tags:nameValue>
					<tags:nameValue name="MSP Request Message Timeout">
				    	<input title="Enter the Request Message Timeout" type="text" name="mspRequestMessageTimeout" value='<c:out value="${multispeakBean.selectedMspVendor.requestMessageTimeout}"/>'> *
					</tags:nameValue>
					<tags:nameValue name="MSP Max Initiate Request Objects">
				    	<input title="Enter the Max Initiate Request Objects" type="text" name="mspMaxInitiateRequestObjects" value='<c:out value="${multispeakBean.selectedMspVendor.maxInitiateRequestObjects}"/>'> *
					</tags:nameValue>
					<tags:nameValue name="MSP Template Name Default">
				    	<input title="Enter the Template Name Default" type="text" name="mspTemplateNameDefault" value='<c:out value="${multispeakBean.selectedMspVendor.templateNameDefault}"/>'> *
					</tags:nameValue>
				</tags:nameValueContainer>

				<br><br><br>
				<u>Response Message Login</u>
				<br><br>
				<tags:nameValueContainer style="width: 300px;float: left;">
					<tags:nameValue name="UserName">
				    	<input title="Enter the Username for Outgoing Yukon messages" type="text" name="outUserName" value='<spring:escapeBody htmlEscape="true"><c:out value="${multispeakBean.selectedMspVendor.outUserName}"/></spring:escapeBody>'>
					</tags:nameValue>
					<tags:nameValue name="Password">
				    	<input title="Enter the Password for Outgoing Yukon messages" type="text" name="outPassword" value='<c:out value="${multispeakBean.selectedMspVendor.outPassword}"/>'>
					</tags:nameValue>
				</tags:nameValueContainer>
				
				<br style="clear:both">
				<span class="smallText">* required</span>				
			
			
				<br><br>
				<b><u>Interfaces</u></b>
				<br><br>
				
				<span onMouseOver="window.status='Enter the MultiSpeak URL   EX: http://127.0.0.1:80/soap/ ';return true;" onMouseOut="window.status='';return true;">
					MSP URL
					<input type="text" name="mspURL" size="30" value="http://&lt;server&gt;:&lt;port&gt;/soap/">
				</span>
				<br><br>
				
				<table cellspacing="4">
				
					<%------ INTERFACES ------%>
				  	<c:forEach var="mspPossibleInterface" items="${multispeakBean.clientInterfaces}" varStatus="status">
				  	
				    	<c:set var="interfaceValue" value="${interfacesMap[mspPossibleInterface]}" scope="page" />    
				    	<c:set var="disabled" value="${interfaceValue == null}" scope="page" />  
				    	
				      	<tr>
				      	
				      		<%-- checkbox --%>
				        	<td>
				          		<input id="mspInterface" type="checkbox" name='mspInterface' value='<c:out value="${mspPossibleInterface}"/>' onclick='enableEndpointValue(this.checked, this.value)'>
				        		<br><br>
				        	</td>
				        
				        	<%-- interface name --%>
				        	<td>
				        		<c:out value="${mspPossibleInterface}"/>
				        		<br><br>
				        	</td>
				        
				        	<%-- endpoint --%>
				        	<td>
				          		<input id="mspEndpoint<c:out value="${mspPossibleInterface}"/>" type="text" name="mspEndpoint" size="30" disabled
				            		value='<c:out value="${mspPossibleInterface}"/>Soap'>
				            	<br><br>
				            </td>
				        	 
				      	</tr>
				      	
				  	</c:forEach>
				  
					  <%------ SAVE/CANCEL BUTTONS ------%>
					  <tr> 
					    <td align="right" colspan="3">
					      <input type="submit" name="Submit" value="Save" onclick="document.form1.ACTION.value='Create';" class="formSubmit">
					      <input type="button" name="Cancel" value="Cancel" onclick="location.href='msp_setup.jsp?init'" class="formSubmit">
					    </td>
					  </tr>
							  
				</table>
			
				
				<br style="clear:both">
			</form>
		</tags:boxContainer>
	</div>
</cti:standardPage>