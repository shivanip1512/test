<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
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
  
	<%-- ERROR MSG --%>
    <c:if test="${not empty param.errorMsg}">
    	<div class="errorRed">${param.errorMsg}</div>
    	<c:set var="errorMsg" value="" scope="request"/>
    	<br>
    </c:if>
	
	<div style="width: 700px;">
		<form id="collectionProcessingForm" action="<cti:url value="/spring/group/commander/executeCollectionCommand" />" method="post">
        
			<cti:deviceCollection deviceCollection="${deviceCollection}" />
      
      		<%-- SELECT COMMAND --%>
      		<cti:msg var="selectCommandLabel" key="yukon.common.device.commander.commandSelector.selectCommand"/>
      		<div class="largeBoldLabel">${selectCommandLabel}:</div>
            <amr:commandSelector selectName="commandSelectValue" fieldName="commandString" commands="${commands}" selectedCommandString="${param.commandString}" selectedSelectValue="${param.commandSelectValue}" includeDummyOption="true" />
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