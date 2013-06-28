<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="commanderSelect" page="select">
<style type="text/css">
.secondary-menu {
    margin: -15px auto 15px auto;
    width: 98%;
    padding: 2px 5px;
}
</style>
    <cti:standardMenu menuSelection="${menuSelection}" />
    
    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        <cti:msg key="yukon.web.components.button.home.label" var="homeLabel"/>
        <cti:crumbLink url="/dashboard" title="${homeLabel}" />
        
        <%-- bulk home --%>
        <cti:crumbLink><i:inline key=".pageTitle"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <h2 class="page-heading"><i:inline key=".pageTitle"/></h2>
    <p>&nbsp;</p>

<c:set var="isDevicesPage" value='${category == "MCT" || category == "IED" || category == "RTU" || category == "TRANSMITTER"}'/>
<c:set var="isLoadMngtPage" value='${category == "LMGROUP"}'/>
<cti:linkTabbedContainer mode="section" id="page_header_tab_container">
    <cti:msg var="tab_name" key="yukon.web.menu.config.commanderSelect.devices" />
    <c:set var="on_tab" value='${isDevicesPage}'/>
    <cti:linkTab tabId="deviceTab" selectorName="${tab_name}" tabHref="javascript:void(0);" initiallySelected="${on_tab}"/>

    <cti:msg var="tab_name" key="yukon.web.menu.config.commanderSelect.lm" />
    <c:set var="on_tab" value='${isLoadMngtPage}'/>
    <cti:linkTab tabId="loadMgtTab" selectorName="${tab_name}" tabHref="javascript:void(0);" initiallySelected="${on_tab}"/>

    <c:url var="tab_url" value="/commander/select?category=CAP" />
    <cti:msg var="tab_name" key="yukon.web.menu.config.commanderSelect.capcontrol" />
    <cti:linkTab tabId="capControlTab" selectorName="${tab_name}" tabHref="${tab_url}" initiallySelected='${category == "CAP"}' />
</cti:linkTabbedContainer>

<%-- START Secondary Menu --%>
    <div id="menuL2Devices" class="secondary-menu<c:if test="${not isDevicesPage}"> dn</c:if>">
        <c:url var="tab_url" value="/commander/select" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commanderSelect.devices.mct" showPlainText='${category == "MCT"}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/select?category=IED" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commanderSelect.devices.ied" showPlainText='${category == "IED"}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/select?category=RTU" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commanderSelect.devices.rtu" showPlainText='${category == "RTU"}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/select?category=TRANSMITTER" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commanderSelect.devices.transmitter" showPlainText='${category == "TRANSMITTER"}' href="${tab_url}" />
    </div>

    <div id="menuL2LoadMgt" class="secondary-menu<c:if test="${not isLoadMngtPage}"> dn</c:if>">
        <c:url var="tab_url" value="/commander/select?category=LMGROUP" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commanderSelect.lm.group" showPlainText='${category == "LMGROUP"}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/command/xcom" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commanderSelect.lm.xcom.tab.title" showPlainText='${false}' href="${tab_url}" />
        |
        <c:url var="tab_url" value="/commander/command/vcom" />
        <tags:displayOrLink labelKey="yukon.web.menu.config.commanderSelect.lm.vcom.tab.title" showPlainText='${false}' href="${tab_url}" />
    </div>
<%-- END Secondary Menu --%>

    <script>
        jQuery(function() {
            jQuery('.f-show_all').click(function () {
                <c:forEach var="filter" items="${filters}">
                  jQuery('#${filter.name}').val('');
                </c:forEach>
                
                jQuery('#filterForm')[0].submit();
            });
            
            jQuery('#loadMgtTab').click(function(){
                jQuery('#menuL2Devices').hide();
                jQuery('#menuL2LoadMgt').show();
                jQuery('#deviceTab').removeClass('ui-tabs-active ui-state-active');
                jQuery('#capControlTab').removeClass('ui-tabs-active ui-state-active');
                jQuery('#loadMgtTab').addClass('ui-tabs-active ui-state-active');
            });
            jQuery('#deviceTab').click(function(){
                jQuery('#menuL2LoadMgt').hide();
                jQuery('#menuL2Devices').show();
                jQuery('#loadMgtTab').removeClass('ui-tabs-active ui-state-active');
                jQuery('#capControlTab').removeClass('ui-tabs-active ui-state-active');
                jQuery('#deviceTab').addClass('ui-tabs-active ui-state-active');
            });
        });
    </script>
    
    <cti:url var="baseUrl" value="/commander/select"/>
    
    <tags:boxContainer2 nameKey="deviceSearch">
        <form id="filterForm" action="${baseUrl}">
            <input type="hidden" name="startIndex" value="${deviceSearchResults.startIndex}"> 
            <input type="hidden" name="itemsPerPage" value="${deviceSearchResults.count}">
            <input type="hidden" name="orderBy" value="${orderBy}">
            <input type="hidden" name="category" value="${category}">

            <div class="column_8_8_8 tiles clearfix">
                <c:set var="count" value="1"/>
                <c:forEach var="filter" items="${filters}">
                    <c:if test="${count == 1}"><c:set var="clazz" value="one"/></c:if>
                    <c:if test="${count == 2}"><c:set var="clazz" value="two"/></c:if>
                    <c:if test="${count == 3}"><c:set var="clazz" value="three nogutter"/><c:set var="count" value="0"/></c:if>
                    <c:if test="${filter.searchField.visible}">
                        <div class="stacked column ${clazz}">
                            <label class="dib">
                                <i:inline key="${filter}"/>:
                                <input type="text" id="${filter.name}" name="${filter.name}" value="${filter.filterValue}" style="margin-left: 5px;">
                            </label> 
                        </div>
                    </c:if>
                    <c:set var="count" value="${count + 1}"/>
                </c:forEach>
            </div>

            <div class="actionArea">
                <cti:button nameKey="search" type="submit" />
                <cti:button nameKey="showAll" classes="f-show_all" />
            </div>
        </form>
    </tags:boxContainer2>
    
    <tags:pagedBox2 nameKey="deviceSearchResults" searchResult="${deviceSearchResults}" baseUrl="${baseUrl}" pageByHundereds="true">
        <c:if test="${deviceSearchResults.hitCount == 0}">
            <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
        </c:if>
        <c:if test="${deviceSearchResults.hitCount > 0}">
            <table class="compactResultsTable rowHighlighting">
                <thead>
	                <tr>
		                <c:forEach var="field" items="${fields}">
		                    <c:if test="${field.visible}">
		                      <th><tags:sortLink nameKey="deviceSearchField.${field}" baseUrl="${baseUrl}" fieldName="${field}" sortParam="orderBy"/></th>
		                    </c:if>
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
        </c:if>
    </tags:pagedBox2>
</cti:standardPage>