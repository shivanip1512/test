<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:msg key="yukon.web.modules.amr.phaseDetect.pageTitle" var="pageTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step4.sectionTitle" var="sectionTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step4.noteLabel" var="noteLabel"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step4.noteText" var="noteText"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step4.voltIntervalNote" var="voltIntervalNote"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step4.voltDetectNote" var="voltDetectNote"/>

<cti:standardPage title="Phase Detection" module="amr">
    <cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
    <cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
    
    <cti:standardMenu menuSelection="meters" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:crumbLink title="${pageTitle}" />
    </cti:breadCrumbs>

    <script type="text/javascript">

    function sendDetect() {
            $('sendDetectButton').value = 'Sending Detect Command';
            $('sendDetectButton').disable();
            $('spinner').show();
            $('readButton').disable();

            var params = {'phase': $F('phase')};
            
            new Ajax.Request('/spring/amr/phaseDetect/startTest', {
                method: 'post',
                parameters: params,
                onSuccess: function(resp, transport) {
                    if(transport.errorOccurred){
                        $('spinner').hide();
                        $('errorSpan').innerHTML = 'Error Sending Detect Command: ' + transport.errorMsg;
                        $('errorSpan').show();
                        $('resetButton').enable();
                    } else {
                        $(transport.phase).show();
                        $('spinner').hide();
                        startTimers();
                    }
                },
                onException: function(resp, transport) {
                    $('spinner').hide();
                    $('errorSpan').innerHTML = 'Error Sending Detect Command: ' + transport.errorMsg;
                    $('errorSpan').show();
                    $('resetButton').enable();
                }
            });

        }

        function startTimers() {
            $('intervalTimerNote').show();
            $('intervalTimerFont').color = 'red';
            $('detectTimerNote').show();
            $('detectTimerFont').color = 'red';
            setTimeout('updateTimers()', 1000);
        }

        function updateTimers(){
            var intervalTimer = $('intervalTimerSpan').innerHTML;
            var detectTimer = $('detectTimerSpan').innerHTML;
            var timeoutSet = false;
            if(intervalTimer > 0) {
                intervalTimer = intervalTimer - 1;
                $('intervalTimerSpan').innerHTML = intervalTimer;
                setTimeout('updateTimers()', 1000);
                timeoutSet = true;
            }
            if(detectTimer > 0) {
                detectTimer = detectTimer - 1;
                $('detectTimerSpan').innerHTML = detectTimer;
                if(!timeoutSet) {
                    setTimeout('updateTimers()', 1000);
                }
            } else {
                $('readButton').enable();
            }
        }

        function reset(){
            $('sendDetectButton').value = 'Send Phase Detect Command';
            $('sendDetectButton').enable();
            $('resetButton').disable();
            $('readButton').disable();
            $('readProgress').hide();
            $('intervalTimerNote').hide();
            $('detectTimerNote').hide();
            $('errorSpan').hide();
            $('intervalTimerSpan').innerHTML = '${data.intervalLength}';
            $('intervalTimerFont').color = 'black';
            $('detectTimerSpan').innerHTML = '${data.intervalLength * data.numIntervals}';
            $('detectTimerFont').color = 'black';
            $('clearButton').disable();
            $('clearResult').hide();
        }

        function sendRead(){
            new Ajax.Updater('readProgress', '/spring/amr/phaseDetect/readPhase', {method: 'get', parameters:{'phase': $F('phase')}, evalScripts: 'true',
            	onSuccess: function(resp, transport) {
                    if(transport.success){
	                    $('read' + transport.phase).show();
	                    $('readProgress').show();
	                    $('readButton').disable();
	                    if(transport.complete){
	                        $('resultsButton').enable();
	                    } else {
	                    	$('clearButton').enable();
	                    }
                    }
	            },
	            onException: function(resp, transport) {
	                $('errorSpan').innerHTML = 'Error Sending Read Command: ' + transport.errorMsg;
	                $('errorSpan').show();
	            }
            });
        }

        function sendClearCommand(){
            $('clearSpinner').show();
            new Ajax.Updater('clearResult', '/spring/amr/phaseDetect/sendClearFromTestPage', {method: 'get', evalScripts: 'true',
                onSuccess: function(resp, transport) {
                    $('clearSpinner').hide();
                    $('clearResult').show();
                    if(transport.success){
                        $('resetButton').enable();
                    }
                },
                onException: function(resp, transport) {
                	$('clearSpinner').hide();
                	$('clearResult').show();
                }
            });
        }
    </script>
    
    <c:set var="testStep" value="${state.testStep}"/>
    
    <%-- Phase Detect Title --%>
    <h2 style="display: inline;">
        ${pageTitle}
    </h2>
    <br>
    <br>
    <tags:sectionContainer title="${sectionTitle}">
        <table>
            <tr>
             <td valign="top" class="smallBoldLabel">${noteLabel}</td>
             <td style="font-size:11px;">
                 ${noteText}
             </td>
         </tr>
        </table>
        <table style="padding-right: 20px;padding-bottom: 10px;">
            <tr valign="top">
                <td>
                    <tags:nameValueContainer>
                        <tags:nameValue name="Substation">
                            ${data.substationName}
                        </tags:nameValue>
                        <tags:nameValue name="Interval Length">
                            ${data.intervalLength}
                        </tags:nameValue>
                        <tags:nameValue name="Delta Voltage">
                            ${data.deltaVoltage}
                        </tags:nameValue>
                        <tags:nameValue name="Number of Intervals">
                            ${data.numIntervals}
                        </tags:nameValue>
                        <tags:nameValue name="Phase">
                            <select id="phase" name="phase">
                                <option value="A">A</option>
                                <option value="B">B</option>
                                <option value="C">C</option>
                            </select>
                            <input id="sendDetectButton" type="button" <c:if test="${testStep != 'send'}">disabled</c:if> value="Send Phase Detect Command" onclick="sendDetect();"/>
                            <img style="display: none;" id="spinner" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>">
                            <font color="red"><b><span id="errorSpan"></span></b></font>
                        </tags:nameValue>
                        <tags:nameValue name="Phase Detect Sent">
                            <b><span id="A" style="padding-right: 3px;<c:if test="${!state.phaseADetectSent}">display: none;</c:if>">
                                <font color="green">A</font>
                            </span>
                            <span id="B" style="padding-right: 3px;<c:if test="${!state.phaseBDetectSent}">display: none;</c:if>">
                                <font color="green">B</font>
                            </span>
                            <span id="C" style="padding-right: 3px;<c:if test="${!state.phaseCDetectSent}">display: none;</c:if>">
                                <font color="green">C</font>
                            </span></b>
                        </tags:nameValue>
                        <tags:nameValue name="Phases Read">
                            <b><span id="readA" style="padding-right: 3px;<c:if test="${!state.phaseARead}">display: none;</c:if>">
                                <font color="green">A</font>
                            </span>
                            <span id="readB" style="padding-right: 3px;<c:if test="${!state.phaseBRead}">display: none;</c:if>">
                                <font color="green">B</font>
                            </span>
                            <span id="readC" style="padding-right: 3px;<c:if test="${!state.phaseCRead}">display: none;</c:if>">
                                <font color="green">C</font>
                            </span></b>
                        </tags:nameValue>
                        <tags:nameValue name="Interval Timer">
                            <b>
                                <font id="intervalTimerFont" color="black">
                                    <span id="intervalTimerSpan">${data.intervalLength}</span>
                                </font>
                                &nbsp;seconds&nbsp;<span id="intervalTimerNote" style="display: none;">${voltIntervalNote}</span>
                            </b>
                        </tags:nameValue>
                        <tags:nameValue name="Detection Timer">
                            <b>
                                <font id="detectTimerFont" color="black">
                                    <span id="detectTimerSpan">${data.intervalLength * data.numIntervals}</span>
                                </font>
                                &nbsp;seconds&nbsp;<span id="detectTimerNote" style="display: none;">${voltDetectNote}</span>
                            </b>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <input name="readButton" id="readButton" <c:if test="${testStep != 'read'}">disabled</c:if> type="button" value="Read Phase Data" onclick="sendRead();">
                </td>
                <td id="readProgress">
                    <c:if test="${showReadProgress}">
                        <c:choose>
						    <c:when test="${not empty errorMsg}">
						        <font color="red"><b>Error Sending Read Command: ${errorMsg}</b></font>
						    </c:when>
						    <c:otherwise>
						        <tags:updateableProgressBar totalCount="${totalCount}" countKey="COMMAND_REQUEST_EXECUTION/${id}/RESULTS_COUNT"/>
						    </c:otherwise>
						</c:choose>
                    </c:if>
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <input id="clearButton" <c:if test="${testStep != 'clear'}">disabled</c:if> type="button" value="Clear Phase Data" onclick="sendClearCommand();">
                    <img style="display: none;" id="clearSpinner" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>">
                    <span id="clear"></span>
                </td>
                <td id="clearResult">
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <input id="resetButton" disabled="disabled" type="button" value="Reset For Another Detection Command" onclick="reset();">
                </td>
            </tr>
        </table>
        <table>
            <tr>
               <td>
                   <form action="/spring/amr/phaseDetect/phaseDetectResults" method="get">
                       <input id="resultsButton" type="submit" <c:if test="${testStep != 'results'}">disabled</c:if> value="Phase Detect Results">
                   </form>
               </td>
            </tr>
        </table>
    </tags:sectionContainer>
</cti:standardPage>