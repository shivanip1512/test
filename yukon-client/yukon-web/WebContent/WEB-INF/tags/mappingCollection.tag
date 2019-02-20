<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="mappingColorCollection" required="true" type="com.cannontech.web.tools.mapping.MappingColorCollection"%>

<c:if test="${!empty mappingColorCollection}">
    <c:set var="collection" value="${mappingColorCollection.deviceCollection}" /> 
    <tags:selectedDevices deviceCollection="${collection}" labelKey="${mappingColorCollection.labelKey}" badgeColor="${mappingColorCollection.color}" /> 
    <c:if test="${collection.deviceCount > 0}">
        <cti:url var="downloadUrl" value="/tools/map/locations/download">
            <cti:mapParam value="${collection.collectionParameters}" />
        </cti:url>
        <cti:url var="filteredActionsUrl" value="/bulk/collectionActions">
            <c:forEach items="${collection.collectionParameters}" var="cp">
                <cti:param name="${cp.key}" value="${cp.value}" />
            </c:forEach>
        </cti:url>
        <cm:dropdown icon="icon-cog">
            <cm:dropdownOption key=".collectionActions" href="${filteredActionsUrl}" icon="icon-cog-go" newTab="true" />
            <cm:dropdownOption icon="icon-csv" key="yukon.common.download" href="${downloadUrl}" />
        </cm:dropdown>
    </c:if>
</c:if>