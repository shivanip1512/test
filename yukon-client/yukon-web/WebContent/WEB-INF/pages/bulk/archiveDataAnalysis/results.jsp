<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="bulk.analysis.results">

    <div class="notes stacked">
        <tags:selectedDevices id="deviceColletion" deviceCollection="${deviceCollection}" />
    </div>
    
    <div class="column-12-12 clearfix">
    
        <%-- Analysis Info --%>
        <div class="column one">
            <tags:sectionContainer2 nameKey="analysisInfo">
                
                <tags:nameValueContainer2>
                    
                    <tags:nameValue2 nameKey=".attribute">
                        <cti:msg2 key="${result.analysis.attribute}"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".dateRange">
                        <cti:formatInterval type="DATEHM" value="${result.analysis.dateTimeRange}"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".interval">
                        <cti:formatPeriod type="DHMS_REDUCED" value="${result.analysis.intervalPeriod}"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".pointQuality">
                        <c:choose>
                            <c:when test="${result.analysis.excludeBadPointQualities}">
                                <i:inline key=".normalQualitiesOnly"/>
                            </c:when>
                            <c:otherwise>
                                <i:inline key=".allQualities"/>
                            </c:otherwise>
                        </c:choose>
                    </tags:nameValue2>
                    
                </tags:nameValueContainer2>
                
            </tags:sectionContainer2>
        </div>
        
        <%-- Actions --%>

        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="actions">
                
                <ul class="button-stack">
                    <%-- READ LP BUTTON --%>
                    <c:if test="${showReadOption}">
                        <c:url var="readUrl" value="/bulk/archiveDataAnalysis/read/readNow">
                            <c:param name="analysisId" value="${result.analysis.analysisId}"/>
                        </c:url>
                        <li><cti:button nameKey="read" renderMode="labeledImage" href="${readUrl}" icon="icon-control-play-blue"/></li>
                        <%-- 
                        not enabled in 5.3.2 
                        <li><cti:button nameKey="scheduleRead" renderMode="labeledImage" icon="icon-calendar"/></li> 
                        --%>
                    </c:if>
                    
                    <%-- CSV BUTTON --%>
                    <c:url var="csvUrl" value="/bulk/archiveDataAnalysis/tabular/csv">
                        <c:param name="analysisId" value="${result.analysis.analysisId}"/>
                    </c:url>
                    <li><cti:button nameKey="csv" renderMode="labeledImage" href="${csvUrl}" icon="icon-page-white-excel"/></li>
                    
                    <%-- TABULAR BUTTON --%>
                    <c:choose>
                        <c:when test="${underTabularSizeLimit}">
                            <c:url var="tabularUrl" value="/bulk/archiveDataAnalysis/tabular/view">
                                <c:param name="analysisId" value="${result.analysis.analysisId}"/>
                            </c:url>
                            <li><cti:button nameKey="viewTabular" renderMode="labeledImage" href="${tabularUrl}" icon="icon-application-view-columns"/></li>
                        </c:when>
                        <c:otherwise>
                            <li><cti:button nameKey="viewTabularDisabled" renderMode="labeledImage" disabled="true" icon="icon-application-view-columns"/></li>
                        </c:otherwise>
                    </c:choose>
                    
                    <%-- RE-ANALYZE BUTTON --%>
                    <c:url var="reanalyzeUrl" value="/bulk/archiveDataAnalysis/home/reanalyze">
                        <c:param name="oldAnalysisId" value="${result.analysis.analysisId}"/>
                    </c:url>
                    <li><cti:button nameKey="reanalyze" renderMode="labeledImage" href="${reanalyzeUrl}" icon="icon-arrow-rotate-clockwise"/></li>
                    
                    <%-- COLLECTION ACTIONS BUTTON --%>
                    <c:url var="collectionActionsUrl" value="/bulk/collectionActions">
                        <c:forEach var="p" items="${deviceCollection.collectionParameters}">
                            <c:param name="${p.key}" value="${p.value}"/>
                        </c:forEach>
                    </c:url>
                    <li><cti:button nameKey="collectionActions" renderMode="labeledImage" href="${collectionActionsUrl}" icon="icon-wrench"/></li>
                
                </ul>
            </tags:sectionContainer2>
        </div>
    
    </div>
    
    <%-- Results Data --%>
    <cti:url var="url" value="view">
        <cti:param name="analysisId" value="${result.analysis.analysisId}"/>
    </cti:url>
    <div data-url="${url}" data-static>
    <tags:sectionContainer2 nameKey="resultsTable">
        
        <table class="compact-results-table">
            <thead>
                <tr>
                    <tags:sort column="${nameCol}"/>
                    <tags:sort column="${typeCol}"/>
                    <tags:sort column="${meterNumberCol}"/>
                    <tags:sort column="${intervalsCol}"/>
                    <th><i:inline key=".timeline" arguments="${intervals}"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach items="${result.searchResult.resultList}" var="row">
                    <tr>
                    	<td>${row.name}</td>
                        <td><i:inline key="${row.paoIdentifier.paoType}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty row.meterNumber}">${row.meterNumber}</c:when>
                                <c:otherwise><i:inline key="yukon.web.defaults.na"/></c:otherwise>
                            </c:choose>
                        </td>
                        <td>${row.missingIntervals}</td>
                        <td><amr:analysisResult data="${row}" width="${barWidth}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${result.searchResult}" adjustPageCount="true" hundreds="true"/>
    </tags:sectionContainer2>
    </div>
    
</cti:standardPage>