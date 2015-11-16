<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msg2 key="yukon.web.components.updatedWarning.title" var="warningTitle"/>
<tags:simplePopup title="${warningTitle}" id="updatedWarning">
    <div class="stacked">
        <i:inline key="yukon.web.components.updatedWarning.text"/>
    </div>
    <div class="action-area">
        <cti:button nameKey="refresh" classes="primary action" onclick="window.location.reload();"/>
        <cti:button nameKey="ignore" onclick="$('#updatedWarning').dialog('close');"/>
    </div>
</tags:simplePopup>