<%@ tag trimDirectiveWhitespaces="true" dynamic-attributes="attrs" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="name" required="true" %>
<%@ attribute name="nameColumnWidth" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="nameClass" required="false" %>
<%@ attribute name="valueClass" required="false" %>

<c:choose>
    <c:when test="${nameValueContainer}">
        <c:choose>
            <c:when test="${not empty pageScope.nameColumnWidth}">
                <c:set var="withWidth">style="width:${nameColumnWidth};"</c:set>
            </c:when>
            <c:otherwise><c:set var="withWidth" value=""/></c:otherwise>
        </c:choose>
        <tr <c:if test="${not empty pageScope.id}">id="${id}"</c:if> <c:forEach items="${pageScope.attrs}" var="attr">${attr.key}="${attr.value}"</c:forEach>>
            <td class="name ${pageScope.nameClass}" ${withWidth}>${name}${(not empty name)? ':' : '&nbsp;'}</td>
            <td class="value ${pageScope.valueClass}"><jsp:doBody/></td>
        </tr>
    </c:when>
    <c:otherwise>
        <div class="error">
            <strong>ERROR: The &lt;nameValue&gt; tag must be enclosed in a &lt;nameValueContainer&gt; tag</strong>
        </div>
    </c:otherwise>
</c:choose>