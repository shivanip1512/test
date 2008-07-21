<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="High Bill Complaint Device Selection" module="amr">
    <cti:standardMenu menuSelection="deviceselection" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/csr/search" title="Device Selection" />
        <cti:crumbLink url="/spring/csr/home?deviceId=${deviceId}" title="Device Detail" />
    &gt; High Bill Complaint
</cti:breadCrumbs>

<script type="text/javascript">

	function createLPPoint(url){
		window.location = url;
	}
    
    function getReport(getReportUrl, redirectUrl) {
    
        // prettynessifier
        $('getReportProcessImg').src = '/WebConfig/yukon/Icons/indicator_arrows.gif';
        $('getReportProcessImg').show();
        $('getReportButton').value = 'Retrieving Report...';
        $('getReportButton').disable();
    
        // pluck start and end dates from the page
        var getReportStartDate = document.getElementsByName('getReportStartDate')[0].value;
        var getReportStopDate = document.getElementsByName('getReportStopDate')[0].value;
    
        // parameters for the call
        var params = $H();
        params['deviceId'] = ${deviceId};
        params['getReportStartDate'] = getReportStartDate;
        params['getReportStopDate'] = getReportStopDate;
    
        // make ajax request to run a profile report
        new Ajax.Request(getReportUrl, {
            
            'parameters': params,
            
            'onSuccess': function(transport) {
                
                // no errors, redirect back to main hbc page where our fresh report will be waiting for us
                // throw on a couple parameters to pre-set the date fields
                if (transport.responseText.strip() == '') {
                    window.location = redirectUrl + '?analyze=true&deviceId=' + ${deviceId} + '&getReportStartDate=' + getReportStartDate + '&getReportStopDate=' + getReportStopDate;
                }
                
                // errors, make error div visible and fill it with error response html, do not redirect
                // reset button
                else {
                    $('meterReadErrors').show();
                    $('meterReadErrors').update(transport.responseText);
                    
                    $('getReportProcessImg').hide();
                    $('getReportButton').value = 'Report';
                    $('getReportButton').enable();
                }
            }
          }
        );
    }
