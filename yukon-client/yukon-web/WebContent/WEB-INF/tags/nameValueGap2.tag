<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="gapHeight" required="false" %>

<c:choose>
    <c:when test="${nameValueContainer2}">
        <tr <c:if test="${not empty pageScope.gapHeight}">style="height:${pageScope.gapHeight};"</c:if>>
            <td colspan="2">&nbsp;</td>
        </tr>
    </c:when>
    <c:otherwise>
        <div class="error" style="font-weight: bold">
            ERROR: The &lt;nameValueGap2&gt; tag must be enclosed in a &lt;nameValueContainer2&gt; tag
        </div>
    </c:otherwise>
</c:choose>