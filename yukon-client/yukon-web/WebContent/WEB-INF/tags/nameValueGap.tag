<%@ attribute name="gapHeight" required="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:choose>
	<c:when test="${nameValueContainter}">
		<tr <c:if test="${not empty pageScope.gapHeight}">style="height:${pageScope.gapHeight};"</c:if>>
			<td colspan="2"></td>
		</tr>
	</c:when>
	<c:otherwise>
		<div class="errorRed" style="font-weight: bold">
			ERROR: The &lt;nameValueTap&gt; tag must be enclosed in a &lt;nameValueContainer&gt; tag
		</div>
	</c:otherwise>
</c:choose>