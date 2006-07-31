<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<div id="pointPicker_resultScroll">
<table class="pointPickerResultTable">
  <tr><th>Id</th><th>Device</th><th>Point</th></tr>
<c:forEach items="${hitList}" var="thisHit">
  <c:choose>
  <c:when test="${thisHit.pointId eq param.currentPointId}">
  <tr class="pointPicker_currentPointRow">
  </c:when>
  <c:otherwise>
  <tr>
  </c:otherwise>
  </c:choose>
    <td><c:out value="${thisHit.pointId}"/></td>
    <td><c:out value="${thisHit.deviceName}"/></td>
    <td><a href="javascript:pointPicker_selectThisPoint(<c:out value="${thisHit.pointId}"/>)"><c:out value="${thisHit.pointName}"/></a></td>
  </tr>
</c:forEach>

</table>
<c:if test="${empty hitList}">
  <div id="pointPicker_noResult">No results found</div>
</c:if>
<div>
<c:if test="${startIndex > 0}">
  <a href="javascript:pointPicker_previous(<c:out value="${previousIndex}"/>)">Previous</a>
</c:if>

<c:if test="${endIndex < hitCount}">
  <a href="javascript:pointPicker_next(<c:out value="${nextIndex}"/>)">Next</a>
</c:if>

</div>
</div>