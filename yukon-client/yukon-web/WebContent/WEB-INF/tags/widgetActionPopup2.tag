<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="container" required="true" type="java.lang.String"%>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>
<cti:uniqueIdentifier var="dialogId" prefix="${title}_" />

<ct:simpleDialog id="${dialogId}"/>

<script type="text/javascript">
	${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});
</script>

    <cti:msg2 var="labelText" key="${label}"/>
    <cti:msg2 var="labelBusyText" key="${labelBusy}"/>

<a href="javascript:void(0)"
   class="popupLink"
   onclick="${widgetParameters.jsWidget}.doActionPopup('${method}', '${thisId}', '${labelText}', '${dialogId}')">
    <jsp:doBody/>
</a>
