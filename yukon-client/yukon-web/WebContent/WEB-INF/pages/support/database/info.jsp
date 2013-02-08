<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="databaseInfo">

<h3 class="indentedElementHeading"><i:inline key=".dbConnection"/></h3>
<div>
<i:inline key=".dbConnection.jdbcUrl"/> ${dbUrl}<br />
<i:inline key=".dbConnection.jdbcUser"/> ${dbUser}<br />
</div>

</cti:standardPage>
