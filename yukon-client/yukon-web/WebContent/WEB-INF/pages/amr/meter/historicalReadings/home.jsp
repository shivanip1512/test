<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:url var="viewUrl" value="/meter/historicalReadings/view"/>
<dialog:ajaxPage nameKey="popup" title="${title}" module="amr"
    page="widgetClasses.MeterReadingsWidget.historicalReadings" id="${dialogId}" okEvent="none"
    options="{height: 400, 
              width: 450,
              modal:false,
              buttons: [{text: '${oneMonth}', click: function(){window.location='${oneMonthUrl}';}},
                        {text: '${all}', click: function(){window.location='${allUrl}';}}]}">

    <c:if test="${points.size() > 0}">
        <div class="detail">${resultLimit}</div>
    </c:if>

    <c:choose>
        <c:when test="${points.size() == 0}">
            <div class="empty-list"><i:inline key=".notFound" /></div>
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
            
</dialog:ajaxPage>