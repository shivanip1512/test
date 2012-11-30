<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="nameKey" %>
<%@ attribute name="argument" required="false" type="java.lang.Object" %>
<%@ attribute name="label" required="false" type="java.lang.Object" %>
<%@ attribute name="labelForId" required="false" %>
<%@ attribute name="excludeColon" required="false" %>
<%@ attribute name="rowId" %>
<%@ attribute name="rowClass" %>

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
                        <c:choose>
                            <c:when test="${not empty pageScope.argument}">
                                <label for="${pageScope.labelForId}"><i:inline key="${label != null ? label : nameKey}" arguments="${argument}"/>${colonSuffix}</label>
                            </c:when>
                            <c:otherwise>
                                <label for="${pageScope.labelForId}"><i:inline key="${label != null ? label : nameKey}"/>${colonSuffix}</label>
                            </c:otherwise>
                        </c:choose>
					</c:when>
					<c:otherwise>
                        <c:choose>
                            <c:when test="${not empty pageScope.argument}">
                                <i:inline key="${label != null ? label : nameKey}" arguments="${argument}"/>${colonSuffix}
                            </c:when>
                            <c:otherwise>
                                <i:inline key="${label != null ? label : nameKey}"/>${colonSuffix}
                            </c:otherwise>    
                        </c:choose>
					</c:otherwise>
				</c:choose>
				
			</td>
			
			<td class="value"><jsp:doBody/></td>
		</tr>
	</c:when>
	<c:otherwise>
		<div class="error">ERROR: The &lt;nameValue2&gt; tag must be enclosed in a &lt;nameValueContainer2&gt; tag</div>
	</c:otherwise>
</c:choose>