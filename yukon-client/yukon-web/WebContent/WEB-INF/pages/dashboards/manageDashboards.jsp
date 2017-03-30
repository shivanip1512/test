<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="dashboard" page="manage">

<div class="page-actions fr stacked-md"><cti:button icon="icon-plus-green" nameKey="createDashboard" data-popup=".js-create-dashboard-popup"/></div>
<br/><br/>
<cti:tabs>
<cti:tab title="Dashboards" selected="true">

    <hr/>
    <span class="fl"><i:inline key=".filters"/></span><div class="button-group"> <cti:button nameKey="myFavorites"/><cti:button nameKey="createdByMe"/></div>
    <hr/>
    
    <table class="compact-results-table row-highlighting has-actions has-alerts">
        <th></th>
        <th><i:inline key=".name"/></th>
        <th><i:inline key=".createdBy"/></th>
        <th><i:inline key=".visibility"/></th>
        <th><i:inline key=".numberOfUsers"/></th>
        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
        <c:forEach var="dashboard" items="${dashboards}">
            <tr>
                <c:set var="dashboardId" value="${dashboard.dashboardId}"/>
                <cti:url var="dashboardUrl" value="/dashboards/${dashboardId}/view"/>
                <td><cti:icon icon="icon-star"/></td>                
                <td><a href="${dashboardUrl}">${dashboard.name}</a></td>
                <td>${dashboard.owner.username}</td>
                <td><i:inline key=".visibility.${dashboard.visibility}"/></td>
                <td>${dashboard.users}</td>
                <td>
                    <cm:dropdown icon="icon-cog">
                        <div class="dn copy-dashboard-${dashboardId}" data-dialog data-title="<cti:msg2 key=".copyDashboard.label"/>"
                        data-url="<cti:url value="/dashboards/${dashboardId}/copy"/>"></div>
                        <cm:dropdownOption key=".copy" icon="icon-disk-multiple" data-popup=".copy-dashboard-${dashboardId}"/>
                        <cti:url var="editUrl" value="/dashboards/${dashboardId}/edit"/>
                        <cm:dropdownOption key=".edit" icon="icon-pencil" href="${editUrl}"/>
                        <cti:url var="deleteUrl" value="/dashboards/${dashboardId}/delete"/>
                        <cm:dropdownOption key=".delete" icon="icon-cross" href="${deleteUrl}"/>                
                    </cm:dropdown>
                
                </td>
            </tr>   
        </c:forEach>
    </table>
</cti:tab>
<cti:tab title="Settings">

    <tags:nameValueContainer style="width:60%">
        <tags:nameValue name="Home Dashboard">
            <select>
                <option>Yukon Default Dashboard</option>
                <option>Utility Company Sample Dashboard</option>
                <option>User Specific Dashboard</option>
            </select>
            
        </tags:nameValue>
                <tags:nameValue name="AMI Dashboard">
            <select>
                <option>Yukon Default Dashboard</option>
                <option>Utility Company Sample Dashboard</option>
                <option>User Specific Dashboard</option>
            </select>
            
        </tags:nameValue>
                <tags:nameValue name="Demand Response Dashboard">
            <select>
                <option>Yukon Default Dashboard</option>
                <option>Utility Company Sample Dashboard</option>
                <option>User Specific Dashboard</option>
            </select>
            
        </tags:nameValue>
                <tags:nameValue name="Volt/Var Dashboard">
            <select>
                <option>Yukon Default Dashboard</option>
                <option>Utility Company Sample Dashboard</option>
                <option>User Specific Dashboard</option>
            </select>
            
        </tags:nameValue>
    </tags:nameValueContainer>
    
        <div class="page-action-area">
    <cti:button nameKey="cancel"/><cti:button classes="primary" nameKey="save"/>
    </div>
</cti:tab>

</cti:tabs>

<%-- CREATE DASHBOARD POPUP --%>
<div class="dn js-create-dashboard-popup" data-dialog
    data-title="<cti:msg2 key=".createDashboard.label"/>"
    data-ok-text="<cti:msg2 key="yukon.common.create"/>"
    data-url="<cti:url value="/dashboards/create"/>"></div>
  
</cti:standardPage>