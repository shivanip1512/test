<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="modules.dr.controlArea.getChangeTriggerValues">

	<cti:flashScopeMessages/>

    <p class="dialogQuestion">
        <i:inline key=".instructions" arguments="${controlArea.name}" />
    </p>

    <cti:url var="submitUrl" value="/dr/controlArea/triggerChange"/>
        
    <form:form id="getChangeTimeWindowValues" modelAttribute="triggersDto" action="${submitUrl}" onsubmit="submitFormViaAjax('drDialog', 'getChangeTimeWindowValues');return false;">
        <cti:csrfToken/>
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        
        <cti:msg2 var="thresholdName" key=".threshold"/>
        <cti:msg2 var="offsetName" key=".offset"/>
        
        <c:forEach var="trigger" items="${triggersDto.triggers}">
        
        	<c:if test="${not empty trigger.triggerNumber}">
        
        	<form:hidden path="trigger${trigger.triggerNumber}.triggerNumber"/>
        	<form:hidden path="trigger${trigger.triggerNumber}.triggerType"/>
            <div class="triggerSettingsBox">
                <h2><i:inline key=".trigger" arguments="${trigger.triggerNumber}"/></h2>
                <c:if test="${trigger.triggerType == 'STATUS'}">
                    <i:inline key=".statusTrigger"/>
                </c:if>
                <c:if test="${trigger.triggerType == 'THRESHOLD' || trigger.triggerType == 'THRESHOLD_POINT'}">
                    <tags:nameValueContainer>
                        <tags:nameValue name="${thresholdName}">
                        	<tags:input path="trigger${trigger.triggerNumber}.threshold" disabled="${trigger.triggerType == 'THRESHOLD_POINT'}"/>
                        </tags:nameValue>
                        <tags:nameValue name="${offsetName}">
                        	<tags:input path="trigger${trigger.triggerNumber}.minRestoreOffset"/>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </c:if>
            </div>
            
            </c:if>
            
        </c:forEach>

        <div class="action-area">
            <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
            <cti:button nameKey="ok" classes="primary action" type="submit"/>
        </div>
    </form:form>
</cti:msgScope>
