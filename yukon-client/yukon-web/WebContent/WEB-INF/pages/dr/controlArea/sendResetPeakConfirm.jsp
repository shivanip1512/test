<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <p>
        <cti:msg key="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.confirmQuestion"
            argument="${controlArea.name}" />
    </p>
    <form id="sendResetPeakForm" action="/spring/dr/controlArea/resetPeak">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        <div class="actionArea">
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.okButton"/>"
                onclick="submitFormViaAjax('drDialog', 'sendResetPeakForm')"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendResetPeakConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
