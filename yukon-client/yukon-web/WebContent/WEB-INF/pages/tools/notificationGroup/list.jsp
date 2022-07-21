<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="notificationGroup.list">

    <!-- Actions dropdown -->
    <div id="page-actions" class="dn">
        <cti:url var="createUrl" value="/tools/notificationGroup/create" />
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                           id="js-create-option" href="${createUrl}"/>
    </div>

    <hr/>
    <div class="filter-section">
        <cti:url var="url" value="/tools/notificationGroup/list"/>
        <form action="${url}" method="get">
            <i:inline key="yukon.common.filterBy"/>
            <cti:msg2 var="namePlaceholder" key="yukon.common.name"/>
            <input type="text" name="name" size="20" value="${fn:escapeXml(name)}" placeholder="${namePlaceholder}">
            
           <%--  <tags:input path="name" placeholder="${namePlaceholder}" inputClass="vat MR5" id="js-name" autocomplete="nofill"/> --%>
            <cti:button nameKey="filter" type="submit" classes="action primary fn vab"/>
        </form>
    </div>
    <hr/>
    
    <cti:url var="listUrl" value="/tools/notificationGroup/list"/>
    <div data-url="${listUrl}" data-static>
        <table class="compact-results-table row-highlighting">
            <thead>
                <tr>
                    <tags:sort column="${name}"/>
                    <tags:sort column="${status}"/>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="notificationGroup" items="${notificationGroups.items}">
                    <c:set var="cssClass" value="error" />
                    <cti:msg2 var="status" key="yukon.common.disabled"/>
                    <c:if test="${notificationGroup.enabled}">
                        <c:set var="cssClass" value="success" />
                        <cti:msg2 var="status" key="yukon.common.enabled"/>
                    </c:if>
                    <tr>
                        <td>
                            <cti:url value="/tools/notificationGroup/${notificationGroup.id}" var="viewUrl"/>
                            <a href="${viewUrl}">${fn:escapeXml(notificationGroup.name)}</a>
                        </td>
                        <td class="${cssClass}">${status}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <c:if test="${empty notificationGroups.items}">
            <span class="empty-list compact-results-table"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:if>
        <tags:paginatedResponseControls response="${notificationGroups}" adjustPageCount="true" thousands="true"/>
    </div>

</cti:standardPage>