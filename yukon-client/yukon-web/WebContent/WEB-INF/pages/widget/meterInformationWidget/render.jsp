<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer altRowOn="true">
	<tags:nameValue name="Type">${deviceType}</tags:nameValue>
	<tags:nameValue name="Physical Address">${device.address}</tags:nameValue>
	<tags:nameValue name="Device Name">${device.paoName}</tags:nameValue>
	<tags:nameValue name="Description">${device.paoDescription}</tags:nameValue>
</tags:nameValueContainer>
