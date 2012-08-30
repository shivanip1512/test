<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msg2 key="yukon.web.components.updatedWarning.title" var="warningTitle"/>
<tags:simplePopup title="${warningTitle}" id="updatedWarning">
    <div class="bottomPadded">
        <i:inline key="yukon.web.components.updatedWarning.text"/>
    </div>
    <div style="text-align:right;">
        <button onclick="javascript:window.location.reload();">
            <i:inline key="yukon.web.components.updatedWarning.reloadButton.label"/>
        </button>
        <button onclick="jQuery('#updatedWarning').hide();">
            <i:inline key="yukon.web.components.updatedWarning.cancelButton.label"/>
        </button>
    </div>
</tags:simplePopup>