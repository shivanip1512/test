<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/jqGrid" %>

<cti:msgScope paths="yukon.common">
<%-- INPUTS --%>
<c:if test="${not empty metaInfo}">
    <br/>
    <tags:nameValueContainer style="width:40%;">
        <c:forEach var="metaInfo" items="${metaInfo}">
            <tags:nameValue name="${metaInfo.key}">${fn:escapeXml(metaInfo.value)}</tags:nameValue>
        </c:forEach>
    </tags:nameValueContainer>
</c:if>
<br>

<%-- CSV URL --%>
<cti:simpleReportUrlFromNameTag var="csvUrlBase" viewType="csvView" definitionName="${definitionName}" htmlOutput="true" />
<cti:url var="csvUrl" value="${csvUrlBase}">
    <c:forEach var="input" items="${inputMap}">
        <cti:param name="${input.key}" value="${input.value}"/>
    </c:forEach>
</cti:url>

<%-- PDF URL --%>
<cti:simpleReportUrlFromNameTag var="pdfUrlBase" viewType="pdfView" definitionName="${definitionName}" htmlOutput="true" />
<cti:url var="pdfUrl" value="${pdfUrlBase}">
    <c:forEach var="input" items="${inputMap}">
        <cti:param name="${input.key}" value="${input.value}"/>
    </c:forEach>
</cti:url>

<%-- PURE HTML EXPORT OPTIONS --%>
<c:if test="${pureHtml}">
    <div class="action-area stacked">
        <cti:button nameKey="csv" href="${csvUrl}" icon="icon-page-white-excel"/>
        <cti:button nameKey="pdf" href="${pdfUrl}" icon="icon-pdf"/>
    </div>
</c:if>

<c:choose>

    <%-- jqGrid REPORT TABLE --%>
    <c:when test="${!pureHtml}">
    
        <c:set var="gridWidth" value=""/>
        <c:if test="${not empty pageScope.width}">
            <c:set var="gridWidth" value="${pageScope.width}" />
        </c:if>
        
        <grid:report title="${reportTitle}" 
            height="${height}" 
            width="${gridWidth}" 
            columnInfo="${columnInfo}" 
            dataUrl="${dataUrl}" 
            csvUrl="${csvUrl}" 
            pdfUrl="${pdfUrl}" 
            showLoadMask="${showLoadMask}" 
            refreshRate="${refreshRate}" />
    </c:when>
    
    <%-- PURE HTML REPORT TABLE --%>
    <c:otherwise>
        
        <table class="compact-results-table">
        
            <!-- header -->
            <thead>
                <tr>
                    <c:forEach var="ci" items="${columnInfo}">
                        <th style="text-align:${ci.align};width:${ci.columnWidthPercentage}%">${fn:escapeXml(ci.label)}</th>
                    </c:forEach>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
            <!-- data -->
            <c:forEach var="rowData" items="${data}">
                <tr>
                    <c:forEach var="colData" items="${rowData}" varStatus="colCounter">
                        <td>${fn:escapeXml(colData)}</td>
                    </c:forEach>
                </tr>
            </c:forEach>
            </tbody>
        </table>

    </c:otherwise>

</c:choose>
</cti:msgScope>