<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:msg var="pageTitle" key="yukon.web.modules.multispeak.lmMappings.pageTitle" />

<cti:standardPage title="${pageTitle}" module="multispeak">

	<cti:standardMenu menuSelection="multispeak|mappings"/>
	
	<cti:includeScript link="/JavaScript/lmMappings.js"/>
	
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/msp_setup.jsp" title="MultiSpeak"  />
	    &gt; ${pageTitle}
	</cti:breadCrumbs>
	
	<h2>${pageTitle}</h2>
    
    <br>
    <tags:boxContainer title="${pageTitle}" id="container" hideEnabled="false">

		<%-- FIND/ADD MAPPINGS --%>
		<br>
		<table width="95%">
			<tr valign="top">
				<%-- FIND MAPPING --%>
				<td width="50%">
					<tags:sectionContainer title="Find/Set Mapping" id="findMappingsSection">
						<tags:nameValueContainer>
						
					    	<tags:nameValue name="Strategy" nameColumnWidth="200px">
					    		<input type="text" id="strategyName" name="strategyName" value="${strategyName}">
					    	</tags:nameValue>
					    
					    	<tags:nameValue name="Substation" nameColumnWidth="200px">
					    		<input type="text" id="substationName" name="substationName" value="${substationName}">
					    	</tags:nameValue>
					    
					    	<tags:nameValue name="Program/Scenario" nameColumnWidth="200px">
					    		<input type="hidden" id="mappedNameId" name="mappedNameId" value="">
					    		<span id="mappedName" style="display:none;"></span>
					    		
					    		<c:choose>
						    		<c:when test="${not empty mappedName}">
						    			<span id="mappedNameDisplay"><spring:escapeBody htmlEscape="true">${mappedName}</spring:escapeBody></span>
						    		</c:when>
						    		<c:otherwise>
						    			<span id="mappedNameDisplay">Not Found</span>
						    		</c:otherwise>
					    		</c:choose>
					    	</tags:nameValue>
					    
					    	<tags:nameValue name="" nameColumnWidth="200px">
					    		
					    		<cti:paoPicker pickerId="paoPicker" 	
					    					paoIdField="mappedNameId" 
					    					paoNameElement="mappedName"
					    					constraint="com.cannontech.common.search.criteria.LMProgramOrScenarioCriteria" 
					    					finalTriggerAction="setMappedNameId">
	                        	</cti:paoPicker>
	                        	
					    		<input type="button" id="searchButton" value="Search" onclick="doSearch();">
					    		<input type="button" id="addButton" value="Set New Mapping" onclick="paoPicker.showPicker();">
					    		<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;" id="waitImg">
	                        
					    	</tags:nameValue>
					    	
					    </tags:nameValueContainer>
					</tags:sectionContainer>
				</td>
				
			</tr>
		</table>
		
		
		<%-- ALL MAPPINGS --%>
		<br>
		<tags:sectionContainer title="All Mappings" id="allMappingsSection">
		
			<div id="allMappingsTableDiv">
				<c:url var="allMappingsTableUrl" value="/WEB-INF/pages/multispeak/setup/lmMappings/allMappingsTable.jsp" />
    			<jsp:include page="${allMappingsTableUrl}"/>
    		</div>
					    		
		</tags:sectionContainer>
	    
	    
	</tags:boxContainer>

</cti:standardPage>