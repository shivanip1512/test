<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <h1 class="dialogQuestion">
        <c:choose>
            <c:when test="${isEnabled}">
                <cti:msg key="yukon.web.modules.dr.loadGroup.sendEnableConfirm.confirmQuestion"
                	htmlEscape="true" argument="${loadGroup.name}"/>
            </c:when>
            <c:otherwise>
                <cti:msg key="yukon.web.modules.dr.loadGroup.sendDisableConfirm.confirmQuestion"
                	htmlEscape="true" argument="${loadGroup.name}"/>
            </c:otherwise>
        </c:choose>
    </h1>

    <cti:url var="submitUrl" value="/dr/loadGroup/setEnabled"/>
    <form id="sendEnableForm" action="${submitUrl}"
        onsubmit="return submitFormViaAjax('drDialog', 'sendEnableForm');">
        <input type="hidden" name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
        <input type="hidden" name="isEnabled" value="${isEnabled}"/>
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.loadGroup.sendEnableConfirm.okButton"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.loadGroup.sendEnableConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
