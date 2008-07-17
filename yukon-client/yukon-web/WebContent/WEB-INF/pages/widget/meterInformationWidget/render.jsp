<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<tags:nameValueContainer altRowOn="true">
	<tags:nameValue name="Device Name">${meter.name}</tags:nameValue>
	<tags:nameValue name="Meter Number">${meter.meterNumber}</tags:nameValue>
	<tags:nameValue name="Type">${deviceType}</tags:nameValue>
	<tags:nameValue name="Physical Address">
	<c:if test='${meter.address != null}'>${meter.address}</c:if>
    <c:if test='${meter.address == null}'>n/a</c:if>
    </tags:nameValue>
	<tags:nameValue name="Route">
	<c:if test='${meter.route != null}'>${meter.route}</c:if>
    <c:if test='${meter.route == null}'>n/a</c:if>
    </tags:nameValue>
	<tags:nameValue name="Status">
	<c:if test='${meter.disabled}'><span style="font-weight:bold;color:#CC0000;">Disabled</span></c:if>
    <c:if test='${!meter.disabled}'><span style="font-weight:bold;color:#006633;">Enabled</span></c:if>
    </tags:nameValue>
</tags:nameValueContainer>

<br>
<div id="${widgetParameters.widgetId}_results"></div>
<div style="text-align: right">
	<tags:widgetActionUpdate method="ping" label="Ping" labelBusy="Pinging" container="${widgetParameters.widgetId}_results" />
</div>