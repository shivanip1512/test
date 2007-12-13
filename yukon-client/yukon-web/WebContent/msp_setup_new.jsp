<%@ page import="com.cannontech.multispeak.client.*" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="multispeakBean" class="com.cannontech.multispeak.client.MultispeakBean" scope="session"/>

<cti:standardPage title="MultiSpeak Setup" module="multispeak">
	<cti:standardMenu menuSelection="multispeak|interfaces"/>
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/msp_setup.jsp" title="Multispeak"  />
	    &gt; Create Interface
	</cti:breadCrumbs>

	<c:if test="${param.init != null}">
		<c:set var="MSP_RESULT_MSG" value="" scope="session" />
		<c:set var="ERROR_MESSAGE" value="" scope="session" />
	</c:if>
	
	<c:set var="arrayLength" value="<%=MultispeakDefines.MSP_INTERFACE_ARRAY.length%>" />
	
	<script language="JavaScript">
		function enableEndpointValue(selected, interface) { //v1.0
		 
		 if( selected )
		    {
		        document.getElementById("mspPing"+interface).style.cursor = 'pointer';
		        document.getElementById("mspMethods"+interface).style.cursor = 'pointer';
		        document.getElementById("mspPing"+interface).href = 'JavaScript:serviceSubmit(\"'+interface+'\", \"pingURL\");'
		        document.getElementById("mspMethods"+interface).href = 'JavaScript:document.form1.submit()'
		    }
		    else
		    {
		        document.getElementById("mspPing"+interface).style.cursor = 'default';
		        document.getElementById("mspMethods"+interface).style.cursor = 'default';
		        document.getElementById("mspPing"+interface).href = 'javascript:;'
		        document.getElementById("mspMethods"+interface).href = 'javascript:;'
		    }
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
			<form name="form1" method="post" action="<c:url value="/servlet/MultispeakServlet"/>">
				<input type="hidden" name="ACTION" value="updateMSP">
				<input type="hidden" name="REDIRECT" value='<c:out value="${pageContext.request.requestURI}"/>' >
				<input type="hidden" name="actionEndpoint">
				<input type="hidden" name="actionService">
			
				<tags:nameValueContainer style="width: 400px;float: left;">
				
					<tags:nameValue name="Company Name">
				    	<input title="Select vendor" type="text" name="mspCompanyName"/>
					</tags:nameValue>
					<tags:nameValue name="App Name">
				    	<input title="Enter the Application Name" type="text" name="mspAppName">
					</tags:nameValue>
					<tags:nameValue name="MSP UserName">
						<input title="Enter the Username" type="text" name="mspUserName"/>
					</tags:nameValue>
					<tags:nameValue name="MSP Password">
				    	<input title="Enter the Password" type="text" name="mspPassword"/>
					</tags:nameValue>
					<tags:nameValue name="MSP Max Return Records">
				    	<input title="Enter the Max Return Records" type="text" name="mspMaxReturnRecords" value="10000">
					</tags:nameValue>
					<tags:nameValue name="MSP Request Message Timeout">
				    	<input title="Enter the Request Message Timeout" type="text" name="mspRequestMessageTimeout" value="120000">
					</tags:nameValue>
					<tags:nameValue name="MSP Max Initiate Request Objects">
				    	<input title="Enter the Max Initiate Request Objects" type="text" name="mspMaxInitiateRequestObjects" value="15">
					</tags:nameValue>
					<tags:nameValue name="MSP Template Name Default">
				    	<input title="Enter the Template Name Default" type="text" name="mspTemplateNameDefault" value="*Default Template">
					</tags:nameValue>
					<tags:nameValue name="MSP Unique Key">
				    	<select title="Enter the unique key" name="mspUniqueKey">
					        <option value="meterNumber" selected>meterNumber</option>
					        <option value="deviceName" >deviceName</option>
					    </select>                 
					</tags:nameValue>
				
				</tags:nameValueContainer>
				
				<br><br><br>
				<u>Response Message Login</u>
				<br><br>
				<tags:nameValueContainer style="width: 300px;float: left;">
					<tags:nameValue name="UserName">
				    	<input title="Enter the Username for Outgoing Yukon messages" type="text" name="outUserName">
					</tags:nameValue>
					<tags:nameValue name="Password">
				    	<input title="Enter the Password for Outgoing Yukon messages" type="text" name="outPassword" />
					</tags:nameValue>
				</tags:nameValueContainer>
				
				<br style="clear:both">
			
				<table class="keyValueTable">
				  <tr height="40" valign="bottom">
				    <td colspan="2" style="text-align:right"><u>Interfaces</u>
				    </td>
				  </tr>
				  <tr>
				    <td colspan="2" style="text-align:right" onMouseOver="dispStatusMsg('Enter the MultiSpeak URL   EX: http://127.0.0.1:80/soap/ ');return document.statVal" onMouseOut="dispStatusMsg('');return document.statVal">MSP URL</td>
				    <td>
				      <input type="text" name="mspURL" size="30" value="http://&lt;server&gt;:&lt;port&gt;/soap/">
				    </td>
				  </tr>
				
				  <c:forEach var="mspPossibleInterface" items="${multispeakBean.allInterfaces}" varStatus="status">
				    <tr>
				      <td style="text-align:right">
				        <input id="mspInterface" type="checkbox" name='mspInterface' value='<c:out value="${mspPossibleInterface}"/>' onclick='enableEndpointValue(this.checked, this.value)'>
				      </td>
				      <td style="text-align:center"><c:out value="${mspPossibleInterface}"/>
				      </td>
				      <td colspan="2">
				        <input id="mspEndpoint<c:out value="${mspPossibleInterface}"/>" type="text" name="mspEndpoint" size="30" disabled
				            value='<c:out value="${mspPossibleInterface}"/>Soap'>
				          <a id="mspPing<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="pingURL">Ping</a>
				          <a id="mspMethods<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="getMethods">Methods</a>
				      </td>
				      <c:if test="${status.first}">
				      <td rowspan='${arrayLength}'>
				        <textarea cols="50" rows="${arrayLength * 2}" name="Results" readonly wrap="VIRTUAL" style='color:<c:out value="${sessionScope.resultColor}"/>'><c:out value="${sessionScope.MSP_RESULT_MSG}"/></textarea>
				      </td>
				      </c:if>          
				    </tr>
				  </c:forEach>
				  <tr> 
				    <td align="right" colspan="3">
				      <input type="submit" name="Submit" value="Save" onclick="document.form1.ACTION.value='Create';">
				      <input type="button" name="Cancel" value="Cancel" onclick="location.href='msp_setup.jsp?init'">
				    </td>
				  </tr>
				</table>
			</form>
		</tags:boxContainer>
	</div>
</cti:standardPage>