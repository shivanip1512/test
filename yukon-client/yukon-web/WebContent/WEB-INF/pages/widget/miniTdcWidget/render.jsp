<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table class="dataTable" style="width:100%">
  <tr>
    <th>Device Name</th>
    <th>Point Name</th>
    <th>Time Stamp</th>
    <th>Value</th>
  </tr>
  <c:forEach var="point" items="${pointList}">
  <tr>
    <td>${point.deviceName}</td>
    <td>${point.pointName}</td>
    <td><cti:pointValue pointId="${point.pointId}" format="{time|MM/dd/yyyy HH:mm:ss z}"/></td>
    <td><cti:pointValue pointId="${point.pointId}" format="{default}"/></td>
  </tr>
  </c:forEach>
</table>
