<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="gapHeight" required="false" %>

<c:choose>
    <c:when test="${nameValueContainer2}">
        <tr <c:if test="${not empty pageScope.gapHeight}">style="height:${pageScope.gapHeight};"</c:if>>
            <td colspan="2"></td>
        </tr>
    </c:when>
    <c:otherwise>
        <div class="error" style="font-weight: bold">
            ERROR: The &lt;nameValueGap2&gt; tag must be enclosed in a &lt;nameValueContainer2&gt; tag
        </div>
    </c:otherwise>
</c:choose>