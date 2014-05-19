<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="device" required="true" type="com.cannontech.common.pao.YukonDevice" %>
<%@ attribute name="attribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="showHistoricalReadings" required="false" type="java.lang.Boolean"%>

<cti:attributeResolver device="${device}" attribute="${attribute}" var="pointId"/>
<c:choose>
    <c:when test="${pointId == 0}">
        <span class="error">
            <i:inline key="yukon.common.attributes.pointNotFound"/>
        </span>
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
                    <a href="javascript:void(0);" popup="#${popupId}"><cti:pointValue pointId="${pointId}"/></a>
                    <div id="${popupId}" data-width="500" data-height="400" data-url="${valuesUrl}"></div>
                </c:when>
                <c:otherwise><cti:pointValue pointId="${pointId}"/></c:otherwise>
            </c:choose>
        </span>
    </c:otherwise>
</c:choose>
