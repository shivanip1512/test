<%@ tag dynamic-attributes="widgetAttributes" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="widgetContainerParams" value="${pageScope.widgetAttributes}" scope="request"/>
<jsp:doBody/>