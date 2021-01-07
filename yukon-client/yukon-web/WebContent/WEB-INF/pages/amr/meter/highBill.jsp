<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="highChart" tagdir="/WEB-INF/tags/highChart" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="highBill">

<cti:url var="getReportUrl" value="/meter/highBill/getReport"/>
<cti:url var="hbcRedirectUrl" value="/meter/highBill/view"/>
    
<script type="text/javascript">

    function createLPPoint(url){
        window.location = url;
    }
    
    function getReport (getReportUrl, redirectUrl) {
        var reportBtn = $('#b-get-report'),
            meterErrors = $('#meterReadErrors'),
            getReportStartDate = document.getElementsByName('getReportStartDate')[0].value,
            getReportStopDate = document.getElementsByName('getReportStopDate')[0].value,
            paramObj;

        // parameters for the call
        paramObj = {
            'deviceId': '${deviceId}',
            'getReportStartDate': getReportStartDate,
            'getReportStopDate': getReportStopDate
        };
        // make ajax request to run a profile report
        $.ajax({
            url: getReportUrl,
            data: paramObj,
            type: 'POST'
        }).done( function (data, textStatus, jqXHR) {
            if (jqXHR.responseText.trim() === '') {
                // no errors, redirect back to main hbc page where our fresh report will be waiting for us
                // throw on a couple parameters to pre-set the date fields
                window.location = redirectUrl + '?analyze=true&deviceId=${deviceId}&getReportStartDate=' + getReportStartDate + '&getReportStopDate=' + getReportStopDate;
            } else {
                // errors, make error div visible and fill it with error response html, do not redirect
                meterErrors.html(jqXHR.responseText);
                meterErrors.show();
            }
        }).fail( function ( jqXHR, textStatus, errorThrown ) {
            meterErrors.html(jqXHR.responseText);
            meterErrors.show();
        }).always(function() {
            yukon.ui.unbusy(reportBtn);
        });
    }
