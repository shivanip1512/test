<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="deviceCollection" required="true" type="com.cannontech.common.bulk.collection.device.model.DeviceCollection"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ attribute name="icon" required="true" type="java.lang.String"%>
<%@ attribute name="linkKey" required="true" type="java.lang.String"%>
<%@ attribute name="descriptionKey" required="false" type="java.lang.String"%>
<%@ attribute name="inputName" required="false" type="java.lang.String"%>
<%@ attribute name="inputValue" required="false" type="java.lang.String"%>
<%@ attribute name="ajaxSubmit" required="false" type="java.lang.Boolean"%>

<cti:msg2 var="linkText" key="${linkKey}"/>
<cti:msg2 var="descriptionText" key="${descriptionKey}"/>

    <td class="vam PL0" style="padding-bottom: 10px;">
        <cti:url var="actionUrl" value="${action}">
            <cti:param name="errorDevices" value="${deviceCollection.errorDevices}"/>
            <cti:param name="${pageScope.inputName}" value="${pageScope.inputValue}"/>
            <cti:mapParam value="${deviceCollection.collectionParameters}" />
        </cti:url>
        <c:choose>
            <c:when test="${ajaxSubmit}">
                <cti:button renderMode="appButton" icon="${icon}" title="${descriptionText}" classes="js-collection-action" data-url="${actionUrl}" data-action="${linkText}"/>
            </c:when>
            <c:otherwise>
                <a href="${actionUrl}"><cti:button renderMode="appButton" icon="${icon}" title="${descriptionText}"/></a>
            </c:otherwise>
        </c:choose>
    </td>
    <td class="vam PL0 PR0">
        <div class="box fl meta">
            <c:choose>
                <c:when test="${ajaxSubmit}">
                    <div><a href="#" class="title js-collection-action" data-url="${actionUrl}" data-action="${linkText}">${linkText}</a></div>
                </c:when>
                <c:otherwise>
                    <div><a class="title" href="${actionUrl}">${linkText}</a></div> 
                </c:otherwise>
            </c:choose>
            <div class="detail">${descriptionText}</div>
        </div>
    </td>
</tr>