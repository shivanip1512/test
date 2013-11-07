<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<p><cti:msg key="${userMessage}"/></p>

<div class="action-area">
    <input type="button" value="<cti:msg key="yukon.common.okButton"/>"
        onclick="jQuery('#${popupId}').dialog('close');"/>
</div>
