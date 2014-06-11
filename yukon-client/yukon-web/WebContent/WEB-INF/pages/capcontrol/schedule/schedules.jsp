<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol"  page="schedules">

    <c:set var="baseUrl" value="/capcontrol/schedule/schedules"/>
    
    <cti:linkTabbedContainer mode="section">
        <cti:linkTab selectorKey="yukon.web.modules.capcontrol.schedules.tab.title"
                     initiallySelected='${true}'>
            <c:url value="/capcontrol/schedule/schedules" />
        </cti:linkTab>
        <cti:linkTab selectorKey="yukon.web.modules.capcontrol.scheduleAssignments.tab.title">
            <c:url value="/capcontrol/schedule/scheduleAssignments" />
        </cti:linkTab>
    </cti:linkTabbedContainer>
    
    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    <%@include file="/capcontrol/capcontrolHeader.jspf" %>
    
    <div id="schedules-table" data-reloadable>
        <%@include file="schedulesTable.jsp" %>
    </div>

</cti:standardPage>