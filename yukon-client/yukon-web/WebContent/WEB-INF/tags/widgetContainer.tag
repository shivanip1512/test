<%@ tag  dynamic-attributes="widgetAttributes" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<c:set var="widgetContainerParams" value="${widgetAttributes}" scope="request"/>



<jsp:doBody/>

