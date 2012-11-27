<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <h1 class="dialogQuestion">
        <c:choose>
            <c:when test="${isEnabled}">
                <cti:msg key="yukon.web.modules.dr.program.sendEnableConfirm.confirmQuestion"
                	htmlEscape="true" argument="${program.name}"/>
            </c:when>
            <c:otherwise>
                <cti:msg key="yukon.web.modules.dr.program.sendDisableConfirm.confirmQuestion"
                	htmlEscape="true" argument="${program.name}"/>
            </c:otherwise>
        </c:choose>
    </h1>

    <form id="sendEnableForm" action="/dr/program/setEnabled">
        <input type="hidden" name="programId" value="${program.paoIdentifier.paoId}"/>
        <input type="hidden" name="isEnabled" value="${isEnabled}"/>
        <div class="actionArea">
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.sendEnableConfirm.okButton"/>"
                onclick="submitFormViaAjax('drDialog', 'sendEnableForm')"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.sendEnableConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
