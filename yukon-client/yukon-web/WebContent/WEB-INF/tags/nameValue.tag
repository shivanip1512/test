<%@ attribute name="name" required="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
	<c:when test="${nameValueContainter}">
		<c:choose>
			<c:when test="${altRowOn && altRow}">
				<c:set var="altRow" value="false" scope="request"/>
				<tr class="altRow">
			</c:when>
			<c:otherwise>
				<c:set var="altRow" value="true" scope="request"/>
				<tr>
			</c:otherwise>
		</c:choose>
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