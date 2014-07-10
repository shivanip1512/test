<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="commander.select">
<style type="text/css">
.secondary-menu {
    margin: -15px auto 15px auto;
    width: 98%;
    padding: 2px 5px;
}
</style>

<c:set var="isDevicesPage" value='${category == "MCT" || category == "IED" || category == "RTU" || category == "TRANSMITTER"}'/>
<c:set var="isCapCtrlPage" value='${category == "CAP"}'/>
<c:set var="isLoadMngtPage" value='${not (isDevicesPage or isCapCtrlPage)}'/>

<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:linkTab tabId="deviceTab" selectorKey="yukon.web.menu.config.commander.select.devices" 
                                   initiallySelected="${isDevicesPage}">
        <c:url value="/commander/select?category=MCT"/>
    </cti:linkTab>

    <cti:linkTab tabId="loadMgtTab" selectorKey="yukon.web.menu.config.commander.select.lm" 
                                    initiallySelected="${isLoadMngtPage}">
        <c:url value="/commander/select?category=LMGROUP"/>
    </cti:linkTab>

    <cti:linkTab tabId="capControlTab" selectorKey="yukon.web.menu.config.commander.select.capcontrol"
                                    initiallySelected="${isCapCtrlPage}">
        <c:url value="/commander/select?category=CAP"/>
    </cti:linkTab>
</cti:linkTabbedContainer>

<%-- START Secondary Menu --%>
<c:if test="${isDevicesPage}">
    <div id="menuL2Devices" class="secondary-menu">
        <c:url var="tab_url" value="/commander/select"/>
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.devices.mct" showPlainText='${category == "MCT"}' href="${tab_url}"/>
        |
        <c:url var="tab_url" value="/commander/select?category=IED"/>
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.devices.ied" showPlainText='${category == "IED"}' href="${tab_url}"/>
        |
        <c:url var="tab_url" value="/commander/select?category=RTU"/>
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.devices.rtu" showPlainText='${category == "RTU"}' href="${tab_url}"/>
        |
        <c:url var="tab_url" value="/commander/select?category=TRANSMITTER"/>
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.devices.transmitter" showPlainText='${category == "TRANSMITTER"}' href="${tab_url}"/>
    </div>
</c:if>
<c:if test="${isLoadMngtPage}">
    <div id="menuL2LoadMgt" class="secondary-menu">
        <c:url var="tab_url" value="/commander/select?category=LMGROUP"/>
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.lm.group" showPlainText='${category == "LMGROUP"}' href="${tab_url}"/>
        |
        <c:url var="tab_url" value="/commander/command/xcom"/>
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.lm.xcom.tab.title" showPlainText='${serialType == "xcom"}' href="${tab_url}"/>
        |
        <c:url var="tab_url" value="/commander/command/vcom"/>
        <tags:displayOrLink labelKey="yukon.web.menu.config.commander.select.lm.vcom.tab.title" showPlainText='${serialType == "vcom"}' href="${tab_url}"/>
    </div>
</c:if>
<%-- END Secondary Menu --%>

    <script>
        $(function() {
            $('.js-show-all').click(function () {
                <c:forEach var="filter" items="${filters}">
                  $('#${filter.name}').val('');
                </c:forEach>
                
                $('#filterForm')[0].submit();
            });
            
            $('#search-results-container').attr('data-url', 'select?' + $('#filterForm').serialize());
        });
    </script>
    
    <c:set var="baseUrl" value="/commander/select"/>
    
    <tags:sectionContainer2 nameKey="deviceSearch">
        <form id="filterForm" action="<cti:url value="${baseUrl}"/>">
            <input type="hidden" name="category" value="${category}">

            <div class="column-8-8-8 tiles clearfix">
                <c:set var="count" value="1"/>
                <c:forEach var="filter" items="${filters}">
                    <c:if test="${count == 1}"><c:set var="clazz" value="one"/></c:if>
                    <c:if test="${count == 2}"><c:set var="clazz" value="two"/></c:if>
                    <c:if test="${count == 3}"><c:set var="clazz" value="three nogutter"/><c:set var="count" value="0"/></c:if>
                    <c:if test="${filter.searchField.visible}">
                        <div class="stacked column ${clazz}">
                            <label class="dib">
                                <i:inline key="${filter}"/>:
                                <input type="text" id="${filter.name}" name="${filter.name}" value="${fn:escapeXml(filter.filterValue)}" style="margin-left: 5px;">
                            </label> 
                        </div>
                    </c:if>
                    <c:set var="count" value="${count + 1}"/>
                </c:forEach>
            </div>

            <div class="action-area">
                <cti:button nameKey="search" type="submit" classes="primary action"/>
                <cti:button nameKey="showAll" classes="js-show-all"/>
            </div>
        </form>
    </tags:sectionContainer2>
    
    <c:if test="${deviceSearchResults.hitCount == 0}">
        <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
    </c:if>
    <c:if test="${deviceSearchResults.hitCount > 0}">
        <div id="search-results-container" data-static>
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <c:forEach var="column" items="${columns}">
                            <tags:sort column="${column}"/>
                        </c:forEach>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="searchResultRow" items="${deviceSearchResults.resultList}">
                        <tr>
                            <c:forEach var="field" items="${fields}">
                                <c:if test="${field.visible}">
                                <c:set var="value" value="${fn:escapeXml(searchResultRow.map[field.fieldName])}"/>
                                <c:choose>
                                    <c:when test="${field eq 'NAME'}">
                                    <cti:url var="commandUrl" value="/commander/command">
                                        <cti:param name="deviceId" value="${searchResultRow.map['id']}"/>
                                        <cti:param name="category" value="${category}"/>
                                    </cti:url>
                                    <td><a href="${commandUrl}">${value}</a></td>
                                    </c:when>
                                    <c:when test="${field eq 'TYPE'}"><td>${value}</td></c:when>
                                    <c:otherwise><td><c:choose>
                                        <c:when test="${empty value}">-</c:when>
                                        <c:otherwise>${value}</c:otherwise>
                                    </c:choose></td></c:otherwise>
                                </c:choose>
                                </c:if>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <tags:pagingResultsControls result="${deviceSearchResults}" hundreds="true" adjustPageCount="true"/>
        </div>
    </c:if>
</cti:standardPage>