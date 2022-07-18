<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">

    <c:choose>
        <c:when test="${warnings.size() > 0}">
            <c:forEach var="warning" items="${warnings}">
                <c:set var="warningColor" value=""/>
                <c:set var="tagText" value=""/>
                <c:if test="${warning.severity == 'HIGH'}">
                    <c:set var="warningColor" value="badge-error"/>
                    <c:set var="tagText" value="${warning.severity}"/>
                </c:if>
                <cti:msg2 var="warningText" key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/>
                <tags:infoListItem statusBackgroundColorClass="${warningColor}" dateTime="${warning.timestamp}"
                    subTitle="${warningText}" tagText="${tagText}" tagColorClass="${warningColor}"></tags:infoListItem>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key=".noRecentWarnings" /></span>
        </c:otherwise>
    </c:choose>

</cti:msgScope>
