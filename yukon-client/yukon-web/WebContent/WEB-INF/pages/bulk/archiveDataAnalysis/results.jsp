<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="analysis.results">

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <%-- metering --%>
        <cti:msg var="metersPageTitle" key="yukon.web.modules.amr.meteringStart.pageName" />
        <cti:crumbLink url="/spring/meter/start" title="${metersPageTitle}" />
        <%-- ADA List --%>
        <cti:msg var="adaListPageTitle" key="yukon.web.modules.amr.analysis.list.pageName" />
        <cti:crumbLink url="/spring/bulk/archiveDataAnalysis/list/view" title="${adaListPageTitle}" />
        <%-- ADA Results --%>
        <cti:crumbLink><i:inline key="yukon.web.modules.amr.analysis.results.pageName"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <div class="smallBoldLabel notesSection">
        <tags:selectedDevices id="deviceColletion" deviceCollection="${deviceCollection}" />
    </div>
    
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
    
        <%-- Analysis Info --%>
        <cti:dataGridCell>
            <tags:formElementContainer nameKey="analysisInfo">
                
                <tags:nameValueContainer2>
                    
                    <tags:nameValue2 nameKey=".attribute">
                        ${result.analysis.attribute.description}
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
        </cti:dataGridCell>
        
        <%-- Actions --%>
        <cti:dataGridCell>
            <tags:formElementContainer nameKey="actions">
                
                <ul class="buttonStack">
                    <%-- READ LP BUTTON --%>
                    <c:if test="${showReadOption}">
                        <c:url var="readUrl" value="/spring/bulk/archiveDataAnalysis/read/readNow">
                            <c:param name="analysisId" value="${result.analysis.analysisId}"/>
                        </c:url>
                        <li><cti:button key="read" renderMode="labeledImage" href="${readUrl}"/></li>
                        <%-- 
                        not enabled in 5.3.2 
                        <li><cti:button key="scheduleRead" renderMode="labeledImage"/></li> 
                        --%>
                    </c:if>
                    
                    <%-- CSV BUTTON --%>
                    <c:url var="csvUrl" value="/spring/bulk/archiveDataAnalysis/tabular/csv">
                        <c:param name="analysisId" value="${result.analysis.analysisId}"/>
                    </c:url>
                    <li><cti:button key="csv" renderMode="labeledImage" href="${csvUrl}"/></li>
                    
                    <%-- TABULAR BUTTON --%>
                    <c:choose>
                        <c:when test="${underTabularSizeLimit}">
                            <c:url var="tabularUrl" value="/spring/bulk/archiveDataAnalysis/tabular/view">
                                <c:param name="analysisId" value="${result.analysis.analysisId}"/>
                            </c:url>
                            <li><cti:button key="viewTabular" renderMode="labeledImage" href="${tabularUrl}"/></li>
                        </c:when>
                        <c:otherwise>
                            <li><cti:button key="viewTabularDisabled" renderMode="labeledImage" disabled="true"/></li>
                        </c:otherwise>
                    </c:choose>
                    
                    <%-- RE-ANALYZE BUTTON --%>
                    <c:url var="reanalyzeUrl" value="/spring/bulk/archiveDataAnalysis/home/reanalyze">
                        <c:param name="oldAnalysisId" value="${result.analysis.analysisId}"/>
                    </c:url>
                    <li><cti:button key="reanalyze" renderMode="labeledImage" href="${reanalyzeUrl}"/></li>
                    
                    <%-- COLLECTION ACTIONS BUTTON --%>
                    <c:url var="collectionActionsUrl" value="/spring/bulk/collectionActions">
                        <c:forEach var="p" items="${deviceCollection.collectionParameters}">
                            <c:param name="${p.key}" value="${p.value}"/>
                        </c:forEach>
                    </c:url>
                    <li><cti:button key="collectionActions" renderMode="labeledImage" href="${collectionActionsUrl}"/></li>
                
                </ul>
            </tags:formElementContainer>
        </cti:dataGridCell>
    
    </cti:dataGrid>
    
    <%-- Results Data --%>
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
    
</cti:standardPage>