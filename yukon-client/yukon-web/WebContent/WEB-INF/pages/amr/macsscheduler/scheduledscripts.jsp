<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="url" value="/spring/macsscheduler/schedules/innerView"/>

<cti:standardPage title="Scheduled Scripts"
    module="amr">
	<cti:standardMenu menuSelection="scheduler"/>
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    &gt; Scheduler
	</cti:breadCrumbs>

    <cti:includeScript link="/JavaScript/macsscheduledscripts.js" />

<script language="JavaScript">
    Event.observe(window, 'load', function() {
       new Ajax.PeriodicalUpdater('main', '${url}?sortBy=${sortBy}&descending=${descending}', {
           "method": 'post', "frequency": 5, "decay": 1
       });
    });
</script>

<div id="main" style="margin-left: 5%; margin-right: 5%;">
    <jsp:include page="${url}"/>
</div>
    
</cti:standardPage>