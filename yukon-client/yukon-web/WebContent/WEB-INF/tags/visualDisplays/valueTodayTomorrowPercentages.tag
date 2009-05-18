<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="todayPointId" required="true" type="java.lang.Integer"%>
<%@ attribute name="tomorrowPointId" required="true" type="java.lang.Integer"%>
    
<td align="right" style="padding-right:10px;"><cti:pointValue format="VALUE" pointId="${todayPointId}"/>%</td>
<td align="right" style="padding-right:10px;"><cti:pointValue format="VALUE" pointId="${tomorrowPointId}"/>%</td>

<script type="text/javascript">
	cannonDataUpdateRegistration(setLastTransmission(),{'value':'POINT/${todayPointId}/VALUE'});
	cannonDataUpdateRegistration(setLastTransmission(),{'value':'POINT/${tomorrowPointId}/VALUE'});
</script>
