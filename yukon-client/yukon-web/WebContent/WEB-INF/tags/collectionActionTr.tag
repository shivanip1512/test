<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="deviceCollection" required="true" type="com.cannontech.common.bulk.collection.DeviceCollection"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ attribute name="inputName" required="false" type="java.lang.String"%>
<%@ attribute name="inputValue" required="false" type="java.lang.String"%>
<%@ attribute name="buttonValue" required="true" type="java.lang.String"%>
<%@ attribute name="description" required="true" type="java.lang.String"%>

 <tr>
    <td style="vertical-align: top;padding-right: 10px;padding-bottom: 10px;">
        <form method="get" action="${action}">
            <input type="hidden" name="${inputName}" value="${inputValue}" />
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            <input type="submit" value="${buttonValue}" style="width:140px;"/>
        </form>
    </td>
    <td style="vertical-align: top;padding-right: 1px;padding-bottom: 10px;">${description}</td>
</tr>