<%@ attribute name="device" required="true" type="com.cannontech.common.device.YukonDevice"%>
<%@ attribute name="attribute" required="true" type="com.cannontech.common.device.attribute.model.Attribute"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:attributeResolver device="${device}" attribute="${attribute}" var="pointId"/>
<cti:pointValue pointId="${pointId}"/>
