<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:url var="url" value="/macsscheduler/schedules/innerView"/>

<cti:standardPage module="tools" page="script">

<cti:includeScript link="/JavaScript/macsscheduledscripts.js" />

<div id="main">
    <jsp:include page="${url}"/>
</div>
    
</cti:standardPage>