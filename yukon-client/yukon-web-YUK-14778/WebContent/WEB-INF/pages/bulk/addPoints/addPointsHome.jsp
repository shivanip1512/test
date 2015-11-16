<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage page="bulk.addPointsHome" module="tools">
<cti:checkRolesAndProperties value="ADD_REMOVE_POINTS">

<cti:includeScript link="/resources/js/pages/yukon.bulk.point.js"/>

<script type="text/javascript">
$(function() {
  var sharedPoints = ${sharedPoints};
  doToggleShowSharedPoints(sharedPoints);
});
</script>

    <tags:bulkActionContainer key="yukon.common.device.bulk.addPointsHome" deviceCollection="${deviceCollection}">
        
        <form id="executeAddPointsForm" action="<cti:url value="/bulk/addPoints/execute" />">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- OPTIONS --%>
            <c:set var="optionNameWidth" value="150px" />
            <c:set var="selectInputWidth" value="300px" />
            <tags:sectionContainer2 nameKey="options">
                <tags:nameValueContainer>
                    <cti:msg var="sharedPointsOptionLabel" key="yukon.common.device.bulk.addPointsHome.sharedPointsOptionLabel"/>
                    <cti:msg var="sharedPointsTrueOptionText" key="yukon.common.device.bulk.addPointsHome.sharedPointsTrueOptionText"/>
                       <cti:msg var="sharedPointsTrueOptionTooltip" key="yukon.common.device.bulk.addPointsHome.sharedPointsTrueOptionTooltip"/>
                       <cti:msg var="sharedPointsFalseOptionText" key="yukon.common.device.bulk.addPointsHome.sharedPointsFalseOptionText"/>
                       <cti:msg var="sharedPointsFalseOptionTooltip" key="yukon.common.device.bulk.addPointsHome.sharedPointsFalseOptionTooltip"/>
                       <c:set var="sharedPointsTrueSelected" value="${sharedPoints ? 'selected' : ''}"/>
                       <c:set var="sharedPointsFalseSelected" value="${sharedPoints ? '' : 'selected'}"/>
                    <tags:nameValue name="${sharedPointsOptionLabel}" nameColumnWidth="${optionNameWidth}">
                        <select name="sharedPoints" style="width:${selectInputWidth};" onchange="toggleShowSharedPoints(this);" class="fl">
                            <option value="true" title="${sharedPointsTrueOptionTooltip}" ${sharedPointsTrueSelected}>${sharedPointsTrueOptionText}</option>
                            <option value="false" title="${sharedPointsFalseOptionTooltip}" ${sharedPointsFalseSelected}>${sharedPointsFalseOptionText}</option>
                        </select> <cti:icon icon="icon-help" id="shared_help" classes="cp"/>
                    </tags:nameValue>
                    <cti:msg var="updatePointsOptionLabel" key="yukon.common.device.bulk.addPointsHome.updatePointsOptionLabel"/>
                    <cti:msg var="updatePointsFalseOptionText" key="yukon.common.device.bulk.addPointsHome.updatePointsFalseOptionText"/>
                       <cti:msg var="updatePointsFalseOptionTooltip" key="yukon.common.device.bulk.addPointsHome.updatePointsFalseOptionTooltip"/>
                       <cti:msg var="updatePointsTrueOptionText" key="yukon.common.device.bulk.addPointsHome.updatePointsTrueOptionText"/>
                       <cti:msg var="updatePointsTrueOptionTooltip" key="yukon.common.device.bulk.addPointsHome.updatePointsTrueOptionTooltip"/>
                       <c:set var="updatePointsFalseSelected" value="${updatePoints ? '' : 'selected'}"/>
                       <c:set var="updatePointsTrueSelected" value="${updatePoints ? 'selected' : ''}"/>
                     <tags:nameValue name="${updatePointsOptionLabel}" id="updatePointsSelectTr">
                           <select id="updatePointsSelectEl" name="updatePoints" style="width:${selectInputWidth};" class="fl">
                               <option value="false" title="${updatePointsFalseOptionTooltip}" ${updatePointsFalseSelected}>${updatePointsFalseOptionText}</option>
                               <option value="true" title="${updatePointsTrueOptionTooltip}" ${updatePointsTrueSelected}>${updatePointsTrueOptionText}</option>
                           </select>
                            <cti:icon icon="icon-help" id="update_help" classes="cp"/>
                    </tags:nameValue>
                    
                    <cti:msg var="maskExistingPointsOptionLabel" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsOptionLabel"/>
                    <cti:msg var="maskExistingPointsOptionDescription" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsOptionDescription"/>
                    <cti:msg var="maskExistingPointsFalseOptionText" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsFalseOptionText"/>
                       <cti:msg var="maskExistingPointsFalseOptionTooltip" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsFalseOptionTooltip"/>
                       <cti:msg var="maskExistingPointsTrueOptionText" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsTrueOptionText"/>
                       <cti:msg var="maskExistingPointsTrueOptionTooltip" key="yukon.common.device.bulk.addPointsHome.maskExistingPointsTrueOptionTooltip"/>
                       <input type="hidden" name="maskExistingPoints" value="${maskExistingPoints}">
                    <tags:nameValue name="${maskExistingPointsOptionLabel}">
                        <c:choose>
                            <c:when test="${not maskExistingPoints}">
                                <cti:button type="submit"
                                    name="maskExistingPointsSubmitButton"
                                    label="${maskExistingPointsFalseOptionText}"
                                    value="${maskExistingPointsFalseOptionText}"
                                    title="${maskExistingPointsFalseOptionTooltip}"/><cti:icon icon="icon-help" id="existing_help" classes="cp"/>
                            </c:when>
                            <c:otherwise>
                                <cti:button type="submit" 
                                    name="maskExistingPointsSubmitButton" 
                                    label="${maskExistingPointsTrueOptionText}" 
                                    value="${maskExistingPointsTrueOptionText}" 
                                    title="${maskExistingPointsTrueOptionTooltip}"/><cti:icon icon="icon-help" id="existing_help" classes="cp"/>
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
            
            <tags:simplePopup id="updatePointsOptionInfoPopup" title="${updatePointsOptionLabel}" on="#update_help" options="{width: 600}">
                <tags:nameValueContainer>
                    <tags:nameValue nameColumnWidth="30%" name="${updatePointsFalseOptionText}">${updatePointsFalseOptionTooltip}</tags:nameValue>
                    <tags:nameValue nameColumnWidth="30%" name="${updatePointsTrueOptionText}">${updatePointsTrueOptionTooltip}</tags:nameValue>
                </tags:nameValueContainer>
            </tags:simplePopup>
            
            <tags:simplePopup id="maskExistingPointsOptionInfoPopup" title="${maskExistingPointsOptionLabel}" on="#existing_help" options="{width: 600}">
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
                    
            <%-- ADD POINTS BUTTON --%>
            <cti:button nameKey="add" classes="action primary" type="submit"/>
            
        </form>
<c:if test="${not empty preselectedPointIdentifiers}">
<script type="text/javascript">
<c:forEach var="pointTypeOffset" items="${preselectedPointIdentifiers}">
    $("[name$='${pointTypeOffset}']").attr("checked","checked");
    $("[name$='${pointTypeOffset}']").closest('td').flash();
</c:forEach>
</script>
</c:if>
    </tags:bulkActionContainer>
                    
</cti:checkRolesAndProperties>
</cti:standardPage>