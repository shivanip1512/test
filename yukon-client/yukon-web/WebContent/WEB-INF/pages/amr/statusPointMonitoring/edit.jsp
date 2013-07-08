<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="amr" page="statusPointMonitorEditor">

    <cti:includeScript link="/JavaScript/statusPointMonitorActions.js"/>
    
    <cti:msg2 var="deviceGroupTitle" key=".popupInfo.deviceGroup.title"/>
    
    <span id="templateHtml" style="display: none">
        <select name="states">
            <c:forEach items="${statusPointMonitor.stateGroup.statesList}" var="state">
                <option value="${state.liteID}">${state.stateText}</option>
            </c:forEach>
            <option value="${dontCare}"><i:inline key=".state.dontCare"/></option>
            <option value="${difference}"><i:inline key=".state.difference"/></option>
        </select>
        <select name="eventTypes">
            <c:forEach items="${eventTypes}" var="eventType">
                <option value="${eventType}"/>${eventType}</option>
            </c:forEach>
        </select>
        <cti:icon nameKey="deleteAction" icon="icon-cross" href="#"/>
    </span>
    
    <cti:url var="submitUrl" value="/amr/statusPointMonitoring/delete"/>
    <form id="deleteStatusPointMonitor" action="${submitUrl}" method="post">
        <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}">
    </form>

	<%-- MISC FORMS --%>
	<form id="toggleEnabledForm" action="/amr/statusPointMonitoring/toggleEnabled" method="post">
		<input type="hidden" name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}">
	</form>
	
	<%-- UPDATE FORM --%>
    <cti:url var="submitUrl" value="/amr/statusPointMonitoring/update"/>
	<form:form commandName="statusPointMonitor" action="${submitUrl}" method="post">
	
		<form:hidden path="statusPointMonitorId"/>
        <form:hidden path="evaluatorStatus"/>
        <form:hidden path="stateGroup"/>
		
		<tags:formElementContainer nameKey="sectionHeader">
			<tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">
			
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
				<tags:nameValue2 nameKey=".statusPointMonitoring">
                    <i:inline key="${statusPointMonitor.evaluatorStatus}"/>
				</tags:nameValue2>
                
			</tags:nameValueContainer2>
		</tags:formElementContainer>
        
        <tags:boxContainer2 id="actionsBox" nameKey="stateActionsTable" styleClass="mediumContainer">
            <div class="dialogScrollArea stacked">
                <table id="actionsTable" class="compactResultsTable">
                    <thead>
                    <tr>
                        <th><i:inline key=".stateActionsTable.prevState"/></th>
                        <th><i:inline key=".stateActionsTable.nextState"/></th>
                        <th><i:inline key=".stateActionsTable.action"/></th>
                        <th><i:inline key=".stateActionsTable.delete"/></th>
                    </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody></tbody>
                </table>
            </div>
            <br>
            <span style="float:right;">
                <cti:button nameKey="addAction" onclick="addAction()" icon="icon-add"/>
            </span>
        </tags:boxContainer2>
		
		<%-- create / update / delete --%>
		<div class="pageActionArea">
            <cti:button nameKey="update" type="submit" busy="true" classes="primary action f-disableAfterClick" data-disable-group="actionButtons"/>
            <c:set var="enableDisableKey" value="disable"/>
            <c:if test="${statusPointMonitor.evaluatorStatus eq 'DISABLED'}">
                <c:set var="enableDisableKey" value="enable"/>
            </c:if>
            <cti:button nameKey="${enableDisableKey}" busy="true" classes="f-disableAfterClick" data-disable-group="actionButtons"
                onclick="jQuery('#toggleEnabledForm').submit()"/>
            <cti:button id="deleteButton" nameKey="delete" busy="true" onclick="jQuery('#deleteStatusPointMonitor').submit();" data-disable-group="actionButtons"/>
            <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${statusPointMonitor.statusPointMonitorName}"/>
            <cti:url var="backUrl" value="/amr/statusPointMonitoring/viewPage">
                <cti:param name="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}" />
            </cti:url>
            <cti:button nameKey="cancel" type="button" href="${backUrl }"  busy="true" classes="f-disableAfterClick" data-disable-group="actionButtons"/>
        </div>
	</form:form>
    
    <script type="text/javascript">
        var processors = ${cti:jsonString2(statusPointMonitor.processors)};
        initWithProcessors(processors);
    </script>
        
</cti:standardPage>
