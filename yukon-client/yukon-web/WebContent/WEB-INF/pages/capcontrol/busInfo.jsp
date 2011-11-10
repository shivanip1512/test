<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.substation">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".controlMethod">
            <i:inline key="yukon.web.modules.capcontrol.controlAlgorithm.${bus.subBus.controlMethod}"/> (<i:inline key="yukon.web.modules.capcontrol.controlAlgorithm.${bus.subBus.controlUnits}"/>)
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".varPoint">
            <c:choose>
                <c:when test="${bus.varPoint != null}">${bus.varPoint.pointName}</c:when>
                <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
            </c:choose>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".wattPoint">
            <c:choose>
               <c:when test="${bus.wattPoint != null}">${bus.wattPoint.pointName}</c:when>
               <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
            </c:choose>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".voltPoint">
            <c:choose>
                <c:when test="${bus.voltPoint != null}">${bus.voltPoint.pointName}</c:when>
                <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
            </c:choose>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</cti:msgScope>