$(document).on('click', '#b-get-report', function(event) {
    getReport('${getReportUrl}', '${hbcRedirectUrl}');
});
</script>

    <%-- FORMATTED DATE STRINGS --%>
    <cti:formatDate var="formattedStartDate" value="${startDate}" type="DATE"/>
    <cti:formatDate var="formattedStopDate" value="${stopDate}" type="DATE"/>
    
    <%-- ERROR MSG --%>
    <c:if test="${errorMsg != null}">
        <div class="error stacked"><i:inline key=".error" arguments="${errorMsg}"/></div>
    </c:if>
    
    <div class="column-14-10">
        <div class="column one">
    
            <c:choose>
                <c:when test="${lmPointExists}">
                
                <%-- STEP 1: FIND PEAK DAY --%>
                <tags:sectionContainer2 nameKey="step1" id="hbcStep1" styleClass="stacked">
                    
                    <%-- GET REPORT --%>
                    <tags:nameValueContainer2 tableClass="with-form-controls">
                        <tags:nameValue2 nameKey=".startDate">
                            <dt:date name="getReportStartDate" value="${startDate}"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".endDate">
                            <dt:date name="getReportStopDate" value="${stopDate}"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    
                    <c:if test="${readable}">
                        <div class="action-area stacked">
                            <cti:button nameKey="getReport" id="b-get-report" busy="true"/>
                        </div>
                    </c:if>
                    <%-- PROFILE PEAK REPORT(S) --%>
                    <c:choose>
                        <c:when test="${not analyze}">
                            
                            <c:if test="${not empty preResult && !preResult.noData && preResult.deviceError == ''}">
                                <c:set var="reportHeader">
                                    <jsp:attribute name="value">
                                        <cti:url var="analyzeThisDataUrl" value="/meter/highBill/view">
                                            <cti:param name="deviceId" value="${deviceId}"/>
                                            <cti:param name="analyze" value="true"/>
                                            <cti:param name="getReportStartDate" value="${formattedStartDate}"/>
                                            <cti:param name="getReportStopDate" value="${formattedStopDate}"/>
                                        </cti:url>
                                        <i:inline key=".prevReports"/>:&nbsp;<a href="${analyzeThisDataUrl}"><i:inline key=".expandAvailableReport"/></a>
                                    </jsp:attribute>
                                </c:set>
                            </c:if>
                            
                        </c:when>
                        <c:otherwise>
                            <cti:msg2 var="availableReports" key=".availableReports"/>
                            <c:set var="reportHeader" value="${availableReports}"/>
                        </c:otherwise>
                    </c:choose>
                    
                    <div id="meterReadErrors" style="display:none;"></div>
            
                    <c:if test="${! empty preResult || ! empty postResult}">
                    
                        <tags:sectionContainer title="${reportHeader}" styleClass="dashed">
                            
                            <table class="compact-results-table">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".period"/></th>
                                        <th><i:inline key=".dailyTotal"/></th>
                                        <th><i:inline key=".peak" arguments="${preResult.peakType.displayName}"/></th>
                                        <th><i:inline key=".peakTotalUsage"/></th>
                                    </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:if test="${! empty preResult}">
                                        <c:choose>
                                            <c:when test="${!preResult.noData && preResult.deviceError == ''}">
                                                <tr>
                                                    <td>
                                                        <cti:formatDate value="${preResult.rangeStartDate}" type="DATE"/> - 
                                                        <cti:formatDate value="${preResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV"/>  
                                                    </td>
                                                    <td>
                                                        <cti:list var="arguments">
                                                            <cti:item value="${preResult.averageDailyUsage}"/>
                                                            <cti:item value="${preResult.totalUsage}"/>
                                                        </cti:list>
                                                        <i:inline key=".dailyTotalValue" arguments="${arguments}"/>
                                                    </td>
                                                    <td>${preResult.peakValue}</td>
                                                    <td><i:inline key=".peakTotalUsageValue" arguments="${preResult.usage}"/></td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td colspan="4">
                                                    There was an error reading the meter<br>
                                                    <c:forEach items="${preResult.errors}" var="error">
                                                        <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                                                        ${error.porter}<br>
                                                        ${error.troubleshooting}<br>
                                                        </tags:hideReveal><br>
                                                    </c:forEach>
                                                    </td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                    
                                    <c:if test="${!empty postResult}">
                                        <c:choose>
                                            <c:when test="${!postResult.noData && postResult.deviceError == ''}">
                                                <tr>
                                                    <td>
                                                        <cti:formatDate value="${postResult.rangeStartDate}" type="DATE"/> - 
                                                        <cti:formatDate value="${postResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV"/>
                                                    </td>
                                                    <td nowrap>
                                                        ${postResult.averageDailyUsage} / ${postResult.totalUsage} kWH
                                                    </td>
                                                    <td>
                                                        ${postResult.peakValue}
                                                    </td>
                                                    <td>
                                                        ${postResult.usage} kWH
                                                    </td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <tr>
                                                    <td colspan="4">
                                                    There was an error reading the meter<br>
                                                    <c:forEach items="${postResult.errors}" var="error">
                                                        <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                                                        ${error.porter}<br>
                                                        ${error.troubleshooting}<br>
                                                        </tags:hideReveal><br>
                                                    </c:forEach>
                                                    </td>
                                                </tr>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </tbody>
                            </table>
                        </tags:sectionContainer>
                    </c:if>
                    
                </tags:sectionContainer2>
        
                <%-- STEP 2: COLLECT PROFILE DATA LINKS --%>
                <c:if test="${analyze}">
                    <tags:sectionContainer2 nameKey="step2" id="hbcStep2" styleClass="stacked">
                    
                        <%-- COLLECT PROFILE DATA AROUND PEAK --%>
                        <tags:collectProfileDataAroundPeaks deviceId="${deviceId}"
                                                            preResult="${preResult}"
                                                            preAvailableDaysAfterPeak="${preAvailableDaysAfterPeak}"
                                                            postResult="${postResult}"
                                                            postAvailableDaysAfterPeak="${postAvailableDaysAfterPeak}"
                                                            profileRequestOrigin="HBC"
                                                            isReadable="${readable}"
                                                            email="${email}"/>
                    </tags:sectionContainer2>
                </c:if>
                
                <%-- STEP 3: COLLECT PROFILE DATA LINKS --%>
                <c:if test="${analyze}">
                <cti:msg2 var="plusMinus3days" key=".plusMinus3days"/>
                <tags:sectionContainer2 nameKey="step3" id="hbcStep3" styleClass="stacked">
                    
                    <cti:url var="chartUrlPrefix" value="/meter/highBill/view">
                        <cti:param name="deviceId" value="${deviceId}"/>
                        <cti:param name="analyze" value="true"/>
                        <cti:param name="getReportStartDate" value="${formattedStartDate}"/>
                        <cti:param name="getReportStopDate" value="${formattedStopDate}"/>
                    </cti:url>
                
                    <%-- PRE CHART  --%>
                    <c:if test="${!preResult.noData && preResult.deviceError == ''}">
                        
                        <%-- range links --%>
                        <cti:url var="prePeakDayChartUrl" value="${chartUrlPrefix}">
                            <cti:param name="chartRange" value="PEAK"/>
                        </cti:url>
                        
                        <cti:url var="prePeakPlusMinus3ChartUrl" value="${chartUrlPrefix}">
                            <cti:param name="chartRange" value="PEAKPLUSMINUS3"/>
                        </cti:url>
                        
                        <cti:url var="preEntireRangeChartUrl" value="${chartUrlPrefix}">
                            <cti:param name="chartRange" value="ENTIRE"/>
                        </cti:url>
                        
                        <div class="strong-label-small" style="display:inline;"><i:inline key=".chartRange"/></div>
                        <c:if test="${chartRange == 'PEAK'}"><div style="display:inline;"><i:inline key=".peakDay"/></div></c:if> 
                        <c:if test="${chartRange != 'PEAK'}"><a href="${prePeakDayChartUrl}"><i:inline key=".peakDay"/></a></c:if> 
                        |
                        <c:if test="${chartRange == 'PEAKPLUSMINUS3'}"><div style="display:inline;"><i:inline key=".peakPlusMinus3days"/></div></c:if> 
                        <c:if test="${chartRange != 'PEAKPLUSMINUS3'}"><a href="${prePeakPlusMinus3ChartUrl}"><i:inline key=".peakPlusMinus3days"/></a></c:if> 
                        | 
                        <c:if test="${chartRange == 'ENTIRE'}"><div style="display:inline;"><i:inline key=".reportPeriod"/></div></c:if>
                        <c:if test="${chartRange != 'ENTIRE'}"><a href="${preEntireRangeChartUrl}"><i:inline key=".reportPeriod"/></a></c:if>
                        
                        <%-- chart title --%>
                        <c:choose>
                            <c:when test="${chartRange == 'PEAK'}">
                                <c:set var="preChartTitle" value="${preResult.peakValue}"/>
                            </c:when>
                            <c:when test="${chartRange == 'PEAKPLUSMINUS3'}">
                                <cti:formatDate var="chartTitleStartDate" value="${preChartStartDate}" type="DATE"/>
                                <cti:formatDate var="chartTitleStopDate" value="${preChartStopDate}" type="DATE_MIDNIGHT_PREV"/>
                                <c:set var="preChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate} (${preResult.peakValue} ${plusMinus3days})"/>
                            </c:when>
                            <c:when test="${chartRange == 'ENTIRE'}">
                                <cti:formatDate var="chartTitleStartDate" value="${preChartStartDate}" type="DATE"/>
                                <cti:formatDate var="chartTitleStopDate" value="${preChartStopDate}" type="DATE_MIDNIGHT_PREV"/>
                                <c:set var="preChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate}"/>
                            </c:when>
                        </c:choose>
        
                        <highChart:trend title="${preChartTitle}"
                                         ymin="${yMin}"
                                         ymax="${yMax}"
                                         pointIds="${pointId}"
                                         startDate="${preChartStartDateMillis}"
                                         endDate="${preChartStopDateMillis}"
                                         interval="${preChartInterval}"
                                         converterType="${converterType}"
                                         graphType="${graphType}"
                                         chartHeight="300"
                                         chartWidth="550"
                                         reloadInterval="30"/>
                        <br>
                        
                        <%-- tabular data links --%>
                        <div class="strong-label-small" style="display:inline;"><i:inline key=".tabularData"/> </div>
                        
                        <cti:url var="preHbcArchivedDataReportUrl" value="/amr/reports/hbcArchivedDataReport">
                            <cti:param name="def" value="rawPointHistoryDefinition"/>
                            <cti:param name="pointId" value="${pointId}"/>
                            <cti:param name="startDate" value="${preChartStartDateMillis}"/>
                            <cti:param name="stopDate" value="${preChartStopDateMillis}"/>
                            <cti:param name="def" value="rawPointHistoryDefinition"/>
                            
                            <cti:param name="analyze" value="true"/>
                            <cti:param name="deviceId" value="${deviceId}"/>
                            <cti:param name="getReportStartDate" value="${formattedStartDate}"/>
                            <cti:param name="getReportStopDate" value="${formattedStopDate}"/>
                            <cti:param name="chartRange" value="${chartRange}"/>
                        </cti:url>
        
                        <a href="${preHbcArchivedDataReportUrl}"/><i:inline key="yukon.web.modules.amr.fileFormatHtml"/></a>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="csvView" pointId="${pointId}" startDate="${preChartStartDateMillis}" stopDate="${preChartStopDateMillis}"><i:inline key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="pdfView" pointId="${pointId}" startDate="${preChartStartDateMillis}" stopDate="${preChartStopDateMillis}"><i:inline key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                        
                        <%-- daily usage links --%>
                        <br>
                        <div class="strong-label-small" style="display:inline;"><i:inline key=".dailyUsage"/> </div>
                        <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="extView" module="amr" showMenu="true" menuSelection="meters" pointId="${pointId}" startDate="${preChartStartDate}" stopDate="${preChartStopDate}"><i:inline key="yukon.web.modules.amr.fileFormatHtml"/></cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="csvView" module="amr" showMenu="true" menuSelection="meters" pointId="${pointId}" startDate="${preChartStartDate}" stopDate="${preChartStopDate}"><i:inline key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="pdfView" module="amr" showMenu="true" menuSelection="meters" pointId="${pointId}" startDate="${preChartStartDate}" stopDate="${preChartStopDate}"><i:inline key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                        
                    </c:if>
                    
                    <%-- POST CHART  --%>
                    <c:if test="${!postResult.noData && postResult.deviceError == ''}">
                    
                        <%-- chart title --%>
                        <c:choose>
                            <c:when test="${chartRange == 'PEAK'}">
                                <c:set var="postChartTitle" value="${postResult.peakValue}"/>
                            </c:when>
                            <c:when test="${chartRange == 'PEAKPLUSMINUS3'}">
                                <cti:formatDate var="chartTitleStartDate" value="${postChartStartDate}" type="DATE"/>
                                <cti:formatDate var="chartTitleStopDate" value="${postChartStopDate}" type="DATE_MIDNIGHT_PREV"/>
                                <c:set var="postChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate} (${postResult.peakValue} ${plusMinus3days})"/>
                            </c:when>
                            <c:when test="${chartRange == 'ENTIRE'}">
                                <cti:formatDate var="chartTitleStartDate" value="${postChartStartDate}" type="DATE"/>
                                <cti:formatDate var="chartTitleStopDate" value="${postChartStopDate}" type="DATE_MIDNIGHT_PREV"/>
                                <c:set var="postChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate}"/>
                            </c:when>
                        </c:choose>
        
                        <highChart:trend title="${postChartTitle}"
                                         ymin="${yMin}"
                                         ymax="${yMax}"
                                         pointIds="${pointId}"
                                         startDate="${postChartStartDateMillis}"
                                         endDate="${postChartStopDateMillis}"
                                         interval="${postChartInterval}"
                                         converterType="${converterType}"
                                         graphType="${graphType}"
                                         chartHeight="300"
                                         chartWidth="550"
                                         reloadInterval="30"/>
                        <br>
                        
                        <%-- tabular data links --%>
                        <div class="strong-label-small" style="display:inline;"><i:inline key=".tabularData"/> </div>
                        
                        <cti:url var="postHbcArchivedDataReportUrl" value="/amr/reports/hbcArchivedDataReport">
                            <cti:param name="def" value="rawPointHistoryDefinition"/>
                            <cti:param name="pointId" value="${pointId}"/>
                            <cti:param name="startDate" value="${postChartStartDateMillis}"/>
                            <cti:param name="stopDate" value="${postChartStopDateMillis}"/>
                            <cti:param name="def" value="rawPointHistoryDefinition"/>
                            
                            <cti:param name="analyze" value="true"/>
                            <cti:param name="deviceId" value="${deviceId}"/>
                            <cti:param name="getReportStartDate" value="${formattedStartDate}"/>
                            <cti:param name="getReportStopDate" value="${formattedStopDate}"/>
                            <cti:param name="chartRange" value="${chartRange}"/>
                        </cti:url>
                        
                        <a href="${postHbcArchivedDataReportUrl}"/><i:inline key="yukon.web.modules.amr.fileFormatHtml"/></a>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="csvView" pointId="${pointId}" startDate="${postChartStartDateMillis}" stopDate="${postChartStopDateMillis}"><i:inline key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="pdfView" pointId="${pointId}" startDate="${postChartStartDateMillis}" stopDate="${postChartStopDateMillis}"><i:inline key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                        
                        <%-- daily usage links --%>
                        <br>
                        <div class="strong-label-small" style="display:inline;"><i:inline key=".dailyUsage"/></div>
                        <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="extView" module="amr" showMenu="true" menuSelection="meters" pointId="${pointId}" startDate="${postChartStartDate}" stopDate="${postChartStopDate}"><i:inline key="yukon.web.modules.amr.fileFormatHtml"/></cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="csvView" module="amr" showMenu="true" menuSelection="meters" pointId="${pointId}" startDate="${postChartStartDate}" stopDate="${postChartStopDate}"><i:inline key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="pdfView" module="amr" showMenu="true" menuSelection="meters" pointId="${pointId}" startDate="${postChartStartDate}" stopDate="${postChartStopDate}"><i:inline key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                        
                        
                    </c:if>
                </tags:sectionContainer2>
                </c:if>
                
                </c:when>
                
                <%-- CREATE LP POINT --%>
                <c:otherwise>
                    <cti:url var="highBillUrl" value="/meter/highBill/view">
                        <cti:param name="deviceId" value="${deviceId}"/>
                        <cti:param name="createLPPoint" value="true"/>
                    </cti:url>
                    <cti:deviceName var="deviceName" deviceId="${deviceId}"/>
                    <i:inline key=".isNotConfigured" arguments="${deviceName}"/> 
                    <cti:button nameKey="config" onclick="createLPPoint('${highBillUrl}')"/>
                </c:otherwise>
            </c:choose>
    
        </div>
        
        <div class="column two nogutter">
        
            <tags:widgetContainer deviceId="${deviceId}" identify="false">
                <tags:widget bean="meterInformationWidget" identify="false" deviceId="${deviceId}" hideEnabled="false" container="section"/>
                <tags:widget bean="pendingProfilesWidget" identify="false" deviceId="${deviceId}" hideEnabled="false" container="section"/>
            </tags:widgetContainer>
            
        </div>
    </div>
</cti:standardPage>