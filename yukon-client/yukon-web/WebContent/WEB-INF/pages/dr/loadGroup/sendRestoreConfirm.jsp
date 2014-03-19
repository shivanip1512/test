<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.dr.loadGroup.sendRestoreConfirm">
    <h3 class="dialogQuestion stacked"><i:inline key=".confirmQuestion" arguments="${fn:escapeXml(loadGroup.name)}"/></h3>
    
    <cti:url var="submitUrl" value="/dr/loadGroup/sendRestore"/>
    <form id="sendRestoreForm" action="${submitUrl}"
        onsubmit="return submitFormViaAjax('drDialog', 'sendRestoreForm');">
        <input type="hidden" name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
        <div class="action-area">
            <cti:button nameKey="ok" type="submit" classes="primary action"/>
            <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
        </div>
    </form>
</cti:msgScope>