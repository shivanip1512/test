<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="maxLength" type="java.lang.Integer" %>

<cti:default var="maxLength" value="50"/>
<c:set var="space" value=" "/>
<c:set var="underscore" value="_"/>

<c:choose>
    <c:when test="${fn:length(value) gt maxLength}">
        <c:set var="cleanName" value="${fn:replace(name, space, underscore)}"/>
        <span id="${cleanName}_shrunk_span">
            <a href="" class="${cleanName}_link">(expand)</a>
        </span>
        <span id="${cleanName}_full_span" style="display:none;">
            <a href="" class="${cleanName}_link">(shrink)</a><br>${value}
        </span>
        <script>
            $('.${cleanName}_link').click(function() {
                $('#${cleanName}_shrunk_span').toggle();
                $('#${cleanName}_full_span').toggle();
                return false;
            });
        </script>
    </c:when>
    <c:otherwise>${value}</c:otherwise>
</c:choose>