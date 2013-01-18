<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>


<cti:url var="viewUrl" value="/meter/historicalReadings/view"/>
<dialog:ajaxPage nameKey="popup" title="${title}" module="amr"
    page="widgetClasses.MeterReadingsWidget.historicalReadings" id="${dialogId}" okEvent="none"
    options="{'buttons': [
                        {text: '${oneMonth}', click: function(){window.location='${oneMonthUrl}';}},
                        {text: '${all}', click: function(){window.location='${allUrl}';}}], modal:false}"
>

    <c:if test="${points.size() > 0}">
        <div class="smallText">
            <tr>
                <td>${resultLimit}</td>
            <tr>
        <div>
    </c:if>

    <div class="scrollingContainer_large">
        <c:choose>
            <c:when test="${points.size() == 0}">
                <i:inline key=".notFound" />
            </c:when>
            <c:otherwise>
                <table id="pointValueList" class="compactResultsTable rowHighlighting">
                    <tr>
                        <th>
                            <tags:sortLink nameKey="tableHeader.timestamp" 
                                baseUrl="${viewUrl}" 
                                fieldName="TIMESTAMP"
                                isDefault="${points.size() > 0}" 
                                styleClass="f_ajaxPage" 
                                descendingByDefault="true"
                                moreAttributes="data-selector=\"#${dialogId}\""/>
                        </th>
                        <th>
                            <tags:sortLink nameKey="tableHeader.value" 
                                baseUrl="${viewUrl}" 
                                fieldName="VALUE"
                                isDefault="false" 
                                styleClass="f_ajaxPage" 
                                moreAttributes="data-selector=\"#${dialogId}\""/>
                        </th>
                    </tr>
    
                    <c:forEach var="row" items="${points}">
                        <tr>
                            <td>${fn:escapeXml(row[0])}</td>
                            <td>${fn:escapeXml(row[1])}</td>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
            
    </div>
</dialog:ajaxPage>
