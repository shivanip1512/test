<c:url var="url" value="/spring/stars/hardware/thermostatactivation" />

<html>
	<body>
	<head>
		<title>Thermostat Activation</title>
	</head>

	<center>
		<div id="main">
			<h2>
				Thermostat Activation
			</h2>
			<form id="activateform" method="POST" action="${url}?activate"
				style="margin: 0px; padding: 0px;">
				<table cellspacing="5">
					<tr align="center">
						<td valign="top">
							Account Number :
						</td>
						<td valign="top">
							<input type="text" name="accountnumber" value="${accountNumber}" />
						</td>
					</tr>
					<tr align="center">
						<td valign="top">
							Serial Number :
						</td>
						<td valign="top">
							<input type="text" name="serialnumber" value="${serialNumber}" />
						</td>
					<tr>
						<td />
						<td align="right">
							<input type="submit" name="Activate" value="Activate" />
						</td>
					</tr>
				</table>
			</form>
		</div>
	</center>

	</body>
</html>
