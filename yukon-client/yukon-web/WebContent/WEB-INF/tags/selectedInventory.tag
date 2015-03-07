<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="id" %>
<%@ attribute name="inventoryCollection" required="true" type="java.lang.Object" %>

<cti:uniqueIdentifier prefix="selected-inventory-" var="thisId"/>
<cti:default var="id" value="${thisId}"/>
<c:set var="count" value="${inventoryCollection.count}"/>

<cti:msgScope paths=",yukon.common.collection.inventory.selectedInventoryPopup">
<%-- CREATE URL --%>
<c:if test="${count > 0}">
    <cti:url var="url" value="/stars/operator/inventory/selectedInventoryTable">
        <c:forEach var="entry" items="${pageScope.inventoryCollection.collectionParameters}">
            <cti:param name="${entry.key}" value="${fn:escapeXml(entry.value)}"/>
        </c:forEach>
    </cti:url>
    <div id="${id}-popup" class="dn"
        data-title="<cti:msg2 key=".popupTitle"/>"
        data-width="450" 
        data-height="300" 
        data-url="${url}"></div>
</c:if>
<div id="${id}">
    <strong><i:inline key=".linkLabel"/>:</strong>&nbsp;
    <span class="badge js-count">${count}</span>&nbsp;
    <i:inline key="${inventoryCollection.description}"/>
    <c:if test="${count > 0}">
        <cti:msg2 var="magTitle" key=".magnifierTitle"/>
        <a href="javascript:void(0);" title="${magTitle}" class="dib" data-popup="#${id}-popup"
            data-popup-toggle><cti:icon icon="icon-magnifier"/></a>
    </c:if>
</div>
</cti:msgScope>