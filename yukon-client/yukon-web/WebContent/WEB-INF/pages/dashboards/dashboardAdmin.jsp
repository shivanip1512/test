<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="dashboard" page="admin">

      <table class="compact-results-table row-highlighting has-actions">
        <th><i:inline key=".name"/></th>
        <th><i:inline key=".createdBy"/></th>
        <th><i:inline key=".visibility"/></th>
        <th><i:inline key=".numberOfUsers"/></th>
        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
        <c:forEach var="dashboard" items="${dashboards}">
            <tr>
                <c:set var="dashboardId" value="${dashboard.dashboardId}"/>
                <cti:url var="dashboardUrl" value="/dashboards/${dashboardId}/view"/>
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
                        <cm:dropdownOption key=".changeOwner" icon="icon-user-edit"/>            
                    </cm:dropdown>
                </td>
            </tr>   
        </c:forEach>
    </table>
    
</cti:standardPage>