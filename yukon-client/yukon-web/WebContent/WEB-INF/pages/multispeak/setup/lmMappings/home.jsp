<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage module="adminSetup" page="lmMappings">
  <cti:includeScript link="/JavaScript/lmMappings.js"/>

<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <c:url var="tab_url" value="/multispeak/setup/home" />
    <cti:msg var="tab_name" key="yukon.web.modules.adminSetup.interfaces.home.tab.title" />
    <c:set var="on_tab" value='${isDevicesPage}'/>
    <cti:linkTab tabId="deviceTab" selectorName="${tab_name}" tabHref="${tab_url}"/>

    <c:url var="tab_url" value="/multispeak/setup/lmMappings/home" />
    <cti:msg var="tab_name" key="yukon.web.modules.adminSetup.lmMappings.tab.title" />
    <c:set var="on_tab" value='${isLoadMngtPage}'/>
    <cti:linkTab tabId="loadMgtTab" selectorName="${tab_name}" tabHref="${tab_url}" initiallySelected="${true}"/>

    <c:url var="tab_url" value="/multispeak/setup/deviceGroupSync/home" />
    <cti:msg var="tab_name" key="yukon.web.modules.adminSetup.deviceGroupSyncHome.tab.title" />
    <cti:linkTab tabId="deviceGroupTab" selectorName="${tab_name}" tabHref="${tab_url}"/>
</cti:linkTabbedContainer>
  
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
					    		 <tags:pickerDialog id="paoPicker"
                                    type="lmProgramOrScenarioPicker"
                                    destinationFieldId="mappedNameId"
                                    extraDestinationFields="paoName:mappedName;" 
                                    endAction="setMappedNameId"
                                    linkType="none"/> 
					    		<input type="button" id="searchButton" value="Search" onclick="doLmMappingNameSearch();" class="formSubmit">
					    		<input type="button" id="addButton" value="Set New Mapping" onclick="validateAndShow();" class="formSubmit">
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