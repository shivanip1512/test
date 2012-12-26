<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ tag trimDirectiveWhitespaces="true"%>

<%@ attribute name="containerCssClass" %>
<%@ attribute name="menuCssClass" %>
<%@ attribute name="key" %>

<cti:includeScript link="/JavaScript/dropdown_actions.js"/>

<c:if test="${not empty pageScope.key}">
    <c:set var="textClass" value="with-text"/>
</c:if>

<div class="dropdown-container ${textClass} usn ${pageScope.containerCssClass}">
    <c:choose>
	    <c:when test="${not empty pageScope.key}">
	        <a class="text_with_chevron">
	            <span><cti:msg2 key="${pageScope.key}"/></span>
	            <span class="arrow-down"></span>
	        </a>
	    </c:when>
	    <c:otherwise>
		    <a class="icon icon_with_chevron cog">
				<span class="arrow-down"></span>
		    </a>
	    </c:otherwise>
    </c:choose>
	<ul class="dropdown-menu dn ${pageScope.menuCssClass}">
	    <jsp:doBody/>
	</ul>
</div>