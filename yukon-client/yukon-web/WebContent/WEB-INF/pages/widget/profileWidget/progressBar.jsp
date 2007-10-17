<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:choose>

	<c:when test="${empty percentDone}">
		<div style='color:#FF0000;font-weight:bold;'>Request Failed</div>${lastReturnMsg}
	</c:when>

	<c:when test="${percentDone >= 100.0}">
		<div style='color:#006633;font-weight:bold;text-align:center;'>Completed</div>
	</c:when>
	
	<c:when test="${percentDone <= 0.0}">
		<div style='color:#555555;font-weight:bold;text-align:center;'>Pending</div>
	</c:when>

	<c:otherwise>
		<table cellpadding="0px" border="0px">
			<tr>
				<td>
					<div id="progressBorder" style="height:12px; width:100px; border:1px solid black; padding:0px; background-color:#CCCCCC;" align="left">
						<div id="progressInner" style="height: 10px; width: ${percentDone}px; padding:1px; overflow:hidden; background-color:#006633; ">
						</div>
					</div>
				</td>
				<td>
					<div id="percentDone" style="display: inline;">
						${percentDone}%
					</div>
				</td>
			</tr>
		</table>
	</c:otherwise>
	
</c:choose>