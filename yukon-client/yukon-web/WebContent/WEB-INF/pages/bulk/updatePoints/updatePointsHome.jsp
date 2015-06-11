<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:checkRolesAndProperties value="ADD_REMOVE_POINTS">

<cti:standardPage module="tools" page="bulk.updatePointsHome">

<cti:includeScript link="/resources/js/pages/yukon.bulk.point.js"/>
<script type="text/javascript">

$(function(){
  var sharedPoints = ${sharedPoints};
  doToggleShowSharedPoints(sharedPoints);
});

function fieldToModifyChanged() {
  $('#fieldSpecificOptions').load('/bulk/updatePoints/getSpecificOptions');
}
</script>

    <tags:bulkActionContainer key="yukon.common.device.bulk.updatePointsHome" deviceCollection="${deviceCollection}">
        
        <form id="executeUpdatePointsForm" action="<cti:url value="/bulk/updatePoints/execute" />">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- OPTIONS --%>
            <tags:sectionContainer2 nameKey="options" styleClass="half-width">
                <tags:nameValueContainer>
                    <cti:msg var="sharedPointsOptionLabel" key="yukon.common.device.bulk.updatePointsHome.sharedPointsOptionLabel"/>
                    <cti:msg var="sharedPointsTrueOptionText" key="yukon.common.device.bulk.updatePointsHome.sharedPointsTrueOptionText"/>
                    <cti:msg var="sharedPointsTrueOptionTooltip" key="yukon.common.device.bulk.updatePointsHome.sharedPointsTrueOptionTooltip"/>
                    <cti:msg var="sharedPointsFalseOptionText" key="yukon.common.device.bulk.updatePointsHome.sharedPointsFalseOptionText"/>
                    <cti:msg var="sharedPointsFalseOptionTooltip" key="yukon.common.device.bulk.updatePointsHome.sharedPointsFalseOptionTooltip"/>
                    <c:set var="sharedPointsTrueSelected" value="${sharedPoints ? 'selected' : ''}"/>
                    <c:set var="sharedPointsFalseSelected" value="${sharedPoints ? '' : 'selected'}"/>
                    <tags:nameValue name="${sharedPointsOptionLabel}">
                        <select name="sharedPoints" onchange="toggleShowSharedPoints(this);" class="fl">
                            <option value="true" title="${sharedPointsTrueOptionTooltip}" ${sharedPointsTrueSelected}>${sharedPointsTrueOptionText}</option>
                            <option value="false" title="${sharedPointsFalseOptionTooltip}" ${sharedPointsFalseSelected}>${sharedPointsFalseOptionText}</option>
                        </select><cti:icon id="shared_help" icon="icon-help" classes="cp"/>
                    </tags:nameValue>
                
                    <cti:msg var="maskExistingPointsOptionLabel" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsOptionLabel"/>
                    <cti:msg var="maskExistingPointsOptionDescription" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsOptionDescription"/>
                    <cti:msg var="maskExistingPointsFalseOptionText" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsFalseOptionText"/>
                    <cti:msg var="maskExistingPointsFalseOptionTooltip" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsFalseOptionTooltip"/>
                    <cti:msg var="maskExistingPointsTrueOptionText" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsTrueOptionText"/>
                    <cti:msg var="maskExistingPointsTrueOptionTooltip" key="yukon.common.device.bulk.updatePointsHome.maskExistingPointsTrueOptionTooltip"/>
                    <input type="hidden" name="maskExistingPoints" value="${maskExistingPoints}">
                    <tags:nameValue name="${maskExistingPointsOptionLabel}">
                        <c:choose>
                            <c:when test="${not maskExistingPoints}">
                                <cti:button type="submit" 
                                    name="maskExistingPointsSubmitButton" 
                                    label="${maskExistingPointsFalseOptionText}" 
                                    value="${maskExistingPointsFalseOptionText}" 
                                    title="${maskExistingPointsFalseOptionTooltip}"/>
                                <cti:icon icon="icon-help" id="mask_help" classes="cp"/>
                            </c:when>
                            <c:otherwise>
                                <cti:button type="submit" 
                                    name="maskExistingPointsSubmitButton" 
                                    label="${maskExistingPointsTrueOptionText}" 
                                    value="${maskExistingPointsTrueOptionText}" 
                                    title="${maskExistingPointsTrueOptionTooltip}"/>
                                <cti:icon icon="icon-help" id="mask_help" classes="cp"/>
                            </c:otherwise>
                        </c:choose>
                    </tags:nameValue>
                
                </tags:nameValueContainer>
            </tags:sectionContainer2>
            
            <tags:simplePopup id="sharedPointsOptionInfoPopup" title="${sharedPointsOptionLabel}" on="#shared_help" options="{width: 600}">
                 <tags:nameValueContainer>
                    <tags:nameValue nameColumnWidth="30%" name="${sharedPointsFalseOptionText}">${sharedPointsFalseOptionTooltip}</tags:nameValue>
                    <tags:nameValue nameColumnWidth="30%" name="${sharedPointsTrueOptionText}">${sharedPointsTrueOptionTooltip}</tags:nameValue>
                 </tags:nameValueContainer>
            </tags:simplePopup>
            
            <tags:simplePopup id="maskExistingPointsOptionInfoPopup" title="${maskExistingPointsOptionLabel}" on="#mask_help" options="{width: 600}">
                 <div class="warning stacked"><i:inline key="yukon.common.warningMessage" arguments="${maskExistingPointsOptionDescription}"/></div>
                 <tags:nameValueContainer>
                    <tags:nameValue nameColumnWidth="30%" name="${maskExistingPointsFalseOptionText}">${maskExistingPointsFalseOptionTooltip}</tags:nameValue>
                    <tags:nameValue nameColumnWidth="30%" name="${maskExistingPointsTrueOptionText}">${maskExistingPointsTrueOptionTooltip}</tags:nameValue>
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
            
            <%-- UPDATE POINTS OPTIONS --%>
            <div id="updateOptionsDiv" class="page-action-area">
                <span class="fl">Select update type: </span>
                <select id="fieldToModify" name="fieldToModify" class="fl" style="margin-left: 10px;">
                    <c:forEach var="pointField" items="${pointFields}">
                        <option value="${pointField}">${pointField.displayValue}</option>
                    </c:forEach>
                </select>
                <span class="fl" style="margin-left: 10px;">Value: </span>
                <input type="text" name="setValue" class="fl" style="margin-left: 10px;">
                <cti:button nameKey="update" classes="action primary" type="submit"/>
            </div>
        </form>

    </tags:bulkActionContainer>
                    
</cti:standardPage>
</cti:checkRolesAndProperties>