</script>

    <%-- FORMATTED DATE STRINGS --%>
    <cti:formatDate var="startDate" value="${startDateDate}" type="DATE" />
    <cti:formatDate var="stopDate" value="${stopDateDate}" type="DATE" />

    <%-- HBC TITLE --%>
    <table class="widgetColumns">
        <tr>
            <td>
                <h2 style="display: inline;">
                    High Bill Complaint
                </h2>
            </td>
            <td align="right">
                &nbsp; <!-- quick search? -->
            </td>
        </tr>
    </table>
    <br>
    
    <%-- ERROR MSG --%>
    <c:if test="${errorMsg != null}">
        <div style="color: red;margin: 10px 0px;">Error: ${errorMsg}</div>
    </c:if>
    
    <%-- LEFT SIDE --%>
    <table cellpadding="10">
    <tr valign="top">
    <td>
    
        <c:choose>
        <c:when test="${lmPointExists}">
        
        <%-- STEP 1: FIND PEAK DAY --%>
        <tags:sectionContainer title="Step 1: Find The Peak day" id="hbcStep1">
            
            <%-- GET REPORT --%>
            <div class="smallBoldLabel" style="display:inline;">Start Date: </div><tags:dateInputCalendar fieldName="getReportStartDate" fieldValue="${startDate}"/>
            <div class="smallBoldLabel" style="display:inline;">End Date: </div><tags:dateInputCalendar fieldName="getReportStopDate" fieldValue="${stopDate}"/>
            
            <c:if test="${readable}">
                <c:url var="getReportUrl" value="/spring/csr/highBill/getReport"/>
                <c:url var="hbcRedirectUrl" value="/spring/csr/highBill/view"/>
                
                <input type="button" id="getReportButton" value="Get Report" onclick="getReport('${getReportUrl}', '${hbcRedirectUrl}');" value="click me">
                <img id="getReportProcessImg" style="display:none;" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>">
            </c:if>
                    
            <%-- PROFILE PEAK REPORT(S) --%>
            <c:choose>
                <c:when test="${not analyze}">
                    
                    <c:choose>
                        <c:when test="${not empty preResult && !preResult.noData && preResult.deviceError == ''}">
                        <c:set var="reportHeader">
                            <jsp:attribute name="value">
                                <c:url var="analyzeThisDataUrl" value="/spring/csr/highBill/view">
                                    <c:param name="deviceId" value="${deviceId}"/>
                                    <c:param name="analyze" value="true"/>
                                    <c:param name="getReportStartDate" value="${prePeriodStartDateDisplay}"/>
                                    <c:param name="getReportStopDate" value="${prePeriodStopDateDisplay}"/>
                                </c:url>
                                Previous Reports: <a href="${analyzeThisDataUrl}">Expand Available Report</a>
                            </jsp:attribute>
                        </c:set>
                        </c:when>
                        <c:otherwise>
                            
                        </c:otherwise>
                    </c:choose>
                    
                </c:when>
                <c:otherwise>
                    <c:set var="reportHeader" value="Available Reports:"/>
                </c:otherwise>
            </c:choose>
            
            <div id="meterReadErrors" style="display:none;"></div>
    
            <tags:profilePeakReportsTable   id="hbcProfilePeakReport" 
                                            title="${reportHeader}"
                                            preResult="${preResult}"
                                            postResult="${postResult}"
                                            
                                            displayName="${displayName}"
                                            prePeriodStartDate="${prePeriodStartDate}"
                                            prePeriodStopDate="${prePeriodStopDate}"
                                            prePeriodStartDateDisplay="${prePeriodStartDateDisplay}"
                                            prePeriodStopDateDisplay="${prePeriodStopDateDisplay}"
                                            prePeakValue="${prePeakValue}"
                                            
                                            postPeriodStartDate="${postPeriodStartDate}"
                                            postPeriodStopDate="${postPeriodStopDate}"
                                            postPeriodStartDateDisplay="${postPeriodStartDateDisplay}"
                                            postPeriodStopDateDisplay="${postPeriodStopDateDisplay}"
                                            postPeakValue="${postPeakValue}"></tags:profilePeakReportsTable>
            
        </tags:sectionContainer>
        <br><br>


        <%-- STEP 2: COLLECT PROFILE DATA LINKS --%>
        <c:if test="${analyze}">
        <tags:sectionContainer title="Step 2: Collect Profile Data" id="hbcStep2">  
        
            <%-- COLLECT PROFILE DATA AROUND PEAK --%>
            <tags:collectProfileDataAroundPeaks deviceId="${deviceId}"
            
                                                prePeakValue="${prePeakValue}"
                                                preResult="${preResult}"
                                                preAvailableDaysAfterPeak="${preAvailableDaysAfterPeak}"
                                                preStartDate="${prePeriodStartDateDisplay}"
                                                preStopDate="${prePeriodStopDateDisplay}"
                                                
                                                postPeakValue="${postPeakValue}"
                                                postResult="${postResult}"
                                                postAvailableDaysAfterPeak="${postAvailableDaysAfterPeak}"
                                                postStartDate="${postPeriodStartDateDisplay}"
                                                postStopDate="${postPeriodStopDateDisplay}"
                                                
                                                profileRequestOrigin="HBC"
                                                isReadable="${readable}"
                                                email="${email}" />
                            
        </tags:sectionContainer>
        <br><br>
        </c:if>
        
        
        <%-- STEP 3: COLLECT PROFILE DATA LINKS --%>
        <c:if test="${analyze}">
        <tags:sectionContainer title="Step 3: Analyze Profile Data" id="hbcStep3">  
            
            <c:url var="chartUrlPrefix" value="/spring/csr/highBill/view">
                <c:param name="deviceId" value="${deviceId}"/>
                <c:param name="analyze" value="true"/>
                <c:param name="getReportStartDate" value="${startDate}"/>
                <c:param name="getReportStopDate" value="${stopDate}"/>
            </c:url>
        
            <%-- PRE CHART  --%>
            <c:if test="${!preResult.noData && preResult.deviceError == ''}">
                
                <%-- range links --%>
                <c:url var="prePeakDayChartUrl" value="${chartUrlPrefix}">
                    <c:param name="chartRange" value="PEAK"/>
                </c:url>
                
                <c:url var="prePeakPlusMinus3ChartUrl" value="${chartUrlPrefix}">
                    <c:param name="chartRange" value="PEAKPLUSMINUS3"/>
                </c:url>
                
                <c:url var="preEntireRangeChartUrl" value="${chartUrlPrefix}">
                    <c:param name="chartRange" value="ENTIRE"/>
                </c:url>
                
                <div class="smallBoldLabel" style="display:inline;">Chart Range: </div>
                <c:if test="${chartRange == 'PEAK'}"><div style="display:inline;" title="${prePeakValue}">Peak Day</div></c:if> 
                <c:if test="${chartRange != 'PEAK'}"><a href="${prePeakDayChartUrl}" title="${prePeakValue}">Peak Day</a></c:if> 
                |
                <c:if test="${chartRange == 'PEAKPLUSMINUS3'}"><div style="display:inline;" title="${prePeakValue} +/- 3 Days">Peak +/- 3 Days</div></c:if> 
                <c:if test="${chartRange != 'PEAKPLUSMINUS3'}"><a href="${prePeakPlusMinus3ChartUrl}" title="${prePeakValue} +/- 3 Days">Peak +/- 3 Days</a></c:if> 
                | 
                <c:if test="${chartRange == 'ENTIRE'}"><div style="display:inline;" title="${prePeriodStartDateDisplay} - ${prePeriodStopDateDisplay}">Report Period</div></c:if>
                <c:if test="${chartRange != 'ENTIRE'}"><a href="${preEntireRangeChartUrl}" title="${prePeriodStartDateDisplay} - ${prePeriodStopDateDisplay}">Report Period</a></c:if>
                
                <%-- chart title --%>
                <c:choose>
                    <c:when test="${chartRange == 'PEAK'}">
                        <cti:formatDate var="preChartTitle" value="${preRangeStartDate}" type="DATE" />
                    </c:when>
                    <c:when test="${chartRange == 'PEAKPLUSMINUS3'}">
                        <cti:formatDate var="chartTitleStartDate" value="${preRangeStartDate}" type="DATE" />
                        <cti:formatDate var="chartTitleStopDate" value="${preRangeStopDate}" type="DATE" />
                        <c:set var="preChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate} (${prePeakValue} +/- 3 Days)" />
                    </c:when>
                    <c:when test="${chartRange == 'ENTIRE'}">
                        <cti:formatDate var="chartTitleStartDate" value="${preRangeStartDate}" type="DATE" />
                        <cti:formatDate var="chartTitleStopDate" value="${preRangeStopDate}" type="DATE" />
                        <c:set var="preChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate}" />
                    </c:when>
                </c:choose>
                
                <tags:trend title="${preChartTitle}" width="600" height="220" reloadInterval="30" min="${yMin}" max="${yMax}" pointIds="${pointId}" startDate="${preStartDateMillis}" endDate="${preStopDateMillis}" interval="${preChartInterval}" converterType="${converterType}" graphType="${graphType}"/>
                <br>
                
                <%-- tabular data links --%>
                <div class="smallBoldLabel" style="display:inline;">Tabular Data: </div>
                
                <c:url var="preHbcArchivedDataReportUrl" value="/spring/amr/reports/hbcArchivedDataReport">
                    <c:param name="def" value="rawPointHistoryDefinition"/>
                    <c:param name="pointId" value="${pointId}"/>
                    <c:param name="startDate" value="${preStartDateMillis}"/>
                    <c:param name="stopDate" value="${preStopDateMillis}"/>
                    <c:param name="def" value="rawPointHistoryDefinition"/>
                    
                    <c:param name="analyze" value="true"/>
                    <c:param name="deviceId" value="${deviceId}"/>
                    <c:param name="getReportStartDate" value="${startDate}"/>
                    <c:param name="getReportStopDate" value="${stopDate}"/>
                    <c:param name="chartRange" value="${chartRange}"/>
                </c:url>
                
                <a href="${preHbcArchivedDataReportUrl}"/>HTML</a>
                |
                <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="csvView" pointId="${pointId}" startDate="${preStartDateMillis}" stopDate="${preStopDateMillis}">CSV</cti:simpleReportLinkFromNameTag>
                |
                <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="pdfView" pointId="${pointId}" startDate="${preStartDateMillis}" stopDate="${preStopDateMillis}">PDF</cti:simpleReportLinkFromNameTag>
                
                <%-- daily usage links --%>
                <br>
                <div class="smallBoldLabel" style="display:inline;">Daily Usage Report: </div>
                <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="htmlView" module="amr" showMenu="true" menuSelection="deviceselection" pointId="${pointId}" startDate="${preRangeStartDate}" stopDate="${preRangeStopDate}">HTML</cti:simpleReportLinkFromNameTag>
                |
                <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="csvView" module="amr" showMenu="true" menuSelection="deviceselection" pointId="${pointId}" startDate="${preRangeStartDate}" stopDate="${preRangeStopDate}">CSV</cti:simpleReportLinkFromNameTag>
                |
                <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="pdfView" module="amr" showMenu="true" menuSelection="deviceselection" pointId="${pointId}" startDate="${preRangeStartDate}" stopDate="${preReangeStopDate}">PDF</cti:simpleReportLinkFromNameTag>
                
            </c:if>
            
            <%-- POST CHART  --%>
            <c:if test="${!postResult.noData && postResult.deviceError == ''}">
            
                <%-- chart title --%>
                <c:choose>
                    <c:when test="${chartRange == 'PEAK'}">
                        <cti:formatDate var="postChartTitle" value="${postRangeStartDate}" type="DATE" />
                    </c:when>
                    <c:when test="${chartRange == 'PEAKPLUSMINUS3'}">
                        <cti:formatDate var="chartTitleStartDate" value="${postRangeStartDate}" type="DATE" />
                        <cti:formatDate var="chartTitleStopDate" value="${postRangeStopDate}" type="DATE" />
                        <c:set var="postChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate} (${postPeakValue} +/- 3 Days)" />
                    </c:when>
                    <c:when test="${chartRange == 'ENTIRE'}">
                        <cti:formatDate var="chartTitleStartDate" value="${postRangeStartDate}" type="DATE" />
                        <cti:formatDate var="chartTitleStopDate" value="${postRangeStopDate}" type="DATE" />
                        <c:set var="postChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate}" />
                    </c:when>
                </c:choose>
                
                <tags:trend title="${postChartTitle}" width="600" height="220" reloadInterval="30" min="${yMin}" max="${yMax}" pointIds="${pointId}" startDate="${postStartDateMillis}" endDate="${postStopDateMillis}" interval="${postChartInterval}" converterType="${converterType}" graphType="${graphType}"/>
                <br>
                
                <%-- tabular data links --%>
                <div class="smallBoldLabel" style="display:inline;">Tabular Data: </div>
                
                <c:url var="postHbcArchivedDataReportUrl" value="/spring/amr/reports/hbcArchivedDataReport">
                    <c:param name="def" value="rawPointHistoryDefinition"/>
                    <c:param name="pointId" value="${pointId}"/>
                    <c:param name="startDate" value="${postStartDateMillis}"/>
                    <c:param name="stopDate" value="${postStopDateMillis}"/>
                    <c:param name="def" value="rawPointHistoryDefinition"/>
                    
                    <c:param name="analyze" value="true"/>
                    <c:param name="deviceId" value="${deviceId}"/>
                    <c:param name="getReportStartDate" value="${startDate}"/>
                    <c:param name="getReportStopDate" value="${stopDate}"/>
                    <c:param name="chartRange" value="${chartRange}"/>
                </c:url>
                
                <a href="${postHbcArchivedDataReportUrl}"/>HTML</a>
                |
                <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="csvView" pointId="${pointId}" startDate="${postStartDateMillis}" stopDate="${postStopDateMillis}">CSV</cti:simpleReportLinkFromNameTag>
                |
                <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="pdfView" pointId="${pointId}" startDate="${postStartDateMillis}" stopDate="${postStopDateMillis}">PDF</cti:simpleReportLinkFromNameTag>
                
                <%-- daily usage links --%>
                <br>
                <div class="smallBoldLabel" style="display:inline;">Daily Usage Report: </div>
                <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="htmlView" module="amr" showMenu="true" menuSelection="deviceselection" pointId="${pointId}" startDate="${postRangeStartDate}" stopDate="${postRangeStopDate}">HTML</cti:simpleReportLinkFromNameTag>
                |
                <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="csvView" module="amr" showMenu="true" menuSelection="deviceselection" pointId="${pointId}" startDate="${postRangeStartDate}" stopDate="${postRangeStopDate}">CSV</cti:simpleReportLinkFromNameTag>
                |
                <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="pdfView" module="amr" showMenu="true" menuSelection="deviceselection" pointId="${pointId}" startDate="${postRangeStartDate}" stopDate="${postRangeStopDate}">PDF</cti:simpleReportLinkFromNameTag>
                
                
            </c:if>
        </tags:sectionContainer>
        </c:if>
        
        </c:when>
        
        <%-- CREATE LM POINT --%>
        <c:otherwise>
        
            <c:url var="highBillUrl" value="/spring/csr/highBill/view">
                <c:param name="deviceId" value="${deviceId}" />
                <c:param name="createLPPoint" value="true" />
            </c:url>
            <cti:deviceName deviceId="${deviceId}"></cti:deviceName> is not configured for 
            High Bill Processing. <input type="button" value="Configure Now" onclick="javascript:createLPPoint('${highBillUrl}')"></input>
            
        </c:otherwise>
        </c:choose>
    
    </td>
    
    <td>
        <div style="width:20px;"></div>
    </td>
    
    <%-- RIGHT SIDE: METER INFO WIDGET, PENDING COLLECTIONS WIDGET --%>
    <td>
    
        <div style="height:22px;">&nbsp;</div>
                
        <%-- METER INFORMATION WIDGET COLUMN --%>
        <%-- PENDING --%>
        <tags:widgetContainer deviceId="${deviceId}" identify="false">
        
            <tags:widget bean="meterInformationWidget" width="500px" identify="false" deviceId="${deviceId}" hideEnabled="false" />
            <br>
            <tags:widget bean="pendingProfilesWidget" width="500px" identify="false" deviceId="${deviceId}" hideEnabled="false" />
            
        </tags:widgetContainer>
    
    </td>
    </tr>
    </table>

</cti:standardPage>