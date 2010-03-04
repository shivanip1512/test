<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="width" required="false" type="java.lang.String"%>

<c:if test="${empty pageScope.width}">
	<c:set var="width" value="80px"/>
</c:if>

<cti:msg var="resetButtonText" key="yukon.web.defaults.button.reset.label" />
<input type="reset" value="${resetButtonText}" style="width:${pageScope.width}">