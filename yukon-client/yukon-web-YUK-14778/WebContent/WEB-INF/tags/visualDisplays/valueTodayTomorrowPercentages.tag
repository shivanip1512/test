<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="todayPointId" required="true" type="java.lang.Integer" %>
<%@ attribute name="tomorrowPointId" required="true" type="java.lang.Integer" %>

<td class="tar peak-load-today">
    <cti:pointValue format="VALUE" pointId="${todayPointId}"/>%
</td>
<td class="tar peak-load-tomorrow">
    <cti:pointValue format="VALUE" pointId="${tomorrowPointId}"/>%
</td>

<script type="text/javascript">
    yukon.dataUpdater.registerCallback(yukon.dr.psd.transmitted, { 'value': 'POINT/${todayPointId}/VALUE' });
    yukon.dataUpdater.registerCallback(yukon.dr.psd.transmitted, { 'value': 'POINT/${tomorrowPointId}/VALUE' });
</script>