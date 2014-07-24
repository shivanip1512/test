<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="pointId" required="true" type="java.lang.Integer"%>
    
<td align="right" style="padding-right:10px;"><cti:pointValue format="VALUE" pointId="${pointId}"/></td>
<td align="center"><cti:pointValue format="{quality|com.cannontech.multispeak.service.impl.MultispeakLMServiceImpl.getPointQualityLetter}" pointId="${pointId}"/></td>

<script type="text/javascript">
    yukon.dataUpdater.registerCallback(setLastTransmission, { 'value': 'POINT/${pointId}/VALUE' });
</script>
