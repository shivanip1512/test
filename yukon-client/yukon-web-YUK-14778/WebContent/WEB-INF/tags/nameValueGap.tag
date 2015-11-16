<%@ attribute name="gapHeight" required="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<c:choose>
    <c:when test="${nameValueContainer}">
        <tr <c:if test="${not empty pageScope.gapHeight}">style="height:${pageScope.gapHeight};"</c:if>>
            <td colspan="2"></td>
        </tr>
    </c:when>
    <c:otherwise>
        <div class="error" style="font-weight: bold">
            ERROR: The &lt;nameValueGap&gt; tag must be enclosed in a &lt;nameValueContainer&gt; tag
        </div>
    </c:otherwise>
</c:choose>