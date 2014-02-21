<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<p><cti:msg key="${userMessage}"/></p>

<div class="action-area">
    <cti:button nameKey="ok" onclick="jQuery('#${popupId}').dialog('close');"/>
</div>
