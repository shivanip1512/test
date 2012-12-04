<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ tag trimDirectiveWhitespaces="true"%>

<cti:includeScript link="/JavaScript/dropdown_actions.js"/>

<div class="dropdown-container dib">
    <a class="icon icon_with_chevron cog">
		<span class="arrow-down actions_link"></span>
    </a>
	<ul class="dropdown-menu dn">
	    <jsp:doBody/>
	</ul>
</div>