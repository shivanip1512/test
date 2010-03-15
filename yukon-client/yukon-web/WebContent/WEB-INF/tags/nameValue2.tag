<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" %>

<cti:msg2 var="name" key="${pageScope.nameKey}" blankIfMissing="true"/>

<c:set var="nameColumnWidthStyle" value=""/>
<c:if test="${not empty nameColumnWidth}">
	<c:set var="nameColumnWidthStyle" value="width:${nameColumnWidth};"/>
</c:if>

<c:choose>
	<c:when test="${nameValueContainter2}">
        <tr class="${trClass}" id="${trId}">
			<td class="name" style="white-space:nowrap;${nameColumnWidthStyle}">${name}${(not empty name)? ':':'&nbsp;'}</td>
			<td class="value"><jsp:doBody/></td>
		</tr>
	</c:when>
	<c:otherwise>
		<div class="errorRed">ERROR: The &lt;nameValue2&gt; tag must be enclosed in a &lt;nameValueContainer2&gt; tag</div>
	</c:otherwise>
</c:choose>