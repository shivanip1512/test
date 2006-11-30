<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<cti:standardPage title="Client Launcher" module="javawebstart">
<cti:standardMenu/>

<c:forEach items="${jnlpList}" var="jnlp">
<p>
<a href="<c:url value="${jnlp.path}" />"><img src="<c:url value="${jnlp.appIcon}" />"></a> <a href="<c:url value="${jnlp.path}" />"><c:out value="${jnlp.appTitle}" /></a>
</p>
</c:forEach>

</cti:standardPage>