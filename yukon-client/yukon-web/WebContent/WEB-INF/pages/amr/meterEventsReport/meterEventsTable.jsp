<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.amr.meterEventsReport.report">

<cti:toJson id="meterEventsTableModelData" object="${meterEventsTableModelData}"/>

<c:set var="controls">
    <c:if test="${not empty meterEvents.resultList}">
        <cm:deviceCollectionMenu deviceCollection="${collectionFromReportResults}"
            key="yukon.web.modules.common.contextualMenu.actions"/>
    </c:if>
</c:set>
<tags:sectionContainer2 nameKey="tableTitle" controls="${controls}">
    <c:choose>
        <c:when test="${empty meterEvents.resultList}">
            <span class="empty-list"><i:inline key=".noEvents" /></span>
        </c:when>
        <c:otherwise>
            <table id="eventsTable" class="compact-results-table has-actions dashed">
                <thead>
                    <tr>
                        <tags:sort column="${nameColumn}"/>
                        <tags:sort column="${numberColumn}"/>
                        <tags:sort column="${typeColumn}"/>
                        <tags:sort column="${dateColumn}"/>
                        <tags:sort column="${eventColumn}"/>
                        <tags:sort column="${valueColumn}"/>
                        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="event" items="${meterEvents.resultList}">
                        <tr>
                            <td>
                                <cti:paoDetailUrl yukonPao="${event}" >${fn:escapeXml(event.deviceName)}</cti:paoDetailUrl>
                            </td>
                            <td>${event.meterNumber}</td>
                            <td>
                                <c:choose>
                                     <c:when test="${event.getPaoIdentifier().getPaoType().isRfRelay()}">
                                        <tags:paoType yukonPao="${event}" showLink="false" />
                                     </c:when>
                                     <c:when test="${event.getPaoIdentifier().getPaoType().isRfGateway()}">
                                        <tags:paoType yukonPao="${event}" showLink="false" />
                                     </c:when>
                                     <c:otherwise>
                                        <tags:paoType yukonPao="${event}" />
                                     </c:otherwise>
                                </c:choose>
                            </td>
                            <td><cti:formatDate type="BOTH" value="${event.pointValueHolder.pointDataTimeStamp}"/></td>
                            <td>${fn:escapeXml(event.pointName)}</td>
                            <td>
                                <cti:pointStatus pointId="${event.pointValueHolder.id}" rawState="${event.pointValueHolder.value}"/>&nbsp;
                                <cti:pointValueFormatter format="VALUE" value="${event.pointValueHolder}" />
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${event.getPaoIdentifier().getPaoType().isRfRelay()}">
                                        <cm:singleDeviceMenu deviceId="${event.paoIdentifier.paoId}" menuBeanId="relayMenuSingleDevice"/>
                                    </c:when>
                                    <c:when test="${event.getPaoIdentifier().getPaoType().isRfGateway()}">
                                        <cm:singleDeviceMenu deviceId="${event.paoIdentifier.paoId}" menuBeanId="gatewayMenuSingleDevice"/>
                                    </c:when>
                                    
                                    <c:otherwise>
                                        <cm:singleDeviceMenu deviceId="${event.paoIdentifier.paoId}" menuBeanId="meterMenuSingleDevice"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <tags:pagingResultsControls result="${meterEvents}" adjustPageCount="true"/>
        </c:otherwise>
    </c:choose>
</tags:sectionContainer2>
</cti:msgScope>