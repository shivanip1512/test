<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="container" required="true" type="java.lang.String"%>
<%@ attribute name="nameKey" required="true" type="java.lang.String"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ tag  dynamic-attributes="linkParameters" %>

<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>
<cti:uniqueIdentifier var="dialogId" prefix="${title}_" />

<tags:simpleDialog id="${dialogId}"/>

<script type="text/javascript">
	${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});
</script>

    <cti:msg2 var="labelText" key=".${nameKey}.label"/>
    <cti:msg2 var="labelBusyText" key=".${nameKey}.labelBusy"/>

<a href="javascript:void(0)"
   class="popupLink"
   onclick="${widgetParameters.jsWidget}.doActionPopup('${method}', '${thisId}', '${labelText}', '${dialogId}')">
    <jsp:doBody/>
</a>
