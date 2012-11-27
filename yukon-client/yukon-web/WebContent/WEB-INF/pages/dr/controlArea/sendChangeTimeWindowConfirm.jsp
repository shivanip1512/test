<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <h1 class="dialogQuestion">
        <cti:msg key="yukon.web.modules.dr.controlArea.sendChangeTimeWindowConfirm.confirmQuestion"
			htmlEscape="true" arguments="${controlAreaTimeWindowDto.startTime},${controlAreaTimeWindowDto.stopTime},${controlArea.name}" />
    </h1>

    <cti:url var="submitUrl" value="/dr/controlArea/changeTimeWindow"/>
    <form id="sendChangeTimeWindowForm" action="${submitUrl}"
        onsubmit="return submitFormViaAjax('drDialog', 'sendChangeTimeWindowForm');">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        <input type="hidden" name="startTime" value="${controlAreaTimeWindowDto.startTime}"/>
        <input type="hidden" name="stopTime" value="${controlAreaTimeWindowDto.stopTime}"/>
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendChangeTimeWindowConfirm.okButton"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendChangeTimeWindowConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
