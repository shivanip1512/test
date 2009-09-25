<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <p>
        <cti:msg key="yukon.web.modules.dr.controlArea.sendChangeTimeWindowConfirm.confirmQuestion"
            arguments="${startTime},${stopTime},${controlArea.name}" />
    </p>
    <form id="sendChangeTimeWindowForm" action="/spring/dr/controlArea/changeTimeWindow">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
        <input type="hidden" name="startTime" value="${startTime}"/>
        <input type="hidden" name="stopTime" value="${stopTime}"/>
        <div class="actionArea">
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendChangeTimeWindowConfirm.okButton"/>"
                onclick="submitFormViaAjax('drDialog', 'sendChangeTimeWindowForm')"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.sendChangeTimeWindowConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
