<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage module="adminSetup" page="lmMappings">
  <cti:includeScript link="/JavaScript/yukon.substation.mappings.js"/>

<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.modules.adminSetup.interfaces.home.tab.title">
        <c:url value="/multispeak/setup/home" />
    </cti:linkTab>

    <cti:linkTab tabId="loadMgtTab" selectorKey="yukon.web.modules.adminSetup.lmMappings.tab.title" initiallySelected="${true}">
        <c:url value="/multispeak/setup/lmMappings/home" />
    </cti:linkTab>

    <cti:linkTab tabId="deviceGroupTab" selectorKey="yukon.web.modules.adminSetup.deviceGroupSyncHome.tab.title">
        <c:url value="/multispeak/setup/deviceGroupSync/home" />
    </cti:linkTab>
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
                                <cti:button id="searchButton" nameKey="search" onclick="doLmMappingNameSearch();"/>
                                <cti:button id="addButton" label="Set New Mapping" onclick="validateAndShow();"/>
                                <img src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>" style="display:none;" id="waitImg">
                            
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
                <jsp:include page="allMappingsTable.jsp"/>
            </div>
                                
        </tags:sectionContainer>
        
        
    </tags:boxContainer2>

</cti:standardPage>