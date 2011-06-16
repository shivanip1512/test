<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="analysisResults">

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle" />
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle" />
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}" />
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        <%-- interval data analysis --%>
        <cti:crumbLink><cti:msg2 key="yukon.web.modules.amr.analysisResults.pageName"/></cti:crumbLink>
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
                    
                    <tags:nameValue2 nameKey=".intervalLength">
                        <cti:formatPeriod type="DHMS_REDUCED" value="${result.analysis.intervalLength}"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".pointQuality">
                        <c:choose>
                            <c:when test="${result.analysis.excludeBadPointQualities}">
                                <i:inline key=".normalQualities"/>
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
                
                    <c:if test="${showReadOption}">
                        <li><cti:button key="read" renderMode="labeledImage"/></li>
                        <li><cti:button key="scheduleRead" renderMode="labeledImage"/></li>
                    </c:if>
                    <li><cti:button key="csv" renderMode="labeledImage"/></li>
                    <li><cti:button key="viewTabular" renderMode="labeledImage"/></li>
                
                </ul>
            </tags:formElementContainer>
        </cti:dataGridCell>
    
    </cti:dataGrid>
    
    <%-- Results Data --%>
    <tags:pagedBox2 nameKey="analysisResults" searchResult="${result.searchResult}" baseUrl="results" >
        
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