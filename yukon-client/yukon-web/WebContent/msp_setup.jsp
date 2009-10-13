<%@ page import="com.cannontech.multispeak.client.*" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="multispeakBean" class="com.cannontech.multispeak.client.MultispeakBean" scope="session"/>
<c:if test="${param.init != null}">
	<c:set target="${multispeakBean}" property="selectedMspVendor" value="${null}"/>
	<c:set var="MSP_RESULT_MSG" value="" scope="session" />
	<c:set var="ERROR_MESSAGE" value="" scope="session" />
</c:if>

<cti:standardPage title="MultiSpeak Interfaces" module="multispeak">
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/msp_setup.jsp" title="MultiSpeak"  />
	    &gt; Interfaces
	</cti:breadCrumbs>
	<cti:standardMenu menuSelection="multispeak|interfaces"/>

	<cti:url var="setupUrl" value="/msp_setup.jsp">
	    <cti:param name="init" value="" />
	</cti:url>
	
	<c:set var="interfaceListLength" value="${fn:length(multispeakBean.possibleInterfaces)}" />
	
	<script language="JavaScript">
		function enableEndpointValue(selected, interface) {
		 
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
		
		function vendorChanged(vendor)
		{
			document.form1.ACTION.value='ChangeVendor';
			document.form1.submit();
		}

	</script>
    
	<h2>MultiSpeak Setup</h2>
	<br>

	<h4 class='ErrorMsg'><c:out value="${sessionScope.ERROR_MESSAGE}" default=""/></h4>
	
	<div style="width:800px;">
		<tags:boxContainer title="MultiSpeak Interface Setup" hideEnabled="false">
		
			<form name="form1" method="post" action="<cti:url value="/servlet/MultispeakServlet"/>">
				<input type="hidden" name="ACTION" value="updateMSP">
				<input type="hidden" name="actionEndpoint">
				<input type="hidden" name="actionService">
				<input type="hidden" name="REDIRECT" value='<c:out value="${pageContext.request.requestURI}"/>' >
				<input type="hidden" name="mspCompanyName" value='<c:out value="${multispeakBean.selectedMspVendor.companyName}"/>'>
				
				<tags:nameValueContainer style="width: 400px;float: left;">
				
					<tags:nameValue name="Company Name">
				      <select title="Select vendor" name="mspVendor" onChange="vendorChanged(this[this.selectedIndex].value);">
					      <c:forEach var="mspVendorEntry" items="${multispeakBean.mspVendorList}">
					        <option <c:if test="${mspVendorEntry.vendorID == multispeakBean.selectedMspVendor.vendorID}">selected</c:if> value='<c:out value="${mspVendorEntry.vendorID}"/>'> <c:out value="${mspVendorEntry.companyName}"/> </option>
					      </c:forEach>
				      </select>
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
					<c:if test='${multispeakBean.selectedMspVendor.companyName == "Cannon"}'>
						<tags:nameValue name="Primary CIS">
					    	<select title="Select the Primary CIS vendor" name="mspPrimaryCIS">
					        	<option selected value='0'>(none)</option>
					      		<c:forEach var="mspVendorEntry" items="${multispeakBean.mspVendorList}">
					        		<option <c:if test="${mspVendorEntry.vendorID == multispeakBean.primaryCIS}">selected</c:if> value='<c:out value="${mspVendorEntry.vendorID}"/>'> 
					        			<c:out value="${mspVendorEntry.companyName}"/> <c:if test="${mspVendorEntry.appName != ''}">(<c:out value="${mspVendorEntry.appName}"/>)</c:if>
					        		</option>
						    	</c:forEach>
					      	</select>
						</tags:nameValue>
						<tags:nameValue name="DeviceName Alias">
					        <select title="Select the DeviceName Alias field" name="mspPaoNameAlias">
					        	<c:forEach var="mspPaoNameAliasEntry" items="${multispeakBean.selectedMspVendor.paoNameAliasStrings}" varStatus="status">
					            	<option <c:if test="${status.index == multispeakBean.paoNameAlias}">selected</c:if> value='<c:out value="${status.index}"/>'> <c:out value="${mspPaoNameAliasEntry}"/></option>
					        	</c:forEach>
					        </select>
						</tags:nameValue>
					</c:if>
				</tags:nameValueContainer>
				
				<br style="clear:both">
				<div class="smallText">* required</div>
				
				
				<br><br>
				<b><u>Interfaces</u></b>
				<br><br>
				
				<span onMouseOver="window.status='Enter the MultiSpeak URL   EX: http://127.0.0.1:80/soap/ ';return true;" onMouseOut="window.status='';return true;">
					MSP URL
					<input type="text" name="mspURL" size="30" value='<c:out value="${multispeakBean.selectedMspVendor.url}"/>'>
				</span>
				
				<table cellspacing="4">
				
					<%------ INTERFACES ------%>
		  			<c:set var="interfacesMap" value="${multispeakBean.selectedInterfacesMap}" scope="page" />  
				  	<c:forEach var="mspPossibleInterface" items="${multispeakBean.possibleInterfaces}" varStatus="status">
				  	
				    	<c:set var="interfaceValue" value="${interfacesMap[mspPossibleInterface]}" scope="page" />    
				    	<c:set var="disabled" value="${interfaceValue == null}" scope="page" />  
				    	
				      	<tr>
				      	
				      		<%-- checkbox --%>
				        	<td>
				          		<input id="mspInterface" type="checkbox" <c:if test="${!disabled}">checked</c:if> name='mspInterface' value='<c:out value="${mspPossibleInterface}"/>' onclick='enableEndpointValue(this.checked, this.value)'>
				        	</td>
				        
				        	<%-- interface name --%>
				        	<td>
				        		<c:out value="${mspPossibleInterface}"/>
				        	</td>
				        
				        	<%-- endpoint --%>
				        	<td>
				          		<input id="mspEndpoint<c:out value="${mspPossibleInterface}"/>" type="text" name="mspEndpoint" size="30" 
				                	value='<c:out value="${interfaceValue.mspEndpoint}" default="${mspPossibleInterface}Soap"/>'
				                	<c:if test="${disabled}">disabled</c:if>>  
				            </td>
				                
				                	     
			               	<%-- ping, method links --%>  
			               	<td>       
					          	<c:choose>
					            	<c:when test="${disabled}">
					              		<a id="mspPing<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="pingURL">Ping</a>
					              		<a id="mspMethods<c:out value="${mspPossibleInterface}"/>" href='JavaScript:;' style='cursor:default' class="Link4" name="getMethods">Methods</a>
					            	</c:when>
					            	<c:otherwise>
					              		<a id="mspPing<c:out value="${mspPossibleInterface}"/>" style='cursor:pointer' href='JavaScript:serviceSubmit("<c:out value='${interfaceValue.mspInterface}'/>", "pingURL");' class="Link4" name="pingURL">Ping</a>
					              		<a id="mspMethods<c:out value="${mspPossibleInterface}"/>" style='cursor:pointer' href='JavaScript:serviceSubmit("<c:out value='${interfaceValue.mspInterface}'/>", "getMethods");' class="Link4" name="getMethods">Methods</a>
					            	</c:otherwise>
					          	</c:choose>  
				        	</td>
				        	
				        	<%-- results column --%>
				        	<c:if test="${status.first}">
				        		<td rowspan='${interfaceListLength}'>
				          			<textarea cols="50" rows="${interfaceListLength * 2}" name="Results" readonly wrap="VIRTUAL" style='color:<c:out value="${sessionScope.resultColor}"/>'><c:out value="${sessionScope.MSP_RESULT_MSG}"/></textarea>
				        		</td>
				        	</c:if>
				        	 
				      	</tr>
				      	
				  </c:forEach>
							  
				  		
				  
				  <%------ SAVE/DELETE/NEW BUTTONS ------%>
				  <tr> 
				    <td align="right" colspan="5">
				    	<br>
				      <input type="submit" name="Save" value="Save" onclick="document.form1.ACTION.value='Save';">
				      <input type="submit" name="Delete" value="Delete" onclick="document.form1.ACTION.value='Delete';">
				      
				      <cti:url var="setupNewUrl" value="/msp_setup_new.jsp">
						<cti:param name="init" value="" />
					  </cti:url>
				      <input type="button" name="New" value="New" onclick="javascript:window.location='${setupNewUrl}'">
				    </td>
				  </tr>
				
			</table>
				  
			</form>
		</tags:boxContainer>
	</div>
		
</cti:standardPage>