<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.amr.meterProgramming.summary,modules.amr.meterProgramming">
    
    <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
    <span class="badge">${searchResults.hitCount}</span>&nbsp;<i:inline key="yukon.common.devices"/>
    
    <c:if test="${searchResults.hitCount > 0}">
        <span class="js-cog-menu">
            <cm:dropdown icon="icon-cog">
                <cti:url var="collectionActionsUrl" value="/bulk/collectionActions">
                    <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
                        <cti:param name="${cp.key}" value="${cp.value}"/>
                    </c:forEach>
                </cti:url>
                <cm:dropdownOption key="yukon.common.collectionActions" href="${collectionActionsUrl}" icon="icon-cog-go" newTab="true"/> 
                <cm:dropdownOption icon="icon-csv" key="yukon.common.download" classes="js-download"/>  
                <cti:url var="mapUrl" value="/tools/map">
                    <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                </cti:url>
                <cm:dropdownOption icon="icon-map-sat" key="yukon.common.mapDevices" href="${mapUrl}" newTab="true"/>
            </cm:dropdown>
        </span>
    </c:if>
    
    <table id="summary-table" class="compact-results-table row-highlighting has-actions">
        <thead>
            <tr>
               <tags:sort column="${DEVICE_NAME}"/>
               <tags:sort column="${METER_NUMBER}"/>
               <tags:sort column="${DEVICE_TYPE}"/>
               <tags:sort column="${PROGRAM}"/>
               <tags:sort column="${STATUS}"/>
               <tags:sort column="${LAST_UPDATE}"/>
               <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:choose>
                <c:when test="${searchResults.hitCount > 0}">
                    <c:forEach var="result" items="${searchResults.resultList}">
                        <tr data-device-id="${result.device.id}">                        
                            <%@ include file="summaryResultRow.jsp" %>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="6"><span class="empty-list"><i:inline key="yukon.common.search.noResultsFound" /></span></td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
    <tags:pagingResultsControls result="${searchResults}" adjustPageCount="true" hundreds="true"/>

</cti:msgScope>
