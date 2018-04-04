<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="monitorName" required="true" type="java.lang.String" description="Name of the monitor" %>
<%@ attribute name="target" required="true" type="java.lang.String"
	description="CSS selector of the HTML element that is the target of the data-event"%>

<cti:msg2 var="dialogTitle" key="yukon.web.components.ajaxConfirm.confirmDelete.title"/>
<cti:msg2 var="dialogMessage" key="yukon.web.components.ajaxConfirm.confirmDelete.message" argument="${monitorName}" 
          htmlEscapeArguments="true"/>
<cti:msg2 var="okBtnText" key="yukon.web.components.ajaxConfirm.confirmDelete.ok"/>

<div class="dn" id="confirm-delete-monitor-popup" data-dialog data-target="${target}" 
     data-title="${dialogTitle}" 
     data-width="400" data-event="yukon:delete:monitor"
     data-ok-text="${okBtnText}">
    ${dialogMessage}
</div>