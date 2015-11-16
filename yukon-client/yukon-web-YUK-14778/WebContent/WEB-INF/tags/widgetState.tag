<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<script type="text/javascript">
  <c:forEach items="${pageScope.linkParameters}" var="link">
	${widgetParameters.jsWidget}.setParameter("${link.key}", ${cti:jsonString(link.value)});
  </c:forEach>
</script>
