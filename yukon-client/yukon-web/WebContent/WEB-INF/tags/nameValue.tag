<%@ attribute name="name" required="true" %>
<%@ attribute name="rowHighlight" required="false" %>
<%@ attribute name="nameColumnWidth" required="false" %>
<%@ attribute name="id" required="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:uniqueIdentifier var="trId" prefix="nameValueTr_"/>
<c:if test="${not empty pageScope.id}">
    <c:set var="trId" value="${pageScope.id}" />
</c:if>

<c:choose>
	<c:when test="${nameValueContainter}">
		<c:choose>
			<c:when test="${rowHighlight}">
				<tr class="highlighted" id="${trId}">
			</c:when>
			<c:when test="${altRowOn && altRow}">
				<tr class="altRow" id="${trId}">
			</c:when>
			<c:otherwise>
				<tr id="${trId}">
			</c:otherwise>
		</c:choose>
		<c:set var="altRow" value="${!altRow}" scope="request"/>

			<td class="name" <c:if test="${not empty nameColumnWidth}">style="width:${nameColumnWidth};"</c:if>>${name}${(not empty name)? ':':'&nbsp;'}</td>
			<td class="value"><jsp:doBody/></td>
		</tr>
	</c:when>
	<c:otherwise>
		<div class="errorRed" style="font-weight: bold">
			ERROR: The &lt;nameValue&gt; tag must be enclosed in a &lt;nameValueContainer&gt; tag
		</div>
	</c:otherwise>
</c:choose>