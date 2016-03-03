<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>


<cti:standardPage module="capcontrol" page="search.${pageName}">
<cti:linkTabbedContainer mode="section">
    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedSubs.tab.title"
                 initiallySelected='${pageName == "orphanedSubs"}'>
        <cti:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__"/>
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedBuses.tab.title"
                 initiallySelected='${pageName == "orphanedBuses"}'>
        <cti:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubBuses__"/>
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedFeeders.tab.title"
                 initiallySelected='${pageName == "orphanedFeeders"}'>
        <cti:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oFeeders__"/>
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedBanks.tab.title"
                 initiallySelected='${pageName == "orphanedBanks"}'>
        <cti:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oBanks__"/>
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedCbcs.tab.title"
                 initiallySelected='${pageName == "orphanedCbcs"}'>
        <cti:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oCBCs__"/>
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedRegulators.tab.title"
                 initiallySelected='${pageName == "orphanedRegulators"}'>
        <cti:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oRegulators__"/>
    </cti:linkTab>
</cti:linkTabbedContainer>

    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    
    <c:choose>
    
        <c:when test="${fn:length(results) == 0}">
            <span class="empty-list"><i:inline key="yukon.web.modules.dr.searchResults.noResults"/></span>
        </c:when>
    
        <c:otherwise>
        
            <table id="resTable" class="compact-results-table">
                <thead>
                    <tr>
                        <th><i:inline key=".name"/></th>
                        <th><i:inline key=".itemType"/></th>
                        <th><i:inline key=".description"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach items="${results}" var="row">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${searchType == 'REGULATOR'}">
                                        <cti:url var="viewUrl" value="/capcontrol/regulators/${row.itemId}" />
                                        <a href="${viewUrl}">${fn:escapeXml(row.name)}</a>
                                    </c:when>
                                    <c:when test="${searchType == 'CBC'}">
                                        <cti:url var="editUrl" value="/capcontrol/cbc/${row.itemId}" />
                                        <a href="${editUrl}">${fn:escapeXml(row.name)}</a>
                                    </c:when>
                                    <c:when test="${searchType == 'FEEDER'}">
                                        <cti:url var="editUrl" value="/capcontrol/feeders/${row.itemId}" />
                                        <a href="${editUrl}">${fn:escapeXml(row.name)}</a>
                                    </c:when>
                                    <c:when test="${searchType == 'BUS'}">
                                        <cti:url var="editUrl" value="/capcontrol/buses/${row.itemId}" />
                                        <a href="${editUrl}">${fn:escapeXml(row.name)}</a>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:url var="resultUrl" value="/editor/cbcBase.jsf">
                                            <cti:param name="type" value="2"/>
                                            <cti:param name="itemid" value="${row.itemId}"/>
                                        </cti:url>
                                        <a href="${resultUrl}">${fn:escapeXml(row.name)}</a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${row.itemType}</td>
                            <td>${fn:escapeXml(row.itemDescription)}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            
        </c:otherwise>
    </c:choose>
    
</cti:standardPage>