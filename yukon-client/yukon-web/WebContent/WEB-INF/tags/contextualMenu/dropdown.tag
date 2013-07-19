<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="containerCssClass" %>
<%@ attribute name="icon" description="The css class name of the icon to use. Default: 'icon-cog'" %>
<%@ attribute name="id" %>
<%@ attribute name="key" %>
<%@ attribute name="menuCssClass" %>
<%@ attribute name="type" description="The type of this element. Either 'button', 'link', or 'icon'. Default: 'icon'. 'link' and 'button' require a 'key'" %>
<%@ attribute name="showArrow" description="Show the down arrow? Default: 'true'" %>
<%@ attribute name="showIcon" description="Ignored when type = 'icon'. Default: 'true'" %>

<cti:default var="icon" value="icon-cog"/>
<cti:default var="showArrow" value="true"/>
<cti:default var="showIcon" value="true"/>
<cti:default var="type" value="icon"/>

<div class="f-dropdown_outer_container ${pageScope.containerCssClass}" <c:if test="${not empty id}">id="${id}"</c:if>>
    <div class="dropdown-container usn fr">
      <c:choose>
        <c:when test="${type == 'link' && not empty pageScope.key}">
          <a class="button naked">
            <c:if test="${showIcon}"><i class="icon ${icon}"></i></c:if>
            <span class="label"><cti:msg2 key="${pageScope.key}"/></span>
            <c:if test="${showArrow}"><i class="icon icon-bullet-arrow-down"></i></c:if>
          </a>
        </c:when>
        <c:when test="${type == 'button'}">
          <cti:default var="key" value="yukon.web.defaults.actions"/>
          <button role="button" class="button">
            <c:if test="${showIcon}"><i class="icon ${icon}"></i></c:if>
            <span class="label"><cti:msg2 key="${pageScope.key}"/></span>
            <c:if test="${showArrow}"><i class="icon icon-bullet-arrow-down"></i></c:if>
          </button>
        </c:when>
        <c:otherwise>
          <a><i class="icon ${icon}"></i><c:if test="${showArrow}"><i class="icon icon-bullet-arrow-down"></i></c:if></a>
        </c:otherwise>
      </c:choose>
      <ul class="dropdown-menu dn ${pageScope.menuCssClass}">
        <jsp:doBody/>
      </ul>
    </div>
</div>