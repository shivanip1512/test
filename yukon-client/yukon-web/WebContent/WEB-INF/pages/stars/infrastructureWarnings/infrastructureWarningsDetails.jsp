<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">
    <div class="widget-separator primary-color MT10">
        <b><i:inline key=".recentIssues.title"/></b>
    </div>
    <c:choose>
        <c:when test="${warnings.size() > 0}">
            <c:forEach var="warning" items="${warnings}">
                <c:set var="warningColor" value=""/>
                <c:set var="tagText" value=""/>
                <c:set var="icon" value=""/>
                <c:if test="${warning.severity == 'HIGH'}">
                    <c:set var="warningColor" value="badge-error"/>
                    <c:set var="tagText" value="${warning.severity}"/>
                </c:if>
                <cti:paoDetailUrl var="deviceLink" yukonPao="${warning.paoIdentifier}"/>
                <c:if test="${deviceLink != null}">
                    <c:set var="icon" value="icon-resultset-next-gray"/>
                </c:if>
                <cti:deviceName var="deviceName" deviceId="${warning.paoIdentifier.paoId}"/>
                <cti:msg2 var="deviceDetailsTooltip" key=".deviceDetails"/>
                <cti:msg2 var="warningText" key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/>
                <tags:infoListItem statusBackgroundColorClass="${warningColor}" dateTime="${warning.timestamp}" title="${deviceName}" 
                    subTitle="${warningText}" tagText="${tagText}" tagColorClass="${warningColor}" icon="${icon}" iconLink="${deviceLink}" 
                    iconTooltip="${deviceDetailsTooltip}"></tags:infoListItem>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key=".noRecentWarnings" /></span>
        </c:otherwise>
    </c:choose>
    
    <cti:url var="allWarningsUrl" value="/stars/infrastructureWarnings/detail"/>
    <cti:msg2 var="viewAll" key=".viewAll"/>
    <div style="padding:8px 0px 8px 8px">
        <b><a href="${allWarningsUrl}">${viewAll}</a></b>
        <cti:icon icon="icon-resultset-next-gray" classes="fr cp" href="${allWarningsUrl}" title="${viewAll}"/>
    </div>
    <div style="padding:8px 0px 8px 8px">
        <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
        <span class="fl" title="${lastUpdatedMsg}"><i:inline key=".lastRefresh"/>: <cti:formatDate value="${lastAttemptedRefresh}" type="DATEHMS_12"/></span>
        <cti:button renderMode="image" icon="icon-arrow-refresh" title="${refreshTooltip}" classes="js-update-infrastructure-warnings fr" disabled="${!isRefreshPossible}"/>
    </div>
</cti:msgScope>