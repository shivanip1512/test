<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings">
<div data-reloadable>

    <input class="f-popup-title" type="hidden" value="${fn:escapeXml(title)}"> 

    <c:set var="viewUrl" value="/meter/historicalReadings/view"/>
    <div class="form-control">
    <c:if test="${points.size() > 0}">
        <span class="detail">${resultLimit}</span>
    </c:if>
        <cti:url var="oneMonthUrl" value="${oneMonthUrl}"/>
        <cti:url var="allUrl" value="${allUrl}"/>
        <cti:button label="${oneMonth}" href="${oneMonthUrl}" classes="fr right" icon="icon-page-white-excel"/>
        <cti:button label="${all}" href="${allUrl}" classes="fr left" icon="icon-page-white-excel"/>
    </div>

    <c:choose>
        <c:when test="${points.size() == 0}">
            <div class="empty-list"><i:inline key=".notFound"/></div>
        </c:when>
        <c:otherwise>
            <table id="pointValueList" class="compact-results-table row-highlighting">
                <thead>
                    <tr>
                        <th>
                            <tags:sortLink nameKey="tableHeader.timestamp" 
                                baseUrl="${viewUrl}" 
                                fieldName="TIMESTAMP"
                                isDefault="${points.size() > 0}" 
                                descendingByDefault="true"/>
                        </th>
                        <th>
                            <tags:sortLink nameKey="tableHeader.value" 
                                baseUrl="${viewUrl}" 
                                fieldName="VALUE"
                                isDefault="false"/>
                        </th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="row" items="${points}">
                        <tr>
                            <td>${fn:escapeXml(row[0])}</td>
                            <td>${fn:escapeXml(row[1])}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
</cti:msgScope>