<%@ attribute name="name" required="true" %>
<%@ attribute name="rowHighlight" required="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
	<c:when test="${nameValueContainter}">
		<c:choose>
			<c:when test="${rowHighlight}">
				<tr style="background-color: yellow">
			</c:when>
			<c:when test="${altRowOn && altRow}">
				<tr class="altRow">
			</c:when>
			<c:otherwise>
				<tr>
			</c:otherwise>
		</c:choose>
		<c:set var="altRow" value="${!altRow}" scope="request"/>

			<td class="name">${name}:</td>
			<td class="value"><jsp:doBody/></td>
		</tr>
	</c:when>
	<c:otherwise>
		<font color="red" style="font-weight: bold">
			ERROR: The &lt;nameValue&gt; tag must be enclosed in a &lt;nameValueContainer&gt; tag
		</font>
	</c:otherwise>
</c:choose>