<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ tag trimDirectiveWhitespaces="true"%>

<%@ attribute name="containerCssClass" %>
<%@ attribute name="menuCssClass" %>
<%@ attribute name="key" %>
<%@ attribute name="type" description="The type of this element. Either 'button', 'link', or 'cog'. Defaults to 'cog'. 'link' and 'button' require a 'key'"%>

<cti:includeScript link="/JavaScript/dropdown_actions.js"/>

<cti:default var="type" value="cog"/>

<div class="f_dropdown_outer_container ${pageScope.containerCssClass}">
	<div class="clearfix">
		<div class="dropdown-container usn fr">
			    <c:choose>
				    <c:when test="${type == 'link' && not empty pageScope.key}">
				        <a class="button naked">
				            <span class="label"><cti:msg2 key="${pageScope.key}"/></span>
				            <i class="icon icon-bullet-arrow-down"></i>
				        </a>
				    </c:when>
				    <c:when test="${type == 'button'}">
			            <cti:default var="key" value="yukon.web.defaults.actions"/>
				        <button>
				            <span class="label"><cti:msg2 key="${pageScope.key}"/></span>
				            <i class="icon icon-bullet-arrow-down"></i>
				        </button>
				    </c:when>
				    <c:otherwise>
					    <a><i class="icon icon-cog"></i><i class="icon icon-bullet-arrow-down"></i></a>
				    </c:otherwise>
			    </c:choose>
				<ul class="dropdown-menu dn ${pageScope.menuCssClass}">
				    <jsp:doBody/>
				</ul>
		</div>
	</div>
</div>