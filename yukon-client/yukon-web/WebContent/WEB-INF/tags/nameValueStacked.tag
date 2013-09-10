<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="nameKey" required="true"%>
<%@ attribute name="argument" required="false" type="java.lang.Object" %>

<cti:displayForPageEditModes modes="EDIT,CREATE">
    <h4>
	    <c:if test="${empty pageScope.argument}"><i:inline key="${nameKey}"/></c:if>
	    <c:if test="${not empty pageScope.argument}"><i:inline key="${nameKey}" arguments="${argument}"/></c:if>
    </h4>
    <jsp:doBody/>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="VIEW">
    <div class="stacked property-list">
	    <span class="name">
	       <c:if test="${empty pageScope.argument}"><i:inline key="${nameKey}"/></c:if>
	       <c:if test="${not empty pageScope.argument}"><i:inline key="${nameKey}" arguments="${argument}"/></c:if>:
	    </span>
	    <span class="value"><jsp:doBody/></span>
	</div>
</cti:displayForPageEditModes>