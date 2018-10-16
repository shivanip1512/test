<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="dashboard" page="manage">

<tags:setFormEditMode mode="${mode}"/>
<cti:yukonUser var="user"/>

<div class="page-actions fr stacked-md">
    <cti:button icon="icon-plus-green" nameKey="createDashboard" data-popup=".js-create-dashboard-popup"/>
</div>
<br/><br/>

<cti:tabs>
    <cti:msg2 var="dashboardsTab" key=".dashboardsTab"/>
    <cti:tab title="${dashboardsTab}" selected="true">
    
        <hr/>
        <span class="fl"><i:inline key=".filters"/>&nbsp;&nbsp;</span>
            <cti:url var="manageUrl" value="/dashboards/manage"/>
            <select onchange="window.location.href=this.value">
                <option value="${manageUrl}"/><cti:msg2 key=".showAll"/></option>
                <option value="${manageUrl}?filter=OWNEDBYME" <c:if test="${filter == 'OWNEDBYME'}">selected="selected"</c:if>><cti:msg2 key=".ownedByMe"/></option>
            </select>
        <hr/>
        
        <cti:url var="dataUrl" value="/dashboards/manage">
            <cti:param name="filter" value="${filter}"/>
        </cti:url>
        <div data-url="${dataUrl}" data-static>
            <table class="compact-results-table row-highlighting has-actions">
                <tags:sort column="${name}" />                
                <tags:sort column="${owner}" />                
                <tags:sort column="${visibility}" />                
                <tags:sort column="${numberOfUsers}" />                
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                <c:choose>
                    <c:when test="${dashboards.hitCount > 0}">
                        <c:forEach var="dashboard" items="${dashboards.resultList}">
                            <tr>
                                <c:set var="dashboardId" value="${dashboard.dashboardId}"/>
                                <cti:url var="dashboardUrl" value="/dashboards/${dashboardId}/view"/>            
                                <td><a href="${dashboardUrl}">${fn:escapeXml(dashboard.name)}</a></td>
                                <td>${dashboard.owner.username}</td>
                                <td><i:inline key=".visibility.${dashboard.visibility}"/></td>
                                <td>${dashboard.users}</td>
                                <td>
                                    <cm:dropdown icon="icon-cog">
                                        <div class="dn copy-dashboard-${dashboardId} js-dashboard-details-popup" data-dialog data-title="<cti:msg2 key=".copyDashboard.label"/>"
                                        data-url="<cti:url value="/dashboards/${dashboardId}/copy"/>" data-event="yukon:dashboard:details:save"></div>
                                        <cm:dropdownOption key=".copy" icon="icon-disk-multiple" data-popup=".copy-dashboard-${dashboardId}"/>
                                        <c:choose>
                                            <c:when test="${user.userID == dashboard.owner.userID && dashboard.visibility != 'SYSTEM'}">
                                                <cti:url var="editUrl" value="/dashboards/${dashboardId}/edit"/>
                                                <cm:dropdownOption key=".edit" icon="icon-pencil" href="${editUrl}" />
                                                <cm:dropdownOption id="deleteDashboard_${dashboardId}" key=".delete" icon="icon-cross"
                                                    data-dashboard-id="${dashboardId}" data-ok-event="yukon:dashboard:remove" />
                                                <d:confirm on="#deleteDashboard_${dashboardId}" nameKey="confirmDelete" argument="${dashboard.name}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <cm:dropdownOption key=".edit" icon="icon-pencil" disabled="true" />
                                                <cm:dropdownOption key=".delete" icon="icon-cross" disabled="true" />
                                            </c:otherwise>
                                        </c:choose>
                                    </cm:dropdown>
                                </td>
                            </tr>   
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="5">
                                <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound" /></span>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>
            <tags:pagingResultsControls result="${dashboards}" adjustPageCount="true"/>
        </div>
    </cti:tab>
    <cti:msg2 var="settingsTab" key=".settingsTab"/>
    <cti:tab title="${settingsTab}">
        <cti:url var="action" value="/dashboards/saveSettings"/>
        <form:form modelAttribute="dashboardSettings" action="${action}" method="POST">
            <cti:csrfToken/>
            <tags:nameValueContainer2>
                <c:forEach var="setting" items="${dashboardSettings.settings}" varStatus="status">
                    <c:set var="index" value="${status.index}"/>
                    <form:hidden path="settings[${index}].pageType"/>
                    <tags:nameValue2 nameKey=".pageType.${setting.pageType}">
                        <tags:selectWithItems items="${dashboardsList}" path="settings[${index}].dashboardId" itemValue="dashboardId" itemLabel="name"/>
                    </tags:nameValue2>
                </c:forEach>
            </tags:nameValueContainer2>
                
            <div class="page-action-area">
                <cti:button type="submit" classes="primary" nameKey="save"/>
                <cti:url var="cancelUrl" value="/dashboards/manage"/>
                <cti:button nameKey="cancel" href="${cancelUrl}"/>
            </div>
        
        </form:form>
    </cti:tab>

</cti:tabs>

<%-- CREATE DASHBOARD POPUP --%>
<div class="dn js-create-dashboard-popup js-dashboard-details-popup" data-dialog
    data-title="<cti:msg2 key=".createDashboard.label"/>"
    data-ok-text="<cti:msg2 key="yukon.common.create"/>"
    data-event="yukon:dashboard:details:save" 
    data-url="<cti:url value="/dashboards/create"/>"></div>
    
    <cti:includeScript link="/resources/js/pages/yukon.dashboards.js"/>
    
  
</cti:standardPage>