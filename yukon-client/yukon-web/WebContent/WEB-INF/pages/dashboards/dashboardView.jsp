<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:standardPage module="dashboard" page="view">

<tags:setFormEditMode mode="${mode}"/>

    <div id="page-actions" class="dn">
        <cti:url var="editUrl" value="/dashboards/${dashboard.id}/edit"/>
        <cm:dropdownOption key=".edit" href="${editUrl}"/>
        <cti:url var="manageDashboardsUrl" value="/dashboards/manage"/>
        <cm:dropdownOption key=".manageDashboards" href="${manageDashboardsUrl}"/>
    </div>

    <div class="column-12-12">
        <div class="column one" style="min-height:100px;">
            <c:forEach var="widget" items="${dashboard.column1Widgets}">
                <tags:widget bean="${widget.type.beanName}" paramMap="${widget.parameters}" />
            </c:forEach>
        </div>
        <div class="column two nogutter" style="min-height:100px;">
            <c:forEach var="widget" items="${dashboard.column2Widgets}">
                <tags:widget bean="${widget.type.beanName}" paramMap="${widget.parameters}"/>
            </c:forEach>        
        </div>
    </div>
    
</cti:standardPage>