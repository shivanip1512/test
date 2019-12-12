<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<tags:sectionContainer2 nameKey="address">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".shedTime">
            <tags:intervalDropdown intervals="${shedTimeIntervals}" path="shedTime" noneKey=".continuousLatch" id="js-shed-time"/>
        </tags:nameValue2>
        <c:if test="${isSpecialRippleEnabled}">
            <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
            <tags:nameValue2 nameKey=".group">
                 <c:choose>
                    <c:when test="${empty loadGroup.group && isViewMode}">
                        <i:inline key="yukon.common.none.choice"/>
                    </c:when>
                    <c:when test="${empty loadGroup.group && not isViewMode}">
                        <tags:selectWithItems items="${groups}" path="group" defaultItemLabel="${selectLbl}" defaultItemValue=""/>
                    </c:when>
                    <c:otherwise>
                        <cti:msg2 key=".lg"/>&nbsp;<tags:selectWithItems items="${groups}" path="group" />
                    </c:otherwise>
                 </c:choose>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".areaCode" rowClass="js-area-code-row">
                <c:choose>
                    <c:when test="${empty loadGroup.areaCode && isViewMode}">
                        <i:inline key="yukon.common.none.choice"/>
                    </c:when>
                    <c:when test="${empty loadGroup.areaCode && not isViewMode}">
                        <tags:selectWithItems items="${areaCodes}" path="areaCode" defaultItemLabel="${selectLbl}" defaultItemValue=""/>
                    </c:when>
                    <c:otherwise>
                        <tags:selectWithItems items="${areaCodes}" path="areaCode"/>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
        </c:if>
        <c:if test="${isViewMode &&  not isSpecialRippleEnabled}">
            <tags:nameValue2 nameKey=".control">
                <c:choose>
                    <c:when test="${empty loadGroup.control}">
                        <i:inline key="yukon.common.none.choice"/>
                    </c:when>
                    <c:otherwise>
                        ${loadGroup.control}
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".restore">
                <c:choose>
                    <c:when test="${empty loadGroup.restore}">
                        <i:inline key="yukon.common.none.choice"/>
                    </c:when>
                    <c:otherwise>
                        ${loadGroup.restore}
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
        </c:if>
        <c:if test="${not isViewMode &&  not isSpecialRippleEnabled}">
            <dr:renderControlAndRestoreBits controlBitsLength="${controlBitsLength}" restoreBitsLength="${restoreBitsLength}"/>
        </c:if>
    </tags:nameValueContainer2>
</tags:sectionContainer2>
<c:if test="${isSpecialRippleEnabled}">
    <tags:sectionContainer2 nameKey="doubleOrders">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".control" nameClass="vam">
                <form:hidden path="control" cssClass="js-control-value"/>
                <dr:renderSpecialRippleBits containerCss="js-control-address" isDisabled="${isViewMode}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".restore" nameClass="vam">
                <form:hidden path="restore" cssClass="js-restore-value"/>
                <dr:renderSpecialRippleBits containerCss="js-restore-address" isDisabled="${isViewMode}"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
</c:if>