<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Commander Results" module="amr">
<cti:standardMenu menuSelection="devicegroups|commander"/>
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/dashboard" title="Home" />
   	    <cti:crumbLink url="/group/commander/groupProcessing" title="Group Processing" />
	    &gt; Command Executing
	</cti:breadCrumbs>

	Your group request has been started:<br/><br/>
	Executing <b>${command}</b> on <b>${fn:escapeXml(group)}</b> group.<br/><br/>
	When processing completes, results will be sent to:<br/>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>${emailAddresses}</b>
	
</cti:standardPage>