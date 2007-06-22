<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

The current time is: <fmt:formatDate value="${time}" type="both"/>

<ct:widgetActionRefresh method="render" label="Read Now" labelBusy="Reading"/>