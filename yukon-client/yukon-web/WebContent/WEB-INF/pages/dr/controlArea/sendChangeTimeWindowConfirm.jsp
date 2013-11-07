<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:msgScope paths="modules.dr.controlArea.sendChangeTimeWindowConfirm">
    <p class="dialogQuestion stacked">
        <cti:list var="msgArgs">
            <cti:item value="${controlAreaTimeWindowDto.startTime}"/>
            <cti:item value="${controlAreaTimeWindowDto.stopTime}"/>
            <cti:item value="${controlArea.name}"/>
        </cti:list>
        <cti:msg2 key=".confirmQuestion" arguments="${msgArgs}"/>
    </p>

	<cti:url var="submitUrl" value="/dr/controlArea/changeTimeWindow"/>
	<form id="sendChangeTimeWindowForm" action="${submitUrl}"
	    onsubmit="return submitFormViaAjax('drDialog', 'sendChangeTimeWindowForm');">
	    <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
	    <input type="hidden" name="startTime" value="${controlAreaTimeWindowDto.startTime}"/>
	    <input type="hidden" name="stopTime" value="${controlAreaTimeWindowDto.stopTime}"/>
	    <div class="action-area">
	        <cti:button nameKey="cancel" onclick="jQuery('#drDialog').dialog('close');"/>
	        <cti:button nameKey="ok" classes="primary action" type="submit"/>
	    </div>
	</form>
</cti:msgScope>