<%@ tag trimDirectiveWhitespaces="true" dynamic-attributes="attrs" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="triggerClasses" description="CSS class names applied to the menu trigger." %>
<%@ attribute name="icon" description="The css class name of the icon to use. Default: 'icon-cog'" %>
<%@ attribute name="id" %>
<%@ attribute name="key" %>
<%@ attribute name="label" description="Button text. Overrides key." %>
<%@ attribute name="menuClasses" description="CSS class names applied to the menu." %>
<%@ attribute name="type" description="The type of this element. Either 'button', 'link', or 'icon'. Default: 'icon'. 'link' and 'button' require a 'key'" %>
<%@ attribute name="showArrow" description="Show the down arrow? Default: 'true'" %>
<%@ attribute name="showIcon" description="Ignored when type = 'icon'. Default: 'true'" %>
<%@ attribute name="showLabel" description="Show text on the button? Default: 'true'" %>

<cti:default var="icon" value="icon-cog"/>
<cti:default var="showArrow" value="true"/>
<cti:default var="showIcon" value="true"/>
<cti:default var="showLabel" value="true"/>
<cti:default var="type" value="icon"/>

<div class="dropdown-trigger wsnw usn clearfix ${pageScope.triggerClasses}" <c:if test="${not empty id}">id="${id}"</c:if> <c:forEach items="${pageScope.attrs}" var="attr">${attr.key}="${attr.value}"</c:forEach>>
    <c:choose>
        <c:when test="${type == 'link' && not empty pageScope.key}">
            <a class="button naked">
                <c:if test="${showIcon}"><i class="icon ${icon}"></i></c:if>
                <c:choose>
                    <c:when test="${not empty pageScope.label}"><span class="b-label">${pageScope.label}</span></c:when>
                    <c:otherwise><span class="b-label"><cti:msg2 key="${pageScope.key}"/></span></c:otherwise>
                </c:choose>
                <c:if test="${showArrow}"><i class="icon icon-bullet-arrow-down"></i></c:if>
            </a>
        </c:when>
        <c:when test="${type == 'button'}">
            <cti:default var="key" value="yukon.common.actions"/>
            <button role="button" class="button">
                <c:if test="${showIcon}"><i class="icon ${icon}"></i></c:if>
                <c:if test="${showLabel}">
                    <c:choose>
                        <c:when test="${not empty pageScope.label}"><span class="b-label">${pageScope.label}</span></c:when>
                        <c:otherwise><span class="b-label"><cti:msg2 key="${pageScope.key}"/></span></c:otherwise>
                    </c:choose>
                </c:if>
                <c:if test="${showArrow}"><i class="icon icon-bullet-arrow-down"></i></c:if>
            </button>
        </c:when>
        <c:otherwise>
            <a><i class="icon ${icon} M0 fn"></i><c:if test="${showArrow}"><i class="icon icon-bullet-arrow-down M0 fn"></i></c:if></a>
        </c:otherwise>
    </c:choose>
    <ul class="dropdown-menu dn ${pageScope.menuClasses}" role="menu"><jsp:doBody/></ul>
</div>