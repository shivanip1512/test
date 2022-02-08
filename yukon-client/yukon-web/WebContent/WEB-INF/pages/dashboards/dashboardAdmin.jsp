<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="config.dashboards">

<cti:msgScope paths="modules.dashboard">

    <div class="clearfix box">
        <div class="category fl">
            <cti:url var="viewUrl" value="/dashboards/admin"/>
            <cti:button renderMode="appButton" icon="icon-app icon-app-32-home" href="${viewUrl}"/>
            <div class="box fl meta">
                <div><a class="title" href="${viewUrl}"><i:inline key="yukon.common.setting.subcategory.DASHBOARD_ADMIN"/></a></div>
                <div class="detail"><i:inline key="yukon.common.setting.subcategory.DASHBOARD_ADMIN.description"/></div>
            </div>
        </div>
    </div>


<div class="page-actions fr stacked-md">
    <cti:button icon="icon-plus-green" nameKey="createDashboard" data-popup=".js-create-dashboard-popup"/>
</div>
<br/><br/>

    <tags:sectionContainer2 nameKey="dashboardsSection">

    <cti:url var="dataUrl" value="/dashboards/admin"/>
    <div data-url="${dataUrl}" data-static>
      <table class="compact-results-table row-highlighting has-actions">
        <tags:sort column="${name}" />                
        <tags:sort column="${owner}" />                
        <tags:sort column="${visibility}" />                
        <tags:sort column="${numberOfUsers}" />    
        <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
        <c:forEach var="dashboard" items="${dashboards.resultList}">
            <tr>
                <c:set var="dashboardId" value="${dashboard.dashboardId}"/>
                <cti:url var="dashboardUrl" value="/dashboards/${dashboardId}/view"/>
                <td><a href="${dashboardUrl}">${fn:escapeXml(dashboard.name)}</a></td>
                <td>
                    <c:choose>
                        <c:when test="${!empty dashboard.owner.username}">
                            ${dashboard.owner.username}
                        </c:when>
                        <c:otherwise>
                            <cti:icon icon="icon-error" classes="js-show-user-picker cp" data-dashboard-id="${dashboardId}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td><i:inline key=".visibility.${dashboard.visibility}"/></td>
                <td>${dashboard.users}</td>
                <td>
                    <cm:dropdown icon="icon-cog">
                        <c:choose>
                            <c:when test="${dashboard.visibility != 'SYSTEM'}">
                                <cm:dropdownOption id="deleteDashboard_${dashboardId}" key=".delete" icon="icon-delete"
                                    data-dashboard-id="${dashboardId}" data-ok-event="yukon:dashboard:remove" />
                                <d:confirm on="#deleteDashboard_${dashboardId}" nameKey="confirmDelete" argument="${dashboard.name}"/>
                                <input type="hidden" id="selectedPickerValues${dashboardId}" class="js-user-id">
                                <li class="dropdown-option">
                                    <tags:pickerDialog type="userPicker" id="userPicker_${dashboardId}" destinationFieldId="selectedPickerValues${dashboardId}"
                                        endEvent="yukon:dashboard:changeOwner">
                                        <cti:icon icon="icon-user-edit"/>                                
                                        <cti:msg2 key=".changeOwner" />
                                    </tags:pickerDialog>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <cm:dropdownOption key=".delete" icon="icon-delete" disabled="true" />
                                <cm:dropdownOption key=".changeOwner" icon="icon-user-edit" disabled="true" />
                            </c:otherwise>
                        </c:choose>
                        <li class="dropdown-option">
                            <cti:uniqueIdentifier var="id" />
                            <tags:pickerDialog type="dashboardUsersPicker" id="dashboardUsers_${id}" extraArgs="${dashboardId}" allowEmptySelection="true">
                                <cti:icon icon="icon-group"/>                                
                                <cti:msg2 key=".viewUsers" />
                            </tags:pickerDialog>
                        </li>
                        <c:choose>
                            <c:when test="${dashboard.visibility != 'PRIVATE'}">
                                <div class="dn js-assign-users-${dashboardId}" data-dialog data-title="<cti:msg2 key=".assignUsers.dialogTitle"/>" 
                                    data-width="700" data-height="600" data-event="yukon:dashboard:assignUsers" 
                                    data-ok-text="<cti:msg2 key=".assignButton"/>"
                                    data-url="<cti:url value="/dashboards/${dashboardId}/assignUsers"/>"></div>
                                <cm:dropdownOption key=".assignUsers" icon="icon-group-add" data-popup=".js-assign-users-${dashboardId}"/>
                                <li class="dropdown-option">
                                    <cti:uniqueIdentifier var="id" />
                                    <tags:pickerDialog type="dashboardUsersPicker" id="unassignDashboardUsers_${id}" extraArgs="${dashboardId}" multiSelectMode="true"
                                        endEvent="yukon:dashboard:unassignUsers" okButtonKey=".unassignButton">
                                        <cti:icon icon="icon-group-delete"/>                                
                                        <cti:msg2 key=".unassignUsers" />
                                    </tags:pickerDialog>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <cm:dropdownOption key=".assignUsers" icon="icon-group-add" disabled="true"/>
                                <cm:dropdownOption key=".unassignUsers" icon="icon-group-delete" disabled="true"/>
                            </c:otherwise>
                        </c:choose>
                    </cm:dropdown>
                </td>
            </tr>   
        </c:forEach>
    </table>
    <tags:pagingResultsControls result="${dashboards}" adjustPageCount="true"/>
    </div>
    </tags:sectionContainer2>
    
    <%-- CREATE DASHBOARD POPUP --%>
<div class="dn js-create-dashboard-popup js-dashboard-details-popup" data-dialog
    data-title="<cti:msg2 key=".createDashboard.label"/>"
    data-ok-text="<cti:msg2 key="yukon.common.create"/>"
    data-event="yukon:dashboard:details:save" 
    data-url="<cti:url value="/dashboards/create"/>"></div>
    
    <cti:includeScript link="/resources/js/pages/yukon.dashboards.js"/>
    
    </cti:msgScope>
    
</cti:standardPage>