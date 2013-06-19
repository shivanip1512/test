<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="bulk.analysis.results">

    <div class="smallBoldLabel notesSection">
        <tags:selectedDevices id="deviceColletion" deviceCollection="${deviceCollection}" />
    </div>
    
    <div class="column_12_12">
    
        <%-- Analysis Info --%>
        <div class="column one">
            <tags:formElementContainer nameKey="analysisInfo">
                
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
                
            </tags:formElementContainer>
        </div>
        
        <%-- Actions --%>

        <div class="column two nogutter">
            <tags:formElementContainer nameKey="actions">
                
                <ul class="buttonStack">
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
                    <li><cti:button nameKey="csv" renderMode="labeledImage" href="${csvUrl}" icon="icon-csv"/></li>
                    
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
            </tags:formElementContainer>
        </div>
    
    </div>
    
    <%-- Results Data --%>
    <div class="clear">
    <tags:pagedBox2 nameKey="resultsTable" searchResult="${result.searchResult}" baseUrl="results" >
        
        <table class="compactResultsTable">
        
            <tr>
                <th><i:inline key=".deviceName"/></th>
                <th><i:inline key=".deviceType"/></th>
                <th><i:inline key=".holes"/></th>
                <th><i:inline key=".timeline" arguments="${intervals}"/></th>
            </tr>
        
            <c:forEach items="${result.searchResult.resultList}" var="row">
                <tr class="<tags:alternateRow odd="" even="altTableCell"/>">
                    <td><cti:deviceName deviceId="${row.id.paoId}"/></td>
                    <td><cti:formatObject value="${row.id.paoType}"/></td>
                    <td>${row.holeCount}</td>
                    <td><amr:analysisResult data="${row}" width="${barWidth}"/></td>
                </tr>
            </c:forEach>
        </table>
        
    </tags:pagedBox2>
    </div>
    
</cti:standardPage>