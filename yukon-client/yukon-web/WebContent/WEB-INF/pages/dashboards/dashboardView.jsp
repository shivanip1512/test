<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="dashboard" page="view">

    <cti:breadCrumbs>
        <cti:crumbLink url="/dashboard" title="Home" />
        <c:if test="${dashboardPageType == 'AMI'}">
            <cti:crumbLink url="/meter/start" title="AMI" />
        </c:if>
        <cti:crumbLink>${fn:escapeXml(dashboard.name)}</cti:crumbLink>
    </cti:breadCrumbs>

<tags:setFormEditMode mode="${mode}"/>
<cti:yukonUser var="user"/>
    <div id="page-actions" class="dn">
        <c:if test="${ownedDashboards.size() > 0}">
            <c:forEach var="ownedDashboard" items="${ownedDashboards}" varStatus="status">
                <c:if test="${ownedDashboard.dashboardId != dashboard.dashboardId && status.index <= 10}">
                    <cti:url var="dashboardUrl" value="/dashboards/${ownedDashboard.dashboardId}/view?dashboardPageType=${dashboardPageType}"/>
                    <cm:dropdownOption label="${ownedDashboard.name}" href="${dashboardUrl}"/>
                </c:if>
            </c:forEach>
            <li class="divider"/>
        </c:if>
        <c:if test="${user.userID == dashboard.owner.userID && dashboard.visibility != 'SYSTEM'}">
            <cti:url var="editUrl" value="/dashboards/${dashboard.dashboardId}/edit"/>
            <cm:dropdownOption key=".edit" href="${editUrl}"/>
        </c:if>
        <cti:url var="manageDashboardsUrl" value="/dashboards/manage"/>
        <cm:dropdownOption key=".manageDashboards" href="${manageDashboardsUrl}"/>
        <c:if test="${dashboardPageType == 'AMI'}">
            <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="CREATE">
                <li class="divider"/>
                <cm:dropdownOption key="yukon.web.modules.amr.create" classes="js-create-meter" data-popup-title="${popupTitle}"/>
            </cti:checkRolesAndProperties>
        </c:if>
    </div>

    <div class="column-12-12">
        <div class="column one" style="min-height:100px;">
            <c:forEach var="widget" items="${dashboard.column1Widgets}">
                <tags:widget id="${widget.id}" bean="${widget.type.beanName}" identify="true"
                    paramMap="${widget.parameters}" helpText="${widget.helpText}" displayUnauthorizedMessage="true"/>
            </c:forEach>
        </div>
        <div class="column two nogutter" style="min-height:100px;">
            <c:forEach var="widget" items="${dashboard.column2Widgets}">
                <tags:widget id="${widget.id}" bean="${widget.type.beanName}" identify="true"
                    paramMap="${widget.parameters}" helpText="${widget.helpText}" displayUnauthorizedMessage="true"/>
            </c:forEach>        
        </div>
    </div>
    
    <c:forEach var="widgetJsLink" items="${widgetJavascript}">
        <cti:includeScript link="${widgetJsLink}"/>
    </c:forEach>
    <c:forEach var="widgetCssLink" items="${widgetCss}">
        <cti:includeScript link="${widgetCssLink}"/>
    </c:forEach>
    
    <c:if test="${dashboardPageType == 'AMI'}">
        <cti:msg2 key="yukon.web.modules.amr.create" var="popupTitle"/>
        <div id="contentPopup" class="dn" data-create-title="${popupTitle}"></div>
        <cti:includeScript link="/resources/js/pages/yukon.ami.meter.details.js"/>
    </c:if>
    
</cti:standardPage>