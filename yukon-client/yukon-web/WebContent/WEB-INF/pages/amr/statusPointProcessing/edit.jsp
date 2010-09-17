<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="amr" page="statusPointEditor">

    <cti:includeScript link="/JavaScript/statusPointMonitorActions.js"/>
    
    <cti:msg2 var="deviceGroupTitle" key=".popupInfo.deviceGroup.title"/>
    
    <span id="templateHtml" style="display: none">
        <select name="states">
            <c:forEach items="${statusPointMonitorDto.stateGroup.statesList}" var="state">
                <option value="${state.liteID}">${state.stateText}</option>
            </c:forEach>
            <option value="${dontCare}"><cti:msg2 key=".state.dontCare"/></option>
            <option value="${difference}"><cti:msg2 key=".state.difference"/></option>
        </select>
        <select name="eventTypes">
            <c:forEach items="${eventTypes}" var="eventType">
                <option value="${eventType}"/>${eventType}</option>
            </c:forEach>
        </select>
        <cti:img key="deleteAction" href="#"/>
    </span>

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:crumbLink><cti:msg2 key=".title" /></cti:crumbLink>
    </cti:breadCrumbs>
    
    <i:simplePopup styleClass="mediumSimplePopup" titleKey=".delete.title" id="deleteConfirmDialog">
        <h1 class="dialogQuestion">
            <cti:msg2 key=".deleteConfirm" arguments="${statusPointMonitorDto.statusPointMonitorName}"/>
        </h1>

        <div class="actionArea">
            <input type="button" value="<cti:msg2 key=".ok"/>"
                onclick="$('monitorDeleteForm').submit()"/>
            <input type="button" value="<cti:msg2 key=".cancel"/>"
                onclick="$('deleteConfirmDialog').hide()"/>
        </div>
    </i:simplePopup>
    
    <h2><cti:msg2 key=".title" /></h2>
    <br>
    
        <c:set var="statusPointMonitorId" value="${statusPointMonitorDto.statusPointMonitorId}"/>
        
    	<%-- MISC FORMS --%>
        <form id="monitorDeleteForm" action="/spring/amr/statusPointProcessing/delete" method="post">
            <input type="hidden" name="statusPointMonitorId" value="${statusPointMonitorId}">
        </form>
        
		<form id="toggleEnabledForm" action="/spring/amr/statusPointProcessing/toggleEnabled" method="post">
			<input type="hidden" name="statusPointMonitorId" value="${statusPointMonitorId}">
		</form>
		
		<form id="cancelForm" action="/spring/meter/start" method="get">
		</form>
		
		<%-- UPDATE FORM --%>
		<form:form commandName="statusPointMonitorDto" id="basicInfoForm" action="/spring/amr/statusPointProcessing/update" method="post">
		
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
    					<tags:deviceGroupNameSelector fieldName="deviceGroupName" 
    											  	  fieldValue="${statusPointMonitorDto.deviceGroupName}" 
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
                                <form:option value="${attributeVar.key}">${attributeVar.description}</form:option>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                    
                    <%-- State Group --%>
                    <tags:nameValue2 nameKey=".stateGroup">
                        ${statusPointMonitorDto.stateGroup}
                    </tags:nameValue2>
                    
    				<%-- enable/disable monitoring --%>
					<tags:nameValue2 nameKey=".statusPointMonitoring">
                        ${statusPointMonitorStatus}
					</tags:nameValue2>
                    
    			</tags:nameValueContainer2>
			</tags:formElementContainer>
            
            <tags:boxContainer2 id="actionsBox" nameKey="stateActionsTable" styleClass="pointMonitorContainer">
                <div class="dialogScrollArea">
                    <table id="actionsTable" class="compactResultsTable rowHighlighting">
                        <tr>
                            <th><i:inline key=".stateActionsTable.prevState"/></th>
                            <th><i:inline key=".stateActionsTable.nextState"/></th>
                            <th><i:inline key=".stateActionsTable.action"/></th>
                            <th><i:inline key=".stateActionsTable.delete"/></th>
                        </tr>
                    </table>
                </div>
                <cti:labeledImg key="addAction" href="javascript: addAction()"/>
            </tags:boxContainer2>
			
			<%-- create / update / delete --%>
			<br>
            <tags:slowInput2 formId="basicInfoForm" key="update"/>
    			<c:choose>
    				<c:when test="${statusPointMonitorDto.evaluatorStatus eq 'ENABLED'}">
    					<tags:slowInput2 formId="toggleEnabledForm" key="statusPointMonitoringDisable"/>
    				</c:when>
    				<c:when test="${statusPointMonitorDto.evaluatorStatus eq 'DISABLED'}">
    					<tags:slowInput2 formId="toggleEnabledForm" key="statusPointMonitoringEnable"/>
    				</c:when>
    			</c:choose>
            <input type="button" onclick="$('deleteConfirmDialog').show()" value="<cti:msg2 key=".delete"/>" />
			<tags:slowInput2 formId="cancelForm" key="cancel"/>
		</form:form>
        
        <script type="text/javascript">
            var statusPointMonitorMessageProcessors = ${cti:jsonString2(statusPointMonitorDto.messageProcessors)};
            initWithMessageProcessors(statusPointMonitorMessageProcessors);
        </script>
        
</cti:standardPage>
