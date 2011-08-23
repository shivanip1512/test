<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

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
        <cti:img nameKey="deleteAction" href="#"/>
    </span>
    
    <i:simplePopup styleClass="mediumSimplePopup" titleKey=".delete.title" id="deleteConfirmDialog">
        <h1 class="dialogQuestion">
            <i:inline key=".deleteConfirm" arguments="${statusPointMonitor.statusPointMonitorName}"/>
        </h1>

        <div class="actionArea">
            <tags:slowInput2 formId="monitorDeleteForm" key="ok"/>
            <input type="button" value="<i:inline key=".cancel"/>" onclick="$('deleteConfirmDialog').hide()" class="formSubmit">
        </div>
    </i:simplePopup>
    
    <c:set var="statusPointMonitorId" value="${statusPointMonitor.statusPointMonitorId}"/>
    
	<%-- MISC FORMS --%>
    <form id="monitorDeleteForm" action="/spring/amr/statusPointMonitoring/delete" method="post">
        <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitorId}">
    </form>
    
	<form id="toggleEnabledForm" action="/spring/amr/statusPointMonitoring/toggleEnabled" method="post">
		<input type="hidden" name="statusPointMonitorId" value="${statusPointMonitorId}">
	</form>
	
	<%-- UPDATE FORM --%>
	<form:form commandName="statusPointMonitor" id="basicInfoForm" action="/spring/amr/statusPointMonitoring/update" method="post">
	
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
	            		<cti:msg2 key=".popupInfo.deviceGroup" htmlEscape="false"/>
					</tags:helpInfoPopup>
				</tags:nameValue2>
			    
                <%-- attribute --%>
                <tags:nameValue2 nameKey="yukon.common.device.commander.attributeSelector.selectAttribute">
                    <form:select path="attribute">
                        <c:forEach items="${allAttributes}" var="attributeVar">
                            <form:option value="${attributeVar}">${attributeVar.description}</form:option>
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
            <div class="dialogScrollArea">
                <table id="actionsTable" class="compactResultsTable">
                    <tr>
                        <th><i:inline key=".stateActionsTable.prevState"/></th>
                        <th><i:inline key=".stateActionsTable.nextState"/></th>
                        <th><i:inline key=".stateActionsTable.action"/></th>
                        <th><i:inline key=".stateActionsTable.delete"/></th>
                    </tr>
                </table>
            </div>
            <br>
            <span style="float:right;">
                <cti:button nameKey="addAction" onclick="addAction()"/>
            </span>
        </tags:boxContainer2>
		
		<%-- create / update / delete --%>
		<div class="pageActionArea">
            <tags:slowInput2 formId="basicInfoForm" key="update"/>
    			<c:choose>
    				<c:when test="${statusPointMonitor.evaluatorStatus eq 'ENABLED'}">
    					<tags:slowInput2 formId="toggleEnabledForm" key="statusPointMonitoringDisable"/>
    				</c:when>
    				<c:when test="${statusPointMonitor.evaluatorStatus eq 'DISABLED'}">
    					<tags:slowInput2 formId="toggleEnabledForm" key="statusPointMonitoringEnable"/>
    				</c:when>
    			</c:choose>
            <input type="button" onclick="$('deleteConfirmDialog').show()" value="<i:inline key=".delete"/>" class="formSubmit">
			<input type="submit" name="cancel" class="formSubmit" value="<i:inline key=".cancel"/>">
        </div>
	</form:form>
    
    <script type="text/javascript">
        var processors = ${cti:jsonString2(statusPointMonitor.processors)};
        initWithProcessors(processors);
    </script>
        
</cti:standardPage>
