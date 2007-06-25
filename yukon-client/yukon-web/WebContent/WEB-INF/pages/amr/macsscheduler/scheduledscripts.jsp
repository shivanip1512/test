<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="url" value="/spring/macsscheduler/schedules/view"/>

<cti:standardPage title="Scheduled Scripts"
    module="amr">
    <cti:standardMenu />

<script language="JavaScript">
    Event.observe(window, 'load', function() {
       new Ajax.PeriodicalUpdater('main', '${url}', {
           "method": 'post', "frequency": 5, "decay": 1
       });
    });
</script>

<div id="main" style="margin-left: 5%; margin-right: 5%;">
    <jsp:include page="${url}"/>
</div>
    
</cti:standardPage>