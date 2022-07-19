<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="signalTransmitters.list">

    <!-- Actions dropdown -->
    <div id="page-actions" class="dn">
        <cti:url var="createUrl" value="/stars/device/signalTransmitter/create" />
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" 
                           id="js-create-option" href="${createUrl}"/>
    </div>

    <hr/>
    <div class="filter-section">
            <form action="<cti:url var="url" value="/device/signalTransmitter/list"/>" method="get">
                <i:inline key="yukon.common.filterBy"/>
                <input type="hidden" name="itemsPerPage" value="${paging.itemsPerPage}">
                <cti:msg2 var="namePlaceholder" key=".name"/>
                <input type="text" name="name" size="20" value="${fn:escapeXml(name)}" placeholder="${namePlaceholder}">
                <cti:button nameKey="filter" type="submit" classes="action primary fn vab"/>
            </form>
            <hr/>
    </div>
    
    <cti:url var="listUrl" value="/stars/device/signalTransmitter/list"/>
    <div data-url="${listUrl}" data-static>
        <table class="compact-results-table row-highlighting">
            <thead>
                <tr>
                    <tags:sort column="${name}"/>
                    <tags:sort column="${type}"/>
                    <tags:sort column="${status}"/>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="signalTransmitter" items="${signalTransmitters.items}">
                    <c:set var="cssClass" value="error" />
                    <cti:msg2 var="status" key="yukon.common.disabled"/>
                    <c:if test="${signalTransmitter.enabled}">
                        <c:set var="cssClass" value="success" />
                        <cti:msg2 var="status" key="yukon.common.enabled"/>
                    </c:if>
                    <tr>
                        <td>
                            <cti:url value="/stars/device/signalTransmitter/${signalTransmitter.id}" var="viewUrl"/>
                            <a href="${viewUrl}">${fn:escapeXml(signalTransmitter.name)}</a>
                        </td>
                        <td><i:inline key="${signalTransmitter.type}"/></td>
                        <td class="${cssClass}">${status}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:paginatedResponseControls response="${signalTransmitters}" adjustPageCount="true" thousands="true"/>
    </div>

</cti:standardPage>