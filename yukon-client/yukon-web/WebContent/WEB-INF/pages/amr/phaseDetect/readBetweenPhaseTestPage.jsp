<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:msg key="yukon.web.modules.amr.phaseDetect.pageTitle" var="pageTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step5.sectionTitle" var="sectionTitle"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step5.noteLabel" var="noteLabel"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step5.noteText" var="noteText"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step5.voltIntervalNote" var="voltIntervalNote"/>
<cti:msg key="yukon.web.modules.amr.phaseDetect.step5.voltDetectNote" var="voltDetectNote"/>

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
            $('sendDetectButton').value = 'Sending';
            $('sendDetectButton').disable();
            $('actionResultDiv').hide();
            $('spinner').show();

            var params = {'phase': $F('phase')};
            
            new Ajax.Request('/spring/amr/phaseDetect/startTest', {
                method: 'post',
                parameters: params,
                onSuccess: function(resp, json) {
                    if(json.errorOccurred){
                        $('spinner').hide();
                        $('actionResultDiv').innerHTML = 'Error Sending Detect Command: ' + json.errorMsg;
                        $('actionResultDiv').show();
                        $('sendDetectButton').value = 'Send';
                        $('sendDetectButton').enable();
                    } else {
                    	$('sendDetectButton').value = 'Send';
                        $(json.phase).show();
                        $('spinner').hide();
                        startTimers();
                    }
                },
                onException: function(resp, json) {
                    $('spinner').hide();
                    $('actionResultDiv').innerHTML = 'Error Sending Detect Command: ' + json.errorMsg;
                    $('actionResultDiv').show();
                    $('sendDetectButton').value = 'Send';
                    $('sendDetectButton').enable();
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
                $('sendDetectButton').hide();
                $('readButton').enable();
                $('readButton').show();
            }
        }

        function reset(){
        	$('sendDetectButton').value = 'Send';
            $('sendDetectButton').enable();
            $('sendDetectButton').show();
            $('resetButton').hide();
            $('actionResultDiv').hide();
            $('intervalTimerNote').hide();
            $('detectTimerNote').hide();
            $('intervalTimerSpan').innerHTML = '${data.intervalLength}';
            $('intervalTimerFont').color = 'black';
            $('detectTimerSpan').innerHTML = '${data.intervalLength * data.numIntervals}';
            $('detectTimerFont').color = 'black';
        }

        function sendRead(){
        	$('actionResultDiv').show();
            new Ajax.Updater('actionResultDiv', '/spring/amr/phaseDetect/readPhase', {method: 'post', parameters:{'phase': $F('phase')}, evalScripts: 'true',
            	onSuccess: function(resp, json) {
                    if(json.success){
	                    $('read' + json.phase).show();
	                    $('readButton').value = 'Reading';
	                    $('readButton').disable();
                        if(json.complete){
                        	$('complete').value = 'true';
                        }
                    } else {
                    	$('actionResultDiv').show();
                    	$('actionResultDiv').innerHTML = 'Error Sending Read Command: ' + json.errorMsg;
                    }
	            },
	            onException: function(resp, json) {
	                $('actionResultDiv').innerHTML = 'Error Sending Read Command: ' + json.errorMsg;
	                $('actionResultDiv').show();
	            }
            });
        }

        function sendClearCommand(){
            $('spinner').show();
            $('actionResultDiv').innerHTML = '';
            new Ajax.Updater('actionResultDiv', '/spring/amr/phaseDetect/sendClearFromTestPage', {method: 'get', evalScripts: 'true',
                onSuccess: function(resp, json) {
                    $('spinner').hide();
                    $('actionResultDiv').show();
                    if(json.success){
                        $('clearButton').hide();
                        $('resetButton').show();
                    }
                },
                onException: function(resp, json) {
                	$('spinner').hide();
                	$('actionResultDiv').show();
                }
            });
        }

        function readFinished() {
            $('cancelReadButton').disable();
        	$('readButton').value = 'Read';
            $('readButton').hide();
            $('clearButton').enable();
            $('clearButton').show();
            if($F('complete') == 'true'){
                $('resultsButton').enable();
            }
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
                 <td style="font-size:11px;">${noteText}</td>
            </tr>
        </table>
        <tags:nameValueContainer>
            <tags:nameValue name="Substation" nameColumnWidth="200px">
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
                    <option <c:if test="${not empty setPhaseA}">selected</c:if> value="A">A</option>
                    <option <c:if test="${not empty setPhaseB}">selected</c:if> value="B">B</option>
                    <option <c:if test="${not empty setPhaseC}">selected</c:if> value="C">C</option>
                </select>
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
            <tags:nameValue name="Next Action">
                <div id="magicButtonDiv" style="float: left;padding-right: 10px;">
                    <input type="hidden" name="complete" id="complete" value="${state.phaseDetectComplete}">
                    <input type="button" id="sendDetectButton" <c:if test="${testStep != 'send'}">style="display: none;"</c:if> value="Send" onclick="sendDetect();">
                    <input type="button" id="readButton" <c:if test="${testStep != 'read'}">style="display: none;"</c:if> value="Read" onclick="sendRead();">
                    <input type="button" id="clearButton" <c:if test="${testStep != 'clear' && testStep != 'results'}">style="display: none;"</c:if> value="Clear Phase Data" onclick="sendClearCommand();">
                    <input type="button" id="resetButton" style="display: none;" value="Reset" onclick="reset();">
                    <img style="display: none;" id="spinner" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>">
                </div>
                <div id="actionResultDiv"  style="float: left;">
                    <c:if test="${showReadProgress}">
                        <jsp:include page="readPhaseResults.jsp"></jsp:include>
                    </c:if>
                </div>
            </tags:nameValue>
        </tags:nameValueContainer>
    </tags:sectionContainer>
    <br>
    <form action="/spring/amr/phaseDetect/phaseDetectResults" method="get">
        <input id="cancelButton" name="cancel" type="submit" value="Cancel Test">
        <input id="resultsButton" <c:if test="${testStep != 'results'}">disabled</c:if>  type="submit" value="Phase Detect Results">
    </form>
</cti:standardPage>