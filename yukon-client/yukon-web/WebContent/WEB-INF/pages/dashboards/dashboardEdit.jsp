<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dashboard" page="edit">

<tags:setFormEditMode mode="${mode}"/>

    <div class="page-actions fr">
        <cti:button icon="icon-plus-green" nameKey="addWidgets" data-popup=".js-add-widgets-popup"/>
        <cti:button icon="icon-pencil" nameKey="editDetails" data-popup=".js-dashboard-details-popup"/>
    </div>
    <br/><br/>
    
    <cti:url var="action" value="/dashboards/save"/>
    <form:form modelAttribute="dashboard" action="${action}" method="POST">
        <cti:csrfToken/>

        <form:hidden path="dashboardId"/>
        <form:hidden path="name"/>
        <form:hidden path="description"/>
        <form:hidden path="visibility"/>

        <div class="column-12-12">
            <div id="column1-widgets" class="column one js-with-movables" data-item-selector=".select-box-item" style="min-height:300px;">
                <c:forEach var="widget" items="${dashboard.column1Widgets}" varStatus="status">
                    <c:set var="index" value="${status.index}"/>
                    <div class="select-box-item">
                        <tags:widgetEdit widget="${widget}" path="column1Widgets[${index}]"/>
                    </div>
                </c:forEach>
            </div>
            <div id="column2-widgets" class="column two nogutter js-with-movables" data-item-selector=".select-box-item" style="min-height:300px;">        
                <c:forEach var="widget" items="${dashboard.column2Widgets}" varStatus="status">
                    <c:set var="index" value="${status.index}"/>
                    <div class="select-box-item">
                        <tags:widgetEdit widget="${widget}" path="column2Widgets[${index}]"/>
                    </div>
                </c:forEach>
            </div>
        </div>
        
        <div class="page-action-area">
            <cti:button nameKey="save" type="submit" classes="primary action"/>
            <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
        </div>
    
    </form:form>
    
    <%-- ADD WIDGETS POPUP --%>
<div class="dn js-add-widgets-popup" data-dialog data-cancel-omit="true" data-height="500" data-width="900"
    data-title="<cti:msg2 key=".addWidgets.label"/>"
    data-url="<cti:url value="/dashboards/${dashboard.dashboardId}/addWidgets"/>"></div>
        
    <%-- EDIT DETAILS POPUP --%>
<div class="dn js-dashboard-details-popup" data-dialog
    data-title="<cti:msg2 key=".editDetails.label"/>"
    data-event="yukon:dashboard:details:save" 
    data-url="<cti:url value="/dashboards/${dashboard.dashboardId}/editDetails"/>"></div>
    
    <cti:includeScript link="/resources/js/pages/yukon.dashboards.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dashboards.edit.js"/>
    
</cti:standardPage>