<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.capcontrol.substation">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".controlMethod">
            <c:when test="${bus.controlMethod != null}">
                <i:inline key="${bus.controlMethod}"/> (<i:inline key="${bus.algorithm}"/>)
            </c:when>
            <c:otherwise>
                <span class="empty-list"><i:inline key="yukon.web.defaults.none"/></span>
            </c:otherwise>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".varPoint">
            <c:choose>
                <c:when test="${bus.varPoint != null}">${fn:escapeXml(bus.varPoint.pointName)}</c:when>
                <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
            </c:choose>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".wattPoint">
            <c:choose>
               <c:when test="${bus.wattPoint != null}">${fn:escapeXml(bus.wattPoint.pointName)}</c:when>
               <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
            </c:choose>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".voltPoint">
            <c:choose>
                <c:when test="${bus.voltPoint != null}">${fn:escapeXml(bus.voltPoint.pointName)}</c:when>
                <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
            </c:choose>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</cti:msgScope>