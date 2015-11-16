<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="deviceCollection" required="true" type="com.cannontech.common.bulk.collection.device.model.DeviceCollection"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ attribute name="inputName" required="false" type="java.lang.String"%>
<%@ attribute name="inputValue" required="false" type="java.lang.String"%>
<%@ attribute name="buttonValue" required="true" type="java.lang.String"%>
<%@ attribute name="description" required="true" type="java.lang.String"%>

<cti:uniqueIdentifier prefix="form_" var="thisId"/>

 <tr>
    <td style="vertical-align: top;padding-right: 10px;padding-bottom: 10px;">
        <form method="get" action="${action}" id="${thisId}">
        	<input type="hidden" name="errorDevices" value="${deviceCollection.errorDevices}" />
            <input type="hidden" name="${pageScope.inputName}" value="${pageScope.inputValue}" />
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <a href="javascript:$('#${thisId}').submit();" class="wsnw">${fn:escapeXml(buttonValue)}</a>
        </form>
    </td>
    <td style="vertical-align: top;padding-right: 1px;padding-bottom: 10px;">${description}</td>
</tr>