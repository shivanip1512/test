<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msgScope paths="modules.dr.program.sendEnableConfirm">
    <h4 class="dialogQuestion stacked">
        <c:choose>
            <c:when test="${isEnabled}">
                <cti:msg2 key=".confirmQuestion" htmlEscape="true" argument="${program.name}"/>
            </c:when>
            <c:otherwise>
                <cti:msg2 key="yukon.web.modules.dr.program.sendDisableConfirm.confirmQuestion" htmlEscape="true" argument="${program.name}"/>
            </c:otherwise>
        </c:choose>
    </h4>

    <form id="sendEnableForm" action="<cti:url value="/dr/program/setEnabled"/>">
        <input type="hidden" name="programId" value="${program.paoIdentifier.paoId}"/>
        <input type="hidden" name="isEnabled" value="${isEnabled}"/>
        <div class="action-area">
            <cti:button nameKey="ok" classes="primary action" onclick="submitFormViaAjax('drDialog', 'sendEnableForm')"/>
            <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
        </div>
    </form>
</cti:msgScope>