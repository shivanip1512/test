<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="deviceCollection" required="true" type="com.cannontech.common.bulk.collection.device.DeviceCollection"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ attribute name="inputName" required="false" type="java.lang.String"%>
<%@ attribute name="inputValue" required="false" type="java.lang.String"%>
<%@ attribute name="buttonValue" required="true" type="java.lang.String"%>
<%@ attribute name="description" required="true" type="java.lang.String"%>

 <tr>
    <td style="vertical-align: top;padding-right: 10px;padding-bottom: 10px;">
        <form method="get" action="${action}">
            <input type="hidden" name="${pageScope.inputName}" value="${pageScope.inputValue}" />
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <input type="submit" value="${buttonValue}"/>
        </form>
    </td>
    <td style="vertical-align: top;padding-right: 1px;padding-bottom: 10px;">${description}</td>
</tr>