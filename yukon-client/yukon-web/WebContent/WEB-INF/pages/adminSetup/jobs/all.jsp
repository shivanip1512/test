<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="jobsscheduler.all">
    <cti:linkTabbedContainer>
        <cti:msg2 key=".jobsscheduler.all.contextualPageName" var="allName"/>
        <cti:msg2 key=".jobsscheduler.status.contextualPageName" var="statusName"/>
        <cti:msg2 key=".jobsscheduler.active.contextualPageName" var="activeName"/>
        <cti:linkTab selectorName="${allName}" initiallySelected="true"><c:url value="all" /></cti:linkTab>
        <cti:linkTab selectorName="${activeName}" ><c:url value="active" /></cti:linkTab>
        <cti:linkTab selectorName="${statusName}" ><c:url value="status" /></cti:linkTab>
    </cti:linkTabbedContainer>
    
<h4><i:inline key=".repeatingJobs"/></h2>
<div data-url="repeatingJobs">
    <%@ include file="repeatingjobs.jsp" %>
</div>

<h4><i:inline key=".oneTimeJobs"/></h2>
<div data-url="oneTimeJobs">
    <%@ include file="onetimejobs.jsp" %>
</div>
  <cti:includeScript link="/resources/js/widgets/yukon.widget.scripts.js"/>
 
</cti:standardPage>
