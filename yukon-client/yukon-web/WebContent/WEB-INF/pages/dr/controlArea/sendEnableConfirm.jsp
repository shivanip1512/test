<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="yukon.web.modules.dr.controlArea">
    <p class="dialogQuestion">
        <c:choose>
            <c:when test="${isEnabled}">
                <cti:msg2 key=".sendEnableConfirm.confirmQuestion" htmlEscape="true" argument="${controlArea.name}"/>
            </c:when>
            <c:otherwise>
                <cti:msg2 key=".sendDisableConfirm.confirmQuestion" htmlEscape="true" argument="${controlArea.name}"/>
            </c:otherwise>
        </c:choose>
    </p>

    <cti:url var="submitUrl" value="/dr/controlArea/setEnabled"/>
    <form id="sendEnableForm" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'sendEnableForm');">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        <input type="hidden" name="isEnabled" value="${isEnabled}"/>
        <div class="actionArea">
            <cti:button nameKey="cancel" onclick="jQuery('#drDialog').dialog('close');"/>
            <cti:button nameKey="ok" classes="primary action" type="submit"/>
        </div>
    </form>
</cti:msgScope>