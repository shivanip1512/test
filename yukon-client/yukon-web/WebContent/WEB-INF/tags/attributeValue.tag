<%@ attribute name="device" required="true" type="com.cannontech.common.pao.YukonDevice"%>
<%@ attribute name="attribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="showHistoricalReadings" required="false" type="java.lang.Boolean"%>

<cti:attributeResolver device="${device}" attribute="${attribute}" var="pointId"/>
<c:choose>
    <c:when test="${pointId == 0}">
        <span class="errorMessage">
            <i:inline key="yukon.common.attributes.pointNotFound"/>
        </span>
    </c:when>
    <c:otherwise>
        <span class="wsnw">
            <c:choose>
                <c:when test="${empty pageScope.showHistoricalReadings || pageScope.showHistoricalReadings}">
                    <cti:uniqueIdentifier var="uid" prefix="historicalReadings_" />
                    <cti:url var="showHistoricalReadingsUrl" value="/meter/historicalReadings/view">
                        <cti:param name="div_id" value="${uid}" />
                        <cti:param name="pointId" value="${pointId}" />
                        <cti:param name="attribute" value="${attribute}" />
                    </cti:url>
                    <a class="f_ajaxPage pv_history labeled_icon_right history" 
                        data-selector="#${uid}" 
                        href="${showHistoricalReadingsUrl}"
                        title="<cti:msg2 key="yukon.common.historyTooltip"/>">
                        <cti:pointValue pointId="${pointId}"/>
                    </a>
                    <div id="${uid}"></div>
                </c:when>
                <c:otherwise><cti:pointValue pointId="${pointId}"/></c:otherwise>
            </c:choose>
        </span>
    </c:otherwise>
</c:choose>
