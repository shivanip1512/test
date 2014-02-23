<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="statusPointMonitorEditor">

    <cti:includeScript link="/JavaScript/yukon.monitor.status.point.js"/>
    
    <cti:msg2 var="deviceGroupTitle" key=".popupInfo.deviceGroup.title"/>
    
    <table class="dn">
        <tr class="f-template-row" data-row="0">
            <td>
                <select name="processors[?].prevState" class="f-row-prev-state">
                    <c:forEach items="${statusPointMonitor.stateGroup.statesList}" var="state">
                        <option value="${state.liteID}">${fn:escapeXml(state.stateText)}</option>
                    </c:forEach>
                    <option value="${dontCare}"><cti:msg2 key=".state.dontCare"/></option>
                    <option value="${difference}"><cti:msg2 key=".state.difference"/></option>
                </select>
            </td>
            <td>
                <select name="processors[?].nextState" class="f-row-next-state">
                    <c:forEach items="${statusPointMonitor.stateGroup.statesList}" var="state">
                        <option value="${state.liteID}">${fn:escapeXml(state.stateText)}</option>
                    </c:forEach>
                    <option value="${dontCare}"><cti:msg2 key=".state.dontCare"/></option>
                    <option value="${difference}"><cti:msg2 key=".state.difference"/></option>
                </select>
            </td>
            <td>
                <select name="processors[?].actionType" class="f-row-action-type">
                    <c:forEach items="${eventTypes}" var="eventType">
                        <option value="${eventType}">${eventType}</option>
                    </c:forEach>
                </select>
                <cti:button nameKey="delete" renderMode="buttonImage" icon="icon-cross" classes="f-remove fr"/>
            </td>
        </tr>
    </table>
    
    <cti:url var="submitUrl" value="/amr/statusPointMonitoring/delete"/>
    <form id="deleteStatusPointMonitor" action="${submitUrl}" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}">
    </form>

    <%-- MISC FORMS --%>
    <form id="toggleEnabledForm" action="/amr/statusPointMonitoring/toggleEnabled" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}">
    </form>
    
    <%-- UPDATE FORM --%>
    <cti:url var="submitUrl" value="/amr/statusPointMonitoring/update"/>
    <form:form commandName="statusPointMonitor" action="${submitUrl}" method="post">
    
        <form:hidden path="statusPointMonitorId"/>
        <form:hidden path="evaluatorStatus"/>
        <form:hidden path="stateGroup"/>
        
        <tags:sectionContainer2 nameKey="sectionHeader">
            <tags:nameValueContainer2>
            
                <%-- name --%>
                <tags:inputNameValue nameKey=".name" path="statusPointMonitorName" size="50" maxlength="50"/>
                
                <%-- device group --%>
                <tags:nameValue2 nameKey=".deviceGroup">
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="groupName" 
                                                    fieldValue="${statusPointMonitor.groupName}" 
                                                  dataJson="${groupDataJson}"
                                                  linkGroupName="true"/>
                    <tags:helpInfoPopup title="${deviceGroupTitle}">
                        <cti:msg2 key=".popupInfo.deviceGroup"/>
                    </tags:helpInfoPopup>
                </tags:nameValue2>
                
                <%-- attribute --%>
                <tags:nameValue2 nameKey="yukon.common.device.commander.attributeSelector.selectAttribute">
                    <form:select path="attribute">
                        <c:forEach items="${allGroupedReadableAttributes}" var="group">
                            <optgroup label="<cti:msg2 key="${group.key}"/>">
                                <c:forEach items="${group.value}" var="item">
                                    <c:set var="selected" value=""/>
                                    <c:if test="${statusPointMonitor.attribute == item}">
                                        <c:set var="selected" value="selected"/>
                                    </c:if>
                                    <option value="${item.key}" ${selected}>
                                        <cti:formatObject value="${item}"/>
                                    </option>
                                </c:forEach>
                            </optgroup>
                        </c:forEach>
                    </form:select>
                </tags:nameValue2>
                
                <%-- State Group --%>
                <tags:nameValue2 nameKey=".stateGroup">
                    ${statusPointMonitor.stateGroup}
                </tags:nameValue2>
                
                <%-- enable/disable monitoring --%>
                <c:if test="${statusPointMonitor.enabled}"><c:set var="clazz" value="success"/></c:if>
                <c:if test="${!statusPointMonitor.enabled}"><c:set var="clazz" value="error"/></c:if>
                <tags:nameValue2 nameKey=".statusPointMonitoring" valueClass="${clazz}">
                    <i:inline key="${statusPointMonitor.evaluatorStatus}"/>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        
        <tags:sectionContainer2 id="actionsBox" nameKey="stateActionsTable">
            <table id="processors-table" class="compact-results-table dashed">
                <thead>
                    <tr>
                        <th><i:inline key=".stateActionsTable.prevState"/></th>
                        <th><i:inline key=".stateActionsTable.nextState"/></th>
                        <th><i:inline key=".stateActionsTable.action"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="row" items="${statusPointMonitor.processors}" varStatus="status">
                        <tr data-row="${status.index}">
                            <td>
                                <form:select path="processors[${status.index}].prevState" cssClass="f-row-prev-state">
                                    <c:forEach items="${statusPointMonitor.stateGroup.statesList}" var="state">
                                        <form:option value="${state.liteID}">${fn:escapeXml(state.stateText)}</form:option>
                                    </c:forEach>
                                    <form:option value="${dontCare}"><cti:msg2 key=".state.dontCare"/></form:option>
                                    <form:option value="${difference}"><cti:msg2 key=".state.difference"/></form:option>
                                </form:select>
                            </td>
                            <td>
                                <form:select path="processors[${status.index}].nextState" cssClass="f-row-next-state">
                                    <c:forEach items="${statusPointMonitor.stateGroup.statesList}" var="state">
                                        <form:option value="${state.liteID}">${fn:escapeXml(state.stateText)}</form:option>
                                    </c:forEach>
                                    <form:option value="${dontCare}"><cti:msg2 key=".state.dontCare"/></form:option>
                                    <form:option value="${difference}"><cti:msg2 key=".state.difference"/></form:option>
                                </form:select>
                            </td>
                            <td>
                                <form:select path="processors[${status.index}].actionType" cssClass="f-row-action-type">
                                    <c:forEach items="${eventTypes}" var="eventType">
                                        <form:option value="${eventType}">${eventType}</form:option>
                                    </c:forEach>
                                </form:select>
                                <cti:button nameKey="delete" renderMode="buttonImage" icon="icon-cross" classes="f-remove fr"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div class="action-area">
                <cti:button nameKey="addAction" icon="icon-add" classes="f-add"/>
            </div>
        </tags:sectionContainer2>
        
        <%-- create / update / delete --%>
        <div class="page-action-area">
            <cti:button nameKey="update" type="submit" busy="true" classes="primary action" data-disable-group="actionButtons"/>
            <c:set var="enableDisableKey" value="disable"/>
            <c:if test="${statusPointMonitor.evaluatorStatus eq 'DISABLED'}">
                <c:set var="enableDisableKey" value="enable"/>
            </c:if>
            <cti:button nameKey="${enableDisableKey}" busy="true" data-disable-group="actionButtons"
                onclick="jQuery('#toggleEnabledForm').submit()"/>
            <cti:button id="deleteButton" nameKey="delete" busy="true" onclick="jQuery('#deleteStatusPointMonitor').submit();" data-disable-group="actionButtons" classes="delete"/>
            <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${statusPointMonitor.statusPointMonitorName}"/>
            <cti:url var="backUrl" value="/amr/statusPointMonitoring/viewPage">
                <cti:param name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}" />
            </cti:url>
            <cti:button nameKey="cancel" href="${backUrl }" busy="true" data-disable-group="actionButtons"/>
        </div>
    </form:form>
    
</cti:standardPage>