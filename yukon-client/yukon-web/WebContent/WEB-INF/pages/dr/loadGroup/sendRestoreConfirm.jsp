<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:standardPage module="internal">
    <p>
        <cti:msg key="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.confirmQuestion"
            argument="${loadGroup.name}"/>
    </p>
    <form id="sendRestoreForm" action="/spring/dr/loadGroup/sendRestore">
        <input type="hidden" name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.okButton"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.loadGroup.sendRestoreConfirm.cancelButton"/>"
                onclick="parent.$('drDialog').hide()"/>
        </div>
    </form>
</cti:standardPage>
