<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:standardPage module="internal">
    <p>
        <cti:msg key="yukon.web.modules.dr.loadGroup.sendShedConfirm.confirmQuestion"
            argument="${loadGroup.name}"/>
    </p>
    <form id="sendShedForm" action="/spring/dr/loadGroup/sendShed">
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
</cti:standardPage>
