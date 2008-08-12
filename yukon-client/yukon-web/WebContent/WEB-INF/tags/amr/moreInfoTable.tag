<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="infoList" required="true" type="java.util.List"%>
<%@ attribute name="indent" required="false" type="java.lang.Integer"%>
<%@ attribute name="titleSize" required="false" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/amr" prefix="amr" %>

<c:if test="${empty indent}">
    <c:set var="indent" value="0" />
</c:if>

<c:set var="titleStyle" value="" />
<c:if test="${not empty titleSize}">
    <c:set var="titleStyle" value="style='font-size:${titleSize}px;'" />
</c:if>

<br>
<div style="padding-left:${indent}px">
    <table class="compactResultsTable">
        <tr><th colspan="2" align="left" ${titleStyle}>${title}:</th></tr>
        <amr:moreInfoRows infoList="${infoList}" />
    </table>
</div>