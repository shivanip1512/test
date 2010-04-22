<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="labelForId" required="false" %>

<cti:msg2 var="name" key="${nameKey}" blankIfMissing="true"/>

<c:choose>
	<c:when test="${nameValueContainter2}">
        <tr class="${trClass}" id="${trId}">
			<td class="name" style="white-space:nowrap;">
			
				<c:set var="nameContent" value="${name}${(not empty name)? ':':'&nbsp;'}"/>
				
				<c:choose>
					<c:when test="${not empty pageScope.labelForId}">
						<label for="${pageScope.labelForId}">${nameContent}</label>
					</c:when>
					<c:otherwise>
						${nameContent}
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