<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="inventoryCollection" required="true" type="java.lang.Object"%>

<cti:includeScript link="/JavaScript/showSelectedDevices.js"/>

<c:set value="${inventoryCollection.count}" var="count"/>

<%-- CREATE URL --%>
<c:if test="${count > 0}">
    <cti:url var="selectedInventoryTableUrl" value="/stars/operator/inventory/selectedInventoryTable">
        <c:forEach var="inventoryCollectionParam" items="${pageScope.inventoryCollection.collectionParameters}">
            <cti:param name="${inventoryCollectionParam.key}" value="${fn:escapeXml(inventoryCollectionParam.value)}"/>
        </c:forEach>
    </cti:url>

    <cti:msg2 var="popupTitle" key="yukon.common.collection.inventory.selectedInventoryPopup.popupTitle"/>

    <tags:simplePopup id="${id}" title="${popupTitle}">
        <div style="max-height:300px;overflow:auto;min-width:300px;">
        <div class="smallBoldLabel" id="${id}InnerDiv" style="text-align:left;"></div>
        </div>
    </tags:simplePopup>
    
</c:if>

<span class="smallBoldLabel">
    <i:inline key="yukon.common.collection.inventory.selectedInventoryPopup.linkLabel"/>
</span>
<span class="smallLink">
	<i:inline key="${inventoryCollection.description}"/>
	<c:if test="${count > 0}">
		<div class="dib">
			<cti:msg2 var="magTitle" key="yukon.common.collection.inventory.selectedInventoryPopup.magnifierTitle"/>
			<a href="javascript:void(0);" title="${magTitle}" class="icon magnifier f_showSelectedInventory" data-function-arguments="{'id':'${id}', 'url':'${selectedInventoryTableUrl}'}">${magTitle}</a>
		</div>
	</c:if>
</span>

<br>
<span class="smallBoldLabel">
    <cti:msg key="yukon.common.collection.inventory.selectedInventoryPopup.count" argument="${count}"/>
</span>