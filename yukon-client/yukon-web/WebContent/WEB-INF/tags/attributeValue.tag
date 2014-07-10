<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ attribute name="attribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute" %>

<%@ attribute name="showHistoricalReadings" type="java.lang.Boolean" %>

<cti:attributeResolver pao="${pao}" attribute="${attribute}" var="pointId"/>
<c:choose>
    <c:when test="${pointId == 0}">
        <span class="error"><i:inline key="yukon.common.attributes.pointNotFound"/></span>
    </c:when>
    <c:otherwise>
        <span class="wsnw">
            <c:choose>
                <c:when test="${empty pageScope.showHistoricalReadings || pageScope.showHistoricalReadings}">
                    <cti:uniqueIdentifier var="popupId" prefix="attribute-values-"/>
                    <cti:url var="valuesUrl" value="/meter/historicalReadings/view">
                        <cti:param name="pointId" value="${pointId}"/>
                        <cti:param name="attribute" value="${attribute}"/>
                    </cti:url>
                    <a href="javascript:void(0);" data-popup="#${popupId}"><cti:pointValue pointId="${pointId}"/></a>
                    <div id="${popupId}" data-width="500" data-height="400" data-url="${valuesUrl}"></div>
                </c:when>
                <c:otherwise><cti:pointValue pointId="${pointId}"/></c:otherwise>
            </c:choose>
        </span>
    </c:otherwise>
</c:choose>
