<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:standardPage title="Client Launcher" module="javawebstart">
<cti:standardMenu/>

<table id="launcherLinks"><tbody>
<c:forEach items="${jnlpList}" var="jnlp">
<tr>
<td>
<a href="<c:url value="${jnlp.path}" />">
  <img src="<c:url value="${jnlp.appIcon}" />" border="0">
</a> 
</td>

<td>
<a href="<c:url value="${jnlp.path}" />">
  ${jnlp.appTitle}
</a>
<br>
<span class="description">${jnlp.appDescription}</span>
</td>
</tr>
</c:forEach>
</tbody>
</table>
</cti:standardPage>