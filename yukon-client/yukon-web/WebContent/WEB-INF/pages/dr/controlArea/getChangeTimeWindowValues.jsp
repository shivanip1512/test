<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <h1 class="dialogQuestion">
        <cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.instructions"
            argument="${controlArea.name}" />
    </h1>

    <c:if test="${!empty validWindow && !validWindow}">
        <div class="errorMessage">
            <cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.invalidWindow"/>
        </div>
        <br>
    </c:if>

    <cti:url var="submitUrl" value="/spring/dr/controlArea/sendChangeTimeWindowConfirm"/>
    <form id="getChangeTimeWindowValues" action="${submitUrl}"
        onsubmit="return submitFormViaAjax('drDialog', 'getChangeTimeWindowValues');">
        <input type="hidden" name="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>

        <cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.startTime"/>
        <input type="text" name="startTime" value="${startTime}"/><br>
        <cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.stopTime"/>
        <input type="text" name="stopTime" value="${stopTime}"/>
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.okButton"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.controlArea.getChangeTimeWindowValues.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
