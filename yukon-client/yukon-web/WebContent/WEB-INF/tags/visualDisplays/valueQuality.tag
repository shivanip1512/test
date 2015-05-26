<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="pointId" required="true" type="java.lang.Integer"%>
    
<td class="tar">
    <cti:pointValue format="VALUE" pointId="${pointId}"/>
    <cti:pointValue format="UNIT" pointId="${pointId}"/>
</td>
<td class="tar">
    <cti:pointValue format="{quality|com.cannontech.multispeak.service.impl.MultispeakLMServiceImpl.getPointQualityLetter}" 
        pointId="${pointId}"/>
</td>

<script type="text/javascript">
    yukon.dataUpdater.registerCallback(yukon.dr.psd.transmitted, { 'value': 'POINT/${pointId}/VALUE' });
</script>