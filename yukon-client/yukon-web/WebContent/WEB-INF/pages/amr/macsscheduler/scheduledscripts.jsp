<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="url" value="/spring/macsscheduler/schedules/innerView"/>

<cti:standardPage title="Scheduled Scripts"
    module="amr">
	<cti:standardMenu menuSelection="scheduler"/>
    <cti:includeScript link="/JavaScript/macsscheduledscripts.js" />

<script language="JavaScript">
    Event.observe(window, 'load', function() {
        macsscheduledscripts_updater('${url}','${sortBy}','${descending}');
    });
</script>

<div id="main" style="margin-left: 5%; margin-right: 5%;">
    <jsp:include page="${url}">
        <jsp:param name="sortBy" value="${sortBy}"/>
        <jsp:param name="descending" value="${descending}"/>
    </jsp:include>
</div>
    
</cti:standardPage>