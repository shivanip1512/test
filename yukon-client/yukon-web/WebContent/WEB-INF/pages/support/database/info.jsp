<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support">
    <cti:standardMenu menuSelection="database|info"/>


<h3 class="indentedElementHeading">DB Connection</h3>
<div>
JDBC URL: ${dbUrl}<br />
JDBC User: ${dbUser}<br />
</div>

</cti:standardPage>
