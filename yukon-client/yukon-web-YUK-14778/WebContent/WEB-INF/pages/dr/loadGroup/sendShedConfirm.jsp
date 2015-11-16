<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.dr.loadGroup.sendShedConfirm">
    <h3 class="dialogQuestion stacked"><i:inline key=".confirmQuestion" arguments="${fn:escapeXml(loadGroup.name)}"/></h3>

    <cti:url var="submitUrl" value="/dr/loadGroup/sendShed"/>
    <form id="sendShedForm" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'sendShedForm');">
        <input type="hidden" name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
        <div>
            <i:inline key=".chooseShedTime"/> 
            <select name="durationInSeconds">
                <c:forEach var="shedTimeOption" items="${shedTimeOptions}">
                    <option value="${shedTimeOption.key}">${shedTimeOption.value}</option>
                </c:forEach>
            </select>
        </div>
        <p><i:inline key=".shedTimeNotes"/></p>
        <div class="action-area">
            <cti:button nameKey="ok" type="submit" classes="primary action"/>
            <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
        </div>
    </form>
</cti:msgScope>