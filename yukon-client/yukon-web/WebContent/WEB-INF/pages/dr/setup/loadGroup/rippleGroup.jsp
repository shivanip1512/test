<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<tags:sectionContainer2 nameKey="addressing">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".shedTime">
            <tags:intervalDropdown intervals="${shedTimeIntervals}" path="shedTime" noneKey=".continuousLatch" id="js-shed-time"/>
        </tags:nameValue2>
        <c:if test="${isSpecialRippleEnabled}">
            <tags:nameValue2 nameKey=".group">
                <tags:selectWithItems items="${groups}" path="group"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".areaCode">
                <tags:selectWithItems items="${areaCodes}" path="areaCode"/>
            </tags:nameValue2>
        </c:if>
        <c:if test="${isViewMode}">
                <tags:nameValue2 nameKey=".control">
                    <c:choose>
                        <c:when test="${empty loadGroup. control}">
                            <i:inline key="yukon.common.none.choice"/>
                        </c:when>
                        <c:otherwise>
                            ${loadGroup. control}
                        </c:otherwise>
                    </c:choose>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".restore">
                    <c:choose>
                        <c:when test="${empty loadGroup. restore}">
                            <i:inline key="yukon.common.none.choice"/>
                        </c:when>
                        <c:otherwise>
                            ${loadGroup. restore}
                        </c:otherwise>
                    </c:choose>
                </tags:nameValue2>
        </c:if>
        <c:if test="${not isViewMode &&  not isSpecialRippleEnabled}">
            <dr:renderControlAndRestoreBits controlBitsLength="${controlBitsLength}" restoreBitsLength="${restoreBitsLength}"/>
        </c:if>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<c:if test="${not isViewMode && isSpecialRippleEnabled}">
    <tags:sectionContainer2 nameKey="doubleOrders">
        <tags:nameValueContainer2>
            <dr:renderControlAndRestoreBits controlBitsLength="${controlBitsLength}" restoreBitsLength="${restoreBitsLength}"/>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
</c:if>