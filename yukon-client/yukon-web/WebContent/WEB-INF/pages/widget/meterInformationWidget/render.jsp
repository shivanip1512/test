<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer altRowOn="true">
	<tags:nameValue name="Device Name">${meter.name}</tags:nameValue>
	<tags:nameValue name="Meter Number">${meter.meterNumber}</tags:nameValue>
	<tags:nameValue name="Type">${deviceType}</tags:nameValue>
	<tags:nameValue name="Physical Address">${meter.address}</tags:nameValue>
	<tags:nameValue name="Route">${meter.route}</tags:nameValue>
</tags:nameValueContainer>
