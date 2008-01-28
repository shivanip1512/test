<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<cti:standardPage title="Cap Bank Controller Points and TS" module="capcontrol_internal">

	<c:if test="${isOneline}">
		<table width="475px">
	        <tr>
	            <td align="center" style="color:#9FBBAC; font-weight: bold; font-size: 16;">${paoName}</td>
	            <td align="right">
	                <a href="javascript:void(0);" style="color: gray; font-weight: bold; font-size: 16;" title="Click To Close" onclick="closePopupWindow();">x</a>
	            </td>
	        </tr>
	        <tr>
	            <td  colspan="2">
	                <hr style="color: gray;"/>
	            </td>
	        </tr>
		</table>
	</c:if>
	
	<div style="<c:if test="${isOneline}">width: 475px; height: 400px;</c:if> overflow: auto;">

        <table <c:if test="${isOneline}">style="color: white;"</c:if> width="95%" border="0" cellspacing="0" cellpadding="0">
			<tr style="text-align: left;">
				<th>Point Name</th>
				<th>Value</th>
				<th>Timestamp</th>
			</tr>
			
			<tr>
				<td style="font-weight: bold;">
					<br>Analog
				</td>
			</tr>
			<c:forEach var="point" items="${pointMap.ANALOG}">
				<tr class="<tags:alternateRow odd="" even="altRow"/>" style="font-size: 10px">
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

			<tr>
				<td style="font-weight: bold;">
					<br>Status
				</td>
			</tr>
			<c:forEach var="point" items="${pointMap.STATUS}">
				<tr class="<tags:alternateRow odd="" even="altRow"/>" style="font-size: 10px">
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

			<tr>
				<td style="font-weight: bold;">
					<br>Configurable Parameters
				</td>
			</tr>
			<c:forEach var="point" items="${pointMap.CONFIGURABLE_PARAMETERS}">
				<tr class="<tags:alternateRow odd="" even="altRow"/>" style="font-size: 10px">
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

			<c:if test="${fn:length(pointMap.MISC) > 0 }">
				<tr>
					<td style="font-weight: bold;">
						<br>Misc
					</td>
				</tr>
				<c:forEach var="point" items="${pointMap.MISC}">
					<tr class="<tags:alternateRow odd="" even="altRow"/>" style="font-size: 10px">
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
			</c:if>
        </table>
        <br>
    </div>  

</cti:standardPage>
