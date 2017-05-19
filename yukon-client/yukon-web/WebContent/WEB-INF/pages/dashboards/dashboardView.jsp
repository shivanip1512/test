<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:standardPage module="dashboard" page="view">

<tags:setFormEditMode mode="${mode}"/>
<cti:yukonUser var="user"/>

    <div id="page-actions" class="dn">
        <c:forEach var="ownedDashboard" items="${ownedDashboards}" varStatus="status">
            <c:if test="${ownedDashboard.dashboardId != dashboard.dashboardId && status.index <= 10}">
                <cti:url var="dashboardUrl" value="/dashboards/${ownedDashboard.dashboardId}/view"/>
                <cm:dropdownOption label="${ownedDashboard.name}" href="${dashboardUrl}"/>
            </c:if>
        </c:forEach>
        <li class="divider"/>
        <c:if test="${user.userID == dashboard.owner.userID}">
            <cti:url var="editUrl" value="/dashboards/${dashboard.dashboardId}/edit"/>
            <cm:dropdownOption key=".edit" href="${editUrl}"/>
        </c:if>
        <cti:url var="manageDashboardsUrl" value="/dashboards/manage"/>
        <cm:dropdownOption key=".manageDashboards" href="${manageDashboardsUrl}"/>
    </div>

    <div class="column-12-12">
        <div class="column one" style="min-height:100px;">
            <c:forEach var="widget" items="${dashboard.column1Widgets}">
                <cti:msg2 key="yukon.web.widgets.${widget.type.beanName}.helpText" var="helpText" blankIfMissing="true"/>
                <tags:widget bean="${widget.type.beanName}" paramMap="${widget.parameters}" helpText="${helpText}"/>
            </c:forEach>
        </div>
        <div class="column two nogutter" style="min-height:100px;">
            <c:forEach var="widget" items="${dashboard.column2Widgets}">
                <cti:msg2 key="yukon.web.widgets.${widget.type.beanName}.helpText" var="helpText" blankIfMissing="true"/>
                <tags:widget bean="${widget.type.beanName}" paramMap="${widget.parameters}" helpText="${helpText}"/>
            </c:forEach>        
        </div>
    </div>
    
    <c:forEach var="widgetJsLink" items="${widgetJavascript}">
        <cti:includeScript link="${widgetJsLink}"/>
    </c:forEach>
    <c:forEach var="widgetCssLink" items="${widgetCss}">
        <cti:includeScript link="${widgetCssLink}"/>
    </c:forEach>
    
</cti:standardPage>