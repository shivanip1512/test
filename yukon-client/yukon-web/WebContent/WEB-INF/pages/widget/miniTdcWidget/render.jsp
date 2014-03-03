<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<table class="dataTable" style="width:100%">
  <tr>
    <th>Device Name</th>
    <th>Point Name</th>
    <th>Time Stamp</th>
    <th>Value</th>
  </tr>
  <c:forEach var="point" items="${pointList}">
  <tr>
    <td>${fn:escapeXml(point.deviceName)}</td>
    <td>${fn:escapeXml(point.pointName)}</td>
    <td><cti:pointValue pointId="${point.pointId}" format="{time|MM/dd/yyyy HH:mm:ss z}"/></td>
    <td><cti:pointValue pointId="${point.pointId}" format="{default}"/></td>
  </tr>
  </c:forEach>
</table>
