<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="nameKey" required="true" rtexprvalue="true" %>
<%@ attribute name="label" required="false" type="java.lang.Object"%>
<%@ attribute name="labelForId" required="false" %>
<%@ attribute name="excludeColon" required="false" %>
<%@ attribute name="rowId" rtexprvalue="true" %>
<%@ attribute name="rowClass" rtexprvalue="true" %>

<c:choose>
	<c:when test="${nameValueContainter2}">
        <tr <c:if test="${!empty rowId}"> id="${rowId}"</c:if>
            <c:if test="${!empty rowClass}"> class="${rowClass}"</c:if>>
			<td class="name" style="white-space:nowrap;">
			
				<c:set var="colonSuffix" value=":"/>
				<c:if test="${excludeColon == true}">
					<c:set var="colonSuffix" value=""/>
				</c:if>
				
				<c:choose>
					<c:when test="${not empty pageScope.labelForId}">
						<label for="${pageScope.labelForId}"><i:inline key="${label != null ? label : nameKey}"/>${colonSuffix}</label>
					</c:when>
					<c:otherwise>
						<i:inline key="${label != null ? label : nameKey}"/>${colonSuffix}
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