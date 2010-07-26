<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<br>
<div id="${widgetParameters.widgetId}_results"></div>
<div style="text-align: right">
	<tags:widgetActionUpdate method="read" label="Read Now" labelBusy="Reading" container="${widgetParameters.widgetId}_results" hide="true"/>
</div>