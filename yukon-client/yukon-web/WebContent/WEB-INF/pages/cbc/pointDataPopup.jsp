<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<table id="headerTable" style="text-align:left; color:#9FBBAC; font-weight: bold">
	<tr>
		<td >
			Name
		</td>
		<td>
			Value
		</td>
		<td>
			Timestamp
		</td>
		
	</tr>
</table>
<div style="height: 500px; overflow: auto">
	<table style="color:white" id="dataTable">

		<c:forEach var="point" items="${pointList}">
			<tr>
				<td>
					${point.pointName}
				</td>
				<td>
					${point.value}
				</td>
				<td>
					${point.timestamp}
				</td>
				
			</tr>
		</c:forEach>
	</table>
	<div>