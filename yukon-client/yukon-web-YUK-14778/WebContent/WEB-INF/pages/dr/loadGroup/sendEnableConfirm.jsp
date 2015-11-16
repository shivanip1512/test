<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.dr.loadGroup">
    <h3 class="dialogQuestion stacked">
        <c:choose>
            <c:when test="${isEnabled}">
                <i:inline key=".sendEnableConfirm.confirmQuestion" arguments="${fn:escapeXml(loadGroup.name)}"/>
            </c:when>
            <c:otherwise>
                <i:inline key=".sendDisableConfirm.confirmQuestion" arguments="${fn:escapeXml(loadGroup.name)}"/>
            </c:otherwise>
        </c:choose>
    </h3>
    
    <cti:url var="submitUrl" value="/dr/loadGroup/setEnabled"/>
    <form id="sendEnableForm" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'sendEnableForm');">
        <input type="hidden" name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
        <input type="hidden" name="isEnabled" value="${isEnabled}"/>
        <div class="action-area">
            <cti:button nameKey="ok" type="submit" classes="primary action"/>
            <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
        </div>
    </form>
</cti:msgScope>