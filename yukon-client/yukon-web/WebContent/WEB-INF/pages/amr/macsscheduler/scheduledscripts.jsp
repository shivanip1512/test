<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:url var="url" value="/spring/macsscheduler/schedules/innerView"/>
<cti:msg2 key="yukon.web.modules.amr.macsscheduler.pageName" var="pageName"/>

<cti:standardPage title="${pageName}" module="amr">
	<cti:standardMenu menuSelection="scheduler"/>
	<cti:breadCrumbs>
	    <cti:msg key="yukon.web.components.button.home.label" var="homeLabel"/>
        <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
	    &gt; <cti:msg2 key="yukon.web.menu.config.amr.scheduler"/>
	</cti:breadCrumbs>

    <cti:includeScript link="/JavaScript/macsscheduledscripts.js" />

<script language="JavaScript">
    jQuery(function() {
       new Ajax.PeriodicalUpdater('main', '${url}?sortBy=${sortBy}&descending=${descending}', {
           "method": 'post', "frequency": 5, "decay": 1
       });
    });
</script>

<div id="main">
    <jsp:include page="${url}"/>
</div>
    
</cti:standardPage>