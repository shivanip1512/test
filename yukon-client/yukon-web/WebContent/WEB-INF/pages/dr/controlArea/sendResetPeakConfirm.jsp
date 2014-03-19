<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.dr.controlArea.sendResetPeakConfirm">
    <p class="dialogQuestion">
        <cti:msg2 key=".confirmQuestion" htmlEscape="true" argument="${controlArea.name}" />
    </p>

    <form id="sendResetPeakForm" action="/dr/controlArea/resetPeak" onsubmit="return submitFormViaAjax('drDialog', 'sendResetPeakForm');">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        <div class="action-area">
            <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
            <cti:button nameKey="ok" classes="primary action" type="submit"/>
        </div>
    </form>
</cti:msgScope>