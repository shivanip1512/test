<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="url" value="/spring/stars/hardware/thermostatactivation" />

<html>
<body>
<head>
	<title>Thermostat Activation Status</title>
</head>

<center>
	<div id="main">
		<form id="statusform" method="POST" action="${url}?view">
			<table cellspacing="10">
				<tr>
					<td valign="top">
						<h3>Activation Details :</h3>
					</td>
					<td valign="top">
						${message}
					</td>
				</tr>
				<tr>
					<td/>
					<td valign="top">
						<c:choose>
							<c:when test="${error}">
								<input type="submit" name="back" value="Back"/>
								<input type="hidden" name="accountnumber" value="${accountNumber}"/>
								<input type="hidden" name="serialnumber" value="${serialNumber}"/>
							</c:when>
							<c:otherwise>
								<input type="submit" name="ok" value="Ok"/>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>		
		</form>
	</div>
</center>

</body>
</html>