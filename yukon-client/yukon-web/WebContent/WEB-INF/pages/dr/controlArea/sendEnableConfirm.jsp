<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <p>
        <c:choose>
            <c:when test="${isEnabled}">
                <cti:msg key="yukon.web.modules.dr.controlArea.sendEnableConfirm.confirmQuestion"
                    argument="${controlArea.name}"/>
            </c:when>
            <c:otherwise>
                <cti:msg key="yukon.web.modules.dr.controlArea.sendDisableConfirm.confirmQuestion"
                    argument="${controlArea.name}"/>
            </c:otherwise>
        </c:choose>
    </p>

    <cti:url var="submitUrl" value="/spring/dr/controlArea/setEnabled"/>
    <form id="sendEnableForm" action="${submitUrl}"
        onsubmit="return submitFormViaAjax('drDialog', 'sendEnableForm');">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        <input type="hidden" name="isEnabled" value="${isEnabled}"/>
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendEnableConfirm.okButton"/>"
                onclick="submitFormViaAjax('drDialog', 'sendEnableForm')"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendEnableConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
