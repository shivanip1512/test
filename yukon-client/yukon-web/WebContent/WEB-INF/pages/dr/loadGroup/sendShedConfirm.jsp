<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

    <h1 class="dialogQuestion">
    	<cti:msg key="yukon.web.modules.dr.loadGroup.sendShedConfirm.confirmQuestion"
    		htmlEscape="true" argument="${loadGroup.name}"/>
    </h1>

    <cti:url var="submitUrl" value="/dr/loadGroup/sendShed"/>
    <form id="sendShedForm" action="${submitUrl}"
        onsubmit="return submitFormViaAjax('drDialog', 'sendShedForm');">
        <input type="hidden" name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
        <p><cti:msg key="yukon.web.modules.dr.loadGroup.sendShedConfirm.chooseShedTime"/> <select name="durationInSeconds">
            <c:forEach var="shedTimeOption" items="${shedTimeOptions}">
                <option value="${shedTimeOption.key}">${shedTimeOption.value}</option>
            </c:forEach>
        </select>
        </p>
        <p><cti:msg key="yukon.web.modules.dr.loadGroup.sendShedConfirm.shedTimeNotes"/></p>
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.loadGroup.sendShedConfirm.okButton"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.loadGroup.sendShedConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
