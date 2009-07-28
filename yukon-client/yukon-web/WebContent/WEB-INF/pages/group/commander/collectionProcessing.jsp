<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage title="Group Processing" module="amr">

	<cti:standardMenu menuSelection="devicegroups|commander"/>
	
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    <cti:crumbLink title="Group Processing"/>
	</cti:breadCrumbs>

	<h2>Group Command Processing</h2>
	<br>
  
    <tags:bulkActionContainer key="yukon.common.device.group" deviceCollection="${deviceCollection}">
  
	<c:if test="${param.errorMsg != null}">
    		<div style="color: red;margin: 10px 0px;">Error: <spring:escapeBody htmlEscape="true">${param.errorMsg}</spring:escapeBody></div>
		<c:set var="errorMsg" value="" scope="request"/>
	</c:if>
	
	<br>
	<div style="width: 700px;">
		<form id="collectionProcessingForm" action="<cti:url value="/spring/group/commander/executeCollectionCommand" />" method="post">
        
			<cti:deviceCollection deviceCollection="${deviceCollection}" />
      
      		<%-- SELECT COMMAND --%>
            <amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}"/>
            <br><br>
            
            <%-- EMAIL --%>
            <div class="largeBoldLabel">Email Address (optional):</div>
            <input type="text" name="emailAddress" value="" size="40">
            <br><br>

            <tags:slowInput myFormId="collectionProcessingForm" labelBusy="Execute" label="Execute"></tags:slowInput>
			
		</form>
	</div>
  
  </tags:bulkActionContainer>
	
</cti:standardPage>