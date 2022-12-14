<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="dev" page="rfnTest.viewMappingSimulator">
    <form:form id="mapping-form" action="startMappingSimulator" modelAttribute="currentSettings" method="POST">
        <cti:csrfToken/>
    
    <div class="column-12-12 clearfix">
        <div class="column one">
        
        <tags:sectionContainer title="NM Multi Metadata Response">
            <tags:nameValueContainer tableClass="natural-width">
                <tags:nameValue name="Response Type">
                    <tags:selectWithItems items="${metadataResponseTypes}" path="metadataResponseType" />
                </tags:nameValue>
                <tags:nameValue name="Device Response Type">
                    <tags:selectWithItems items="${metadataQueryResponseTypes}" path="metadataQueryResponseType" />
                </tags:nameValue>
                <tags:nameValue name="Message">
                    <tags:input path="metadataResponseString"/>
                </tags:nameValue>
            </tags:nameValueContainer>
        </tags:sectionContainer>
    
    <tags:sectionContainer title="Neighbor Data Settings">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Address">
                <tags:input path="neighborData.neighborMacAddress"/>
            </tags:nameValue>
            <tags:nameValue name="Flags">
                <c:forEach var="flag" items="${neighborFlags}">
                    <c:set var="checked" value=""/>
                    <c:if test="${fn:contains(currentSettings.neighborData.neighborFlags, flag)}">
                        <c:set var="checked" value="checked"/>
                    </c:if>
                    <input type="checkbox" name="neighborFlag_${flag}" ${checked}/><i:inline key="yukon.web.modules.operator.mapNetwork.neighborFlag.${flag}"/><br>
                </c:forEach>
            </tags:nameValue>
           <tags:nameValue name="Link Rate">
               <tags:selectWithItems items="${currentLinkRate}" path="neighborData.currentLinkRate" />
            </tags:nameValue>
            <tags:nameValue name="Link Power">
               <tags:selectWithItems items="${currentLinkPower}" path="neighborData.currentLinkPower" />
            </tags:nameValue>
        </tags:nameValueContainer>
    </tags:sectionContainer>
    
    </div>
    
    <div class="column two nogutter">
    
    <tags:sectionContainer title="Primary Route Settings">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Path Cost">
                <tags:input path="routeData.totalCost"/>
            </tags:nameValue>
            <tags:nameValue name="Hop Count">
                <tags:input path="routeData.hopCount"/>
            </tags:nameValue>
            <tags:nameValue name="Flags">
                <c:forEach var="flag" items="${routeFlags}">
                    <c:set var="checked" value=""/>
                    <c:if test="${fn:contains(currentSettings.routeData.routeFlags, flag)}">
                        <c:set var="checked" value="checked"/>
                    </c:if>
                    <input type="checkbox" name="routeFlag_${flag}" ${checked}/><i:inline key="yukon.web.modules.operator.mapNetwork.routeFlag.${flag}"/><br>
                </c:forEach>
            </tags:nameValue>
            <tags:nameValue name="Route Color">
                <tags:input path="routeData.routeColor"/>
            </tags:nameValue>
        </tags:nameValueContainer>
    </tags:sectionContainer>
    
    <tags:sectionContainer title="Network Tree Settings">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="% of Empty/Nulls">
                <tags:input path="emptyNullPercent" size="5"/>%
            </tags:nameValue>
        </tags:nameValueContainer>
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="# of Nodes at First Hop">
                <tags:input path="nodesOneHop" size="5"/>
            </tags:nameValue>
        </tags:nameValueContainer>
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Devices Per Hop">
                <tags:input path="maxHop" size="5"/>
            </tags:nameValue>
        </tags:nameValueContainer>
      </tags:sectionContainer>
      <tags:sectionContainer title="Populate Database Settings">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="# of Devices per Gateway">
                <tags:input path="numberOfDevicesPerGateway" size="5"/>
            </tags:nameValue>
        </tags:nameValueContainer>
        <tags:nameValueContainer tableClass="natural-width">
         	<tags:nameValue name="Create Gateways">
            	<tags:checkbox path="createGateways"/>
            </tags:nameValue>
        </tags:nameValueContainer>
    </tags:sectionContainer>
    
    </div>
    </div>

    <cti:button id="populateButton" label="Populate Database"/>
    <d:confirm on="#populateButton" nameKey="confirmPopulate" okClasses="populateDatabase"/>

    <c:if test="${simulatorRunning}">
        <cti:button id="updateSettings" busy="true" label="Update Settings"/>
    </c:if>
    
    <cti:button id="refreshNetworkTree" busy="true" label="Refresh Network Tree"/>
        
    <c:if test="${not simulatorRunning}">
        <cti:button label="Start Simulator" busy="true" type="submit"/>
    </c:if>
    
    </form:form>
    
    <c:if test="${simulatorRunning}">
        <cti:button id="stopSimulator" busy="true" label="Stop Simulator"/>
    </c:if>
    
    <div class="button-group button-group-toggle">
        <div class="js-sim-startup" data-simulator-type="RFN_NETWORK">
            <cti:button nameKey="runSimulatorOnStartup.automatic" classes="yes enable-startup"/>
            <cti:button nameKey="runSimulatorOnStartup.manual" classes="no disable-startup"/>
        </div>  
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.mappingSimulator.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.simulatorStartup.js" />
</cti:standardPage>