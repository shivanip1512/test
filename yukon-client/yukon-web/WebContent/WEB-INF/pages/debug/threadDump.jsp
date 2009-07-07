<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Thread Dump" module="debug">
<cti:standardMenu menuSelection="other|threadDump"/>

<c:forEach items="${dump}" var="threadEntry">
<div class="indentedList">
<h3>${threadEntry.key.name}</h3>
<div>
<c:forEach items="${threadEntry.value}" var="stackEntry">
<div>${stackEntry}</div>
</c:forEach>
</div>
</div>
</c:forEach>

</cti:standardPage>
