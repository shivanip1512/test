<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="deviceCollection" required="true" type="com.cannontech.common.bulk.collection.device.model.DeviceCollection"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ attribute name="icon" required="true" type="java.lang.String"%>
<%@ attribute name="linkKey" required="true" type="java.lang.String"%>
<%@ attribute name="descriptionKey" required="false" type="java.lang.String"%>
<%@ attribute name="inputName" required="false" type="java.lang.String"%>
<%@ attribute name="inputValue" required="false" type="java.lang.String"%>

<cti:uniqueIdentifier prefix="form_" var="thisId"/>

 <tr>
    <td class="vam PL0" style="padding-bottom: 10px;">
        <cti:url var="actionUrl" value="${action}">
            <cti:param name="errorDevices" value="${deviceCollection.errorDevices}"/>
            <cti:param name="${pageScope.inputName}" value="${pageScope.inputValue}"/>
            <cti:mapParam value="${deviceCollection.collectionParameters}" />
        </cti:url>
        <cti:button renderMode="appButton" icon="${icon}" href="${actionUrl}" title="${titleText}"/>
    </td>
    <td class="vam PL0" style="padding-right:0px;">
        <div class="box fl meta">
            <cti:msg2 var="linkText" key="${linkKey}"/>
            <div><a class="title" href="${actionUrl}">${linkText}</a></div>
            <cti:msg2 var="descriptionText" key="${descriptionKey}"/>
            <div class="detail">${descriptionText}</div>
        </div>
    </td>
</tr>