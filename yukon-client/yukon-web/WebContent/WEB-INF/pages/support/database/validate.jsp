<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support">
<cti:standardMenu menuSelection="database|compare"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	<cti:crumbLink url="/spring/support/" title="Support" />
    <cti:crumbLink>Database Validate</cti:crumbLink>
</cti:breadCrumbs>

<script>
Event.observe(window, 'load', function() {
	new Ajax.Updater('compareResults', '/spring/support/database/validate/results');
});
</script>

<pre>
<div id="compareResults">
<c:if test="${displayOracleWarning}">
Due to the way Oracle accesses its database information, the current validation of your database could take some time. (~5 Minutes)
</c:if>

loading... <img id="slowInputProcessImg${uniqueId}" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting"> 
</div>
</pre>
</cti:standardPage>
