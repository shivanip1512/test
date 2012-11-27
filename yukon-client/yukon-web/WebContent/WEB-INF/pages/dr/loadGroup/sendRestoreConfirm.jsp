<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <h1 class="dialogQuestion">
    	<cti:msg key="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.confirmQuestion"
    		htmlEscape="true" argument="${loadGroup.name}"/>
    </h1>

    <cti:url var="submitUrl" value="/dr/loadGroup/sendRestore"/>
    <form id="sendRestoreForm" action="${submitUrl}"
        onsubmit="return submitFormViaAjax('drDialog', 'sendRestoreForm');">
        <input type="hidden" name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.okButton"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
