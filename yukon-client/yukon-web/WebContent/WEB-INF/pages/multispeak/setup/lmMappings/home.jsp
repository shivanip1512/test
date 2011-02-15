<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage module="adminSetup" page="lmMappings">

	<cti:includeScript link="/JavaScript/lmMappings.js"/>
	
    <tags:boxContainer2 nameKey="mappingsContainer" id="container" hideEnabled="false">

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
	                        	
					    		<input type="button" id="searchButton" value="Search" onclick="doLmMappingNameSearch();" class="formSubmit">
					    		<input type="button" id="addButton" value="Set New Mapping" onclick="paoPicker.showPicker();" class="formSubmit">
					    		<img src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;" id="waitImg">
	                        
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
				<cti:url var="allMappingsTableUrl" value="/WEB-INF/pages/multispeak/setup/lmMappings/allMappingsTable.jsp" />
    			<jsp:include page="${allMappingsTableUrl}"/>
    		</div>
					    		
		</tags:sectionContainer>
	    
	    
	</tags:boxContainer2>

</cti:standardPage>