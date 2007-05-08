<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>

<span id="${thisId}">
<input type="button" value="${label}" onclick="${widgetParameters.jsWidget}.doActionRefresh('${method}', '${thisId}', '${labelBusy}...')">
<span class="widgetAction_waiting" style="display:none">
<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" >
</span>
</span>