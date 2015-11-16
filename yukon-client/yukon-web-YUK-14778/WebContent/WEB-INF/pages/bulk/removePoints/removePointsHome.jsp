<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:checkRolesAndProperties value="ADD_REMOVE_POINTS">
<cti:standardPage module="tools" page="bulk.removePointsHome">

<cti:includeScript link="/resources/js/pages/yukon.bulk.point.js"/>
    
<script type="text/javascript">
$(function(){
  var sharedPoints = ${sharedPoints};
  doToggleShowSharedPoints(sharedPoints);
});
</script>

    <tags:bulkActionContainer key="yukon.common.device.bulk.removePointsHome" deviceCollection="${deviceCollection}">
        
        <form id="executeRemovePointsForm" action="<cti:url value="/bulk/removePoints/execute" />">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- OPTIONS --%>
            <c:set var="optionNameWidth" value="150px" />
            <c:set var="selectInputWidth" value="300px" />
            <tags:sectionContainer2 nameKey="options">
                <tags:nameValueContainer>
                
                    <cti:msg var="sharedPointsOptionLabel" key="yukon.common.device.bulk.removePointsHome.sharedPointsOptionLabel"/>
                    <cti:msg var="sharedPointsFalseOptionText" key="yukon.common.device.bulk.removePointsHome.sharedPointsFalseOptionText"/>
                       <cti:msg var="sharedPointsFalseOptionTooltip" key="yukon.common.device.bulk.removePointsHome.sharedPointsFalseOptionTooltip"/>
                       <cti:msg var="sharedPointsTrueOptionText" key="yukon.common.device.bulk.removePointsHome.sharedPointsTrueOptionText"/>
                       <cti:msg var="sharedPointsTrueOptionTooltip" key="yukon.common.device.bulk.removePointsHome.sharedPointsTrueOptionTooltip"/>
                       <c:set var="sharedPointsFalseSelected" value="${sharedPoints ? '' : 'selected'}"/>
                       <c:set var="sharedPointsTrueSelected" value="${sharedPoints ? 'selected' : ''}"/>
                    <tags:nameValue name="${sharedPointsOptionLabel}" nameColumnWidth="${optionNameWidth}">
                        <select name="sharedPoints" style="width:${selectInputWidth};" onchange="toggleShowSharedPoints(this);" class="fl">
                            <option value="false" title="${sharedPointsFalseOptionTooltip}" ${sharedPointsFalseSelected}>${sharedPointsFalseOptionText}</option>
                            <option value="true" title="${sharedPointsTrueOptionTooltip}" ${sharedPointsTrueSelected}>${sharedPointsTrueOptionText}</option>
                        </select><cti:icon icon="icon-help" id="shared_help" classes="cp"/>
                    </tags:nameValue>
                    
                    <cti:msg var="maskMissingPointsOptionLabel" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsOptionLabel"/>
                    <cti:msg var="maskMissingPointsOptionDescription" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsOptionDescription"/>
                    <cti:msg var="maskMissingPointsFalseOptionText" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsFalseOptionText"/>
                    <cti:msg var="maskMissingPointsFalseOptionTooltip" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsFalseOptionTooltip"/>
                    <cti:msg var="maskMissingPointsTrueOptionText" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsTrueOptionText"/>
                    <cti:msg var="maskMissingPointsTrueOptionTooltip" key="yukon.common.device.bulk.removePointsHome.maskMissingPointsTrueOptionTooltip"/>
                    <input type="hidden" name="maskMissingPoints" value="${maskMissingPoints}">
                    <tags:nameValue name="${maskMissingPointsOptionLabel}">
                        <c:choose>
                            <c:when test="${not maskMissingPoints}">
                                <cti:button type="submit" 
                                    name="maskMissingPointsSubmitButton" 
                                    label="${maskMissingPointsFalseOptionText}" 
                                    value="${maskMissingPointsFalseOptionText}" 
                                    title="${maskMissingPointsFalseOptionTooltip}"/><cti:icon icon="icon-help" id="missing_help" classes="cp"/>
                            </c:when>
                            <c:otherwise>
                                <cti:button type="submit" 
                                    name="maskMissingPointsSubmitButton" 
                                    label="${maskMissingPointsTrueOptionText}" 
                                    value="${maskMissingPointsTrueOptionText}" 
                                    title="${maskMissingPointsTrueOptionTooltip}"/><cti:icon icon="icon-help" id="missing_help" classes="cp"/>
                            </c:otherwise>
                        </c:choose>
                    </tags:nameValue>
                    
                </tags:nameValueContainer>
            </tags:sectionContainer2>
            
            <tags:simplePopup id="maskMissingPointsOptionInfoPopup" title="${maskMissingPointsOptionLabel}" on="#missing_help" options="{width: 600}">
                <div class="warning stacked"><i:inline key="yukon.common.warningMessage" arguments="${maskMissingPointsOptionDescription}"/></div>
                <tags:nameValueContainer>
                    <tags:nameValue nameColumnWidth="30%" name="${maskMissingPointsFalseOptionText}">${maskMissingPointsFalseOptionTooltip}</tags:nameValue>
                    <tags:nameValue nameColumnWidth="30%" name="${maskMissingPointsTrueOptionText}">${maskMissingPointsTrueOptionTooltip}</tags:nameValue>
                </tags:nameValueContainer>
            </tags:simplePopup>
            
            <tags:simplePopup id="sharedPointsOptionInfoPopup" title="${sharedPointsOptionLabel}" on="#shared_help" options="{width: 600}">
                <tags:nameValueContainer>
                    <tags:nameValue nameColumnWidth="30%" name="${sharedPointsFalseOptionText}">${sharedPointsFalseOptionTooltip}</tags:nameValue>
                    <tags:nameValue nameColumnWidth="30%" name="${sharedPointsTrueOptionText}">${sharedPointsTrueOptionTooltip}</tags:nameValue>
                </tags:nameValueContainer>
            </tags:simplePopup>
            
            <%-- SHARED POINTS --%>
            <div id="sharedPointsDiv" style="display:none;">
                <tags:deviceTypePointsSection paoTypeMasks="${sharedPaoTypeMasks}" columnCount="4" />
            </div>
            
            <%-- TYPES --%>
            <div id="allPointsDiv">
                <c:forEach var="paoTypeMasks" items="${paoTypeMasksList}">
                    <c:set var="deviceType" value="${paoTypeMasks.paoType}"/>
                    <c:if test="${paoTypeMasks.paoType eq deviceType}">
                        <tags:deviceTypePointsSection deviceType="${deviceType}" 
                                                      deviceTypeDeviceCollection="${deviceTypeDeviceCollectionMap[deviceType]}" 
                                                      paoTypeMasks="${paoTypeMasks}" 
                                                      columnCount="4" />
                    </c:if>
                </c:forEach>
            </div>
                    
            <%-- REMOVE POINTS BUTTON --%>
            <cti:button nameKey="remove" classes="action primary" type="submit"/>

        </form>
        
    </tags:bulkActionContainer>
                    
</cti:standardPage>
</cti:checkRolesAndProperties>