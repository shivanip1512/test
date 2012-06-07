<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="highBill">

<script type="text/javascript">

	function createLPPoint(url){
		window.location = url;
	}
    
    function getReport(getReportUrl, redirectUrl) {
        var retrievingReport = '<cti:msg2 key=".retrievingReport" javaScriptEscape="true"/>';
        var getReportEscaped = '<cti:msg2  key=".getReport" javaScriptEscape="true"/>';
        // prettynessifier
        $('getReportProcessImg').src = '/WebConfig/yukon/Icons/indicator_arrows.gif';
        $('getReportProcessImg').show();
        $('getReportButton').value = retrievingReport;
        $('getReportButton').disable();
    
        // pluck start and end dates from the page
        var getReportStartDate = document.getElementsByName('getReportStartDate')[0].value;
        var getReportStopDate = document.getElementsByName('getReportStopDate')[0].value;
    
        // parameters for the call
        var params = $H({
            'deviceId': ${deviceId},
            'getReportStartDate': getReportStartDate,
            'getReportStopDate': getReportStopDate
        });
    
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
                    $('getReportButton').value = getReportEscaped;
                    $('getReportButton').enable();
                }
            },
            
            'onFailure': function(transport) {
                
                $('meterReadErrors').show();
                $('meterReadErrors').update(transport.responseText);
                
                $('getReportProcessImg').hide();
                $('getReportButton').value = getReportEscaped;
                $('getReportButton').enable();
            }
            
          }
        );
    }
</script>

    <%-- FORMATTED DATE STRINGS --%>
    <cti:formatDate var="formattedStartDate" value="${startDate}" type="DATE" />
    <cti:formatDate var="formattedStopDate" value="${stopDate}" type="DATE" />
    
    <%-- ERROR MSG --%>
    <c:if test="${errorMsg != null}">
        <div style="color: red;margin: 10px 0px;"><i:inline key=".error" arguments="${errorMsg}"/></div>
    </c:if>
    
    <%-- LEFT SIDE --%>
    <table cellpadding="10">
    <tr valign="top">
    <td>
    
        <c:choose>
        <c:when test="${lmPointExists}">
        
        <%-- STEP 1: FIND PEAK DAY --%>
        <cti:msg2 var="step1" key=".step1"/>
        <tags:sectionContainer title="${step1}" id="hbcStep1">
            
            <%-- GET REPORT --%>
            <div class="smallBoldLabel" style="display:inline;"><i:inline key=".startDate"/> </div><tags:dateInputCalendar fieldName="getReportStartDate" fieldValue="${formattedStartDate}"/>
            <div class="smallBoldLabel" style="display:inline;"><i:inline key=".endDate"/> </div><tags:dateInputCalendar fieldName="getReportStopDate" fieldValue="${formattedStopDate}"/>
            
            <c:if test="${readable}">
                <cti:url var="getReportUrl" value="/spring/meter/highBill/getReport"/>
                <cti:url var="hbcRedirectUrl" value="/spring/meter/highBill/view"/>
                
                
                <input type="button" id="getReportButton" value="<cti:msg2 key=".getReport"/>" onclick="getReport('${getReportUrl}', '${hbcRedirectUrl}');" class="formSubmit"> 
                <img id="getReportProcessImg" style="display:none;" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>">
            </c:if>
                    
            <%-- PROFILE PEAK REPORT(S) --%>
            <c:choose>
                <c:when test="${not analyze}">
                    
                    <c:choose>
                        <c:when test="${not empty preResult && !preResult.noData && preResult.deviceError == ''}">
                        <c:set var="reportHeader">
                            <jsp:attribute name="value">
                                <cti:url var="analyzeThisDataUrl" value="/spring/meter/highBill/view">
                                    <cti:param name="deviceId" value="${deviceId}"/>
                                    <cti:param name="analyze" value="true"/>
                                    <cti:param name="getReportStartDate" value="${formattedStartDate}"/>
                                    <cti:param name="getReportStopDate" value="${formattedStopDate}"/>
                                </cti:url>
                                <i:inline key=".prevReports"/><a href="${analyzeThisDataUrl}"><i:inline key=".expandAvailableReport"/></a>
                            </jsp:attribute>
                        </c:set>
                        </c:when>
                        <c:otherwise>
                            
                        </c:otherwise>
                    </c:choose>
                    
                </c:when>
                <c:otherwise>
                    <cti:msg2 var="availableReports" key=".availableReports"/>
                    <c:set var="reportHeader" value="${availableReports}"/>
                </c:otherwise>
            </c:choose>
            
            <div id="meterReadErrors" style="display:none;"></div>
    
            <tags:profilePeakReportsTable   id="hbcProfilePeakReport" 
                                            title="${reportHeader}"
                                            preResult="${preResult}"
                                            postResult="${postResult}" />
            
        </tags:sectionContainer>
        <br><br>


        <%-- STEP 2: COLLECT PROFILE DATA LINKS --%>
        <c:if test="${analyze}">
        <cti:msg2 var="step2" key=".step2"/>
        <tags:sectionContainer title="${step2}" id="hbcStep2">  
        
            <%-- COLLECT PROFILE DATA AROUND PEAK --%>
            <c:set var="pre"/>
            <tags:collectProfileDataAroundPeaks deviceId="${deviceId}"
            
                                                preResult="${preResult}"
                                                preAvailableDaysAfterPeak="${preAvailableDaysAfterPeak}"
                                                
                                                postResult="${postResult}"
                                                postAvailableDaysAfterPeak="${postAvailableDaysAfterPeak}"
                                                
                                                profileRequestOrigin="HBC"
                                                isReadable="${readable}"
                                                email="${email}" />
                            
        </tags:sectionContainer>
        <br><br>
        </c:if>
        
        
        <%-- STEP 3: COLLECT PROFILE DATA LINKS --%>
        <c:if test="${analyze}">
        <cti:msg2 var="step3" key=".step3"/>
        <cti:msg2 var="plusMinus3days" key=".plusMinus3days"/>
        <tags:sectionContainer title="${step3}" id="hbcStep3">  
            
            <cti:url var="chartUrlPrefix" value="/spring/meter/highBill/view">
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
                
                <div class="smallBoldLabel" style="display:inline;"><i:inline key=".chartRange"/></div>
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
                        <c:set var="preChartTitle" value="${preResult.peakValue}" />
                    </c:when>
                    <c:when test="${chartRange == 'PEAKPLUSMINUS3'}">
                        <cti:formatDate var="chartTitleStartDate" value="${preChartStartDate}" type="DATE" />
                        <cti:formatDate var="chartTitleStopDate" value="${preChartStopDate}" type="DATE_MIDNIGHT_PREV" />
                        <c:set var="preChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate} (${preResult.peakValue} ${plusMinus3days})" />
                    </c:when>
                    <c:when test="${chartRange == 'ENTIRE'}">
                        <cti:formatDate var="chartTitleStartDate" value="${preChartStartDate}" type="DATE" />
                        <cti:formatDate var="chartTitleStopDate" value="${preChartStopDate}" type="DATE_MIDNIGHT_PREV" />
                        <c:set var="preChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate}" />
                    </c:when>
                </c:choose>
                
                <tags:trend title="${preChartTitle}" width="600" height="220" reloadInterval="30" min="${yMin}" max="${yMax}" pointIds="${pointId}" startDate="${preChartStartDateMillis}" endDate="${preChartStopDateMillis}" interval="${preChartInterval}" converterType="${converterType}" graphType="${graphType}"/>
                <br>
                
                <%-- tabular data links --%>
                <div class="smallBoldLabel" style="display:inline;"><i:inline key=".tabularData"/> </div>
                
                <cti:url var="preHbcArchivedDataReportUrl" value="/spring/amr/reports/hbcArchivedDataReport">
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
                <div class="smallBoldLabel" style="display:inline;"><i:inline key=".dailyUsage"/> </div>
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
                        <c:set var="postChartTitle" value="${postResult.peakValue}" />
                    </c:when>
                    <c:when test="${chartRange == 'PEAKPLUSMINUS3'}">
                        <cti:formatDate var="chartTitleStartDate" value="${postChartStartDate}" type="DATE" />
                        <cti:formatDate var="chartTitleStopDate" value="${postChartStopDate}" type="DATE_MIDNIGHT_PREV" />
                        <c:set var="postChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate} (${postResult.peakValue} ${plusMinus3days})" />
                    </c:when>
                    <c:when test="${chartRange == 'ENTIRE'}">
                        <cti:formatDate var="chartTitleStartDate" value="${postChartStartDate}" type="DATE" />
                        <cti:formatDate var="chartTitleStopDate" value="${postChartStopDate}" type="DATE_MIDNIGHT_PREV" />
                        <c:set var="postChartTitle" value="${chartTitleStartDate} - ${chartTitleStopDate}" />
                    </c:when>
                </c:choose>
                
                <tags:trend title="${postChartTitle}" width="600" height="220" reloadInterval="30" min="${yMin}" max="${yMax}" pointIds="${pointId}" startDate="${postChartStartDateMillis}" endDate="${postChartStopDateMillis}" interval="${postChartInterval}" converterType="${converterType}" graphType="${graphType}"/>
                <br>
                
                <%-- tabular data links --%>
                <div class="smallBoldLabel" style="display:inline;"><i:inline key=".tabularData"/> </div>
                
                <cti:url var="postHbcArchivedDataReportUrl" value="/spring/amr/reports/hbcArchivedDataReport">
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
                <div class="smallBoldLabel" style="display:inline;"><i:inline key=".dailyUsage"/></div>
                <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="extView" module="amr" showMenu="true" menuSelection="meters" pointId="${pointId}" startDate="${postChartStartDate}" stopDate="${postChartStopDate}"><i:inline key="yukon.web.modules.amr.fileFormatHtml"/></cti:simpleReportLinkFromNameTag>
                |
                <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="csvView" module="amr" showMenu="true" menuSelection="meters" pointId="${pointId}" startDate="${postChartStartDate}" stopDate="${postChartStopDate}"><i:inline key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                |
                <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="pdfView" module="amr" showMenu="true" menuSelection="meters" pointId="${pointId}" startDate="${postChartStartDate}" stopDate="${postChartStopDate}"><i:inline key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                
                
            </c:if>
        </tags:sectionContainer>
        </c:if>
        
        </c:when>
        
        <%-- CREATE LM POINT --%>
        <c:otherwise>
            <cti:url var="highBillUrl" value="/spring/meter/highBill/view">
                <cti:param name="deviceId" value="${deviceId}" />
                <cti:param name="createLPPoint" value="true" />
            </cti:url>
            <cti:deviceName var="deviceName" deviceId="${deviceId}"/>
            <i:inline key=".isNotConfigured" arguments="${deviceName}"/> 
            <input type="button" value="Configure Now" onclick="javascript:createLPPoint('${highBillUrl}')" class="formSubmit">
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
