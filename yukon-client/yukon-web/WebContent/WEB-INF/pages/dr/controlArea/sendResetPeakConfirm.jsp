<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <h1 class="dialogQuestion">
        	<cti:msg key="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.confirmQuestion"
        		htmlEscape="true" argument="${controlArea.name}" />
    </h1>

    <cti:url var="submitUrl" value="/dr/controlArea/resetPeak"/>
    <form id="sendResetPeakForm" action="${submitUrl}"
        onsubmit="return submitFormViaAjax('drDialog', 'sendResetPeakForm');">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.okButton"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
