<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="adminSetup" page="jobsscheduler.status">
    <cti:linkTabbedContainer>
        <cti:msg2 key=".jobsscheduler.all.contextualPageName" var="allName"/>
        <cti:msg2 key=".jobsscheduler.status.contextualPageName" var="statusName"/>
        <cti:msg2 key=".jobsscheduler.active.contextualPageName" var="activeName"/>
        <cti:linkTab selectorName="${allName}"><c:url value="all" /></cti:linkTab>
        <cti:linkTab selectorName="${activeName}" ><c:url value="active" /></cti:linkTab>
        <cti:linkTab selectorName="${statusName}"  initiallySelected="true"><c:url value="status" /></cti:linkTab>
    </cti:linkTabbedContainer>
    
<div data-url="statusJobs">
    <%@ include file="statusjobs.jsp" %>
</div>

</cti:standardPage>
