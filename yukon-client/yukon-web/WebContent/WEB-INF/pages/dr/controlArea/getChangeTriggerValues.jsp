<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.dr.controlArea.getChangeTriggerValues">
    <h1 class="dialogQuestion">
        <i:inline key=".instructions" arguments="${controlArea.name}" />
    </h1>

    <cti:url var="submitUrl" value="/spring/dr/controlArea/triggerChange"/>
    <form id="getChangeTimeWindowValues" action="${submitUrl}"
        onsubmit="submitFormViaAjax('drDialog', 'getChangeTimeWindowValues');return false;">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        
        <cti:msg2 var="thresholdName" key=".threshold"/>
        <cti:msg2 var="offsetName" key=".offset"/>
        <c:forEach var="trigger" items="${triggers}">
            <div class="triggerSettingsBox">
                <h2><i:inline key=".trigger" arguments="${trigger.triggerNumber}"/></h2>
                <c:if test="${trigger.triggerType == 'STATUS'}">
                    <i:inline key=".statusTrigger"/>
                </c:if>
                <c:if test="${trigger.triggerType == 'THRESHOLD'}">
                    <tags:nameValueContainer>
                        <tags:nameValue name="${thresholdName}">
                            <input type="text" name="threshold${trigger.triggerNumber}" value="${trigger.threshold}"/>
                        </tags:nameValue>
                        <tags:nameValue name="${offsetName}">
                            <input type="text" name="offset${trigger.triggerNumber}" value="${trigger.minRestoreOffset}"/>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </c:if>
            </div>
        </c:forEach>

        <div class="actionArea">
            <input type="submit" value="<cti:msg2 key=".okButton"/>"/>
            <input type="button" value="<cti:msg2 key=".cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
</cti:msgScope>
