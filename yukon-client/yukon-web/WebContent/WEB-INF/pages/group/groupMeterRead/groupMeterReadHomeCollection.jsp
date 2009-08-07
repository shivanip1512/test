<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:msg var="pageTitle" key="yukon.common.device.groupMeterRead.home.pageTitle"/>
<cti:msg var="selectAttributeLabel" key="yukon.common.device.groupMeterRead.home.selectAttributeLabel"/>
<cti:msg var="readButtonText" key="yukon.common.device.groupMeterRead.home.readButton"/>
    
<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="devicegroups|groupMeterRead"/>

	<cti:breadCrumbs>
   	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
   	    <cti:crumbLink title="${pageTitle}"/>
   	</cti:breadCrumbs>
      

	<script type="text/javascript">
	
	</script>

	<h2>${pageTitle}</h2>
   	<br>
   	
   	<tags:bulkActionContainer key="yukon.common.device.groupMeterRead.home" deviceCollection="${deviceCollection}">
   	
   		<form id="groupMeterReadForm" action="/spring/group/groupMeterRead/readCollection" method="post">
   		
	   		<cti:deviceCollection deviceCollection="${deviceCollection}" />
	   		
	   		<%-- SELECT ATTRIBUTE --%>
	        <div class="largeBoldLabel">${selectAttributeLabel}:</div>
	        <amr:attributeSelector attributes="${allAttributes}" fieldName="attribute" selectedAttribute="${attribute}" includeDummyOption="true"/>
			<br>
			
	   		<%-- READ BUTTON --%>
	   		<br>
			<tags:slowInput myFormId="groupMeterReadForm" labelBusy="${readButtonText}" label="${readButtonText}"/>
   	
   		</form>
   	
   	</tags:bulkActionContainer>
			 
	
</cti:standardPage>