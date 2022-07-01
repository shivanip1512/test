<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">
    <tags:sectionContainer2 nameKey="recentIssues">
    
        <c:choose>
            <c:when test="${warnings.size() > 0}">
                <c:forEach var="warning" items="${warnings}">
                    <c:set var="warningColor" value=""/>
                    <c:set var="tagText" value=""/>
                    <c:if test="${warning.severity == 'HIGH'}">
                        <c:set var="warningColor" value="badge-error"/>
                        <c:set var="tagText" value="${warning.severity}"/>
                    </c:if>
                    <cti:deviceName var="deviceName" deviceId="${warning.paoIdentifier.paoId}"/>
                    <cti:msg2 var="warningText" key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/>
                    <tags:infoListItem statusBackgroundColorClass="${warningColor}" dateTime="${warning.timestamp}" 
                        title="${deviceName}" subTitle="${warningText}" tagText="${tagText}" tagColor="${warningColor}"></tags:infoListItem>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <span class="empty-list"><i:inline key=".noRecentWarnings" /></span>
            </c:otherwise>
        </c:choose>
    
<%--         <table class="compact-results-table" width="100%">
            <th width="20%"><i:inline key=".name"/></th>
            <th width="60%"><i:inline key=".status"/></th>
            <th width="20%"><i:inline key="yukon.common.duration"/></th>
        
            <c:choose>
                <c:when test="${warnings.size() > 0}">
                    <c:forEach var="warning" items="${warnings}">
                        <tr>
                            <td class="wsnw">
                                <cti:paoDetailUrl yukonPao="${warning.paoIdentifier}" newTab="true">
                                    <cti:deviceName deviceId="${warning.paoIdentifier.paoId}"/>
                                </cti:paoDetailUrl>
                            </td>
                            <td>
                                <c:set var="warningColor" value="warning"/>
                                <c:if test="${warning.severity == 'HIGH'}">
                                    <c:set var="warningColor" value="error"/>
                                </c:if>
                                <span class="${warningColor}">
                                    <i:inline key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/>
                                </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${warning.timestamp le epoch1990}">
                                        <cti:msg2 key="yukon.common.dashes" var="timestamp"/>
                                        <div title="${timestamp}">
                                            <i:inline key="${warning.approximateDurationKey}" arguments="${warning.approximateDurationValue}"/>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:formatDate value="${warning.timestamp}" type="FULL" var="timestamp"/>
                                        <div title="${timestamp}">
                                            <i:inline key="${warning.approximateDurationKey}" arguments="${warning.approximateDurationValue}"/>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="3">
                            <span class="empty-list"><i:inline key=".noRecentWarnings" /></span>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table> --%>
        
    </tags:sectionContainer2>
    <span class="fr">
        <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
        <span class="fl" style="font-size:11px" title="${lastUpdatedMsg}"><cti:formatDate value="${lastAttemptedRefresh}" type="DATEHMS_12"/></span>
        <cti:button renderMode="image" icon="icon-arrow-refresh" title="${refreshTooltip}" classes="js-update-infrastructure-warnings" disabled="${!isRefreshPossible}"/>
    </span>
</cti:msgScope>