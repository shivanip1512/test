<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:msg var="pageTitle" key="yukon.common.device.groupMeterRead.home.pageTitle"/>
<cti:msg var="selectAttributeLabel" key="yukon.common.device.groupMeterRead.home.selectAttributeLabel"/>
<cti:msg var="selectGroupLabel" key="yukon.common.device.groupMeterRead.home.selectGroupLabel"/>
<cti:msg var="recentResultLinkLabel" key="yukon.common.device.groupMeterRead.home.recentResultsLinkLabel"/>
<cti:msg var="recentResultLink" key="yukon.common.device.groupMeterRead.home.recentResultsLink"/>
<cti:msg var="readButtonText" key="yukon.common.device.groupMeterRead.home.readButton"/>
    
<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="devicegroups|groupMeterRead"/>

       	<cti:breadCrumbs>
    	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
    	    <cti:crumbLink url="/spring/group/editor/home" title="Device Groups" />
    	    <cti:crumbLink title="${pageTitle}"/>
    	</cti:breadCrumbs>
        

        <script type="text/javascript">
        
        </script>
		
		<h2>${pageTitle}</h2>
    	
    	<c:if test="${not empty errorMsg}">
    		<br>
    		<div class="errorRed">${errorMsg}</div>
    	</c:if>
	
    	<br>
        
		<form id="groupMeterReadForm" action="/spring/group/groupMeterRead/readGroup" method="post">
		
			<%-- SELECT ATTRIBUTE --%>
	        <div class="largeBoldLabel">${selectAttributeLabel}:</div>
	        <amr:attributeSelector attributes="${allAttributes}" fieldName="attribute" selectedAttributes="${selectedAttributes}" multipleSize="5"/>
			<br>
			
			
			<%-- SELECT DEVICE GROUP TREE INPUT --%>
			<br>
			<div class="largeBoldLabel">${selectGroupLabel}</div>
			
			<cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="dataJson" selectGroupName="${groupName}" selectedNodePathVar="selectedNodePath"/>
			<jsTree:nodeValueSelectingInlineTree   fieldId="groupName" 
			                                    fieldName="groupName"
			                                    nodeValueName="groupName" 
			                                    fieldValue="${groupName}"
			                                    multiSelect="false"
			                                    id="selectGroupTree" 
			                                    dataJson="${dataJson}" 
			                                    width="500"
			                                    height="400"
                                                includeControlBar="true"
			                                    highlightNodePath="${selectedNodePath}" />
			                                    
			      
			      
			<%-- READ BUTTON --%>
			<tags:slowInput myFormId="groupMeterReadForm" labelBusy="${readButtonText}" label="${readButtonText}"/>
			
			<%-- RECENT RESULTS --%>
			<br><br>
			<span class="largeBoldLabel">${recentResultLinkLabel}</span> 
			<a href="/spring/group/groupMeterRead/resultsList">${recentResultLink}</a>
			 
		</form>
	
</cti:standardPage>