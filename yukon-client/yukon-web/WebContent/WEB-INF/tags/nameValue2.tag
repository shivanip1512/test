<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="labelForId" required="false" %>
<%@ attribute name="excludeColon" required="false" %>

<c:choose>
	<c:when test="${nameValueContainter2}">
        <tr class="${trClass}" id="${trId}">
			<td class="name" style="white-space:nowrap;">
			
				<c:set var="colonSuffix" value=":"/>
				<c:if test="${excludeColon == true}">
					<c:set var="colonSuffix" value=""/>
				</c:if>
				
				<c:choose>
					<c:when test="${not empty pageScope.labelForId}">
						<label for="${pageScope.labelForId}"><i:inline key="${nameKey}"/>${colonSuffix}</label>
					</c:when>
					<c:otherwise>
						<i:inline key="${nameKey}"/>${colonSuffix}
					</c:otherwise>
				</c:choose>
				
			</td>
			
			<td class="value"><jsp:doBody/></td>
		</tr>
	</c:when>
	<c:otherwise>
		<div class="errorRed">ERROR: The &lt;nameValue2&gt; tag must be enclosed in a &lt;nameValueContainer2&gt; tag</div>
	</c:otherwise>
</c:choose>