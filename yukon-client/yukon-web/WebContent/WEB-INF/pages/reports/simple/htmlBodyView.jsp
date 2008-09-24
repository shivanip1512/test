<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

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
<c:url var="csvUrl" value="${csvUrlBase}">
    <c:forEach var="input" items="${inputMap}">
        <c:param name="${input.key}" value="${input.value}"/>
    </c:forEach>
</c:url>

<%-- PDF URL --%>
<cti:simpleReportUrlFromNameTag var="pdfUrlBase" viewType="pdfView" definitionName="${definitionName}" htmlOutput="true" />
<c:url var="pdfUrl" value="${pdfUrlBase}">
    <c:forEach var="input" items="${inputMap}">
        <c:param name="${input.key}" value="${input.value}"/>
    </c:forEach>
</c:url>


<%-- REPORT TABLE --%>
<tags:extReportGrid title="${reportTitle}" height="350" width="1000" columnInfo="${columnInfo}" dataUrl="${dataUrl}" csvUrl="${csvUrl}" pdfUrl="${pdfUrl}" />
