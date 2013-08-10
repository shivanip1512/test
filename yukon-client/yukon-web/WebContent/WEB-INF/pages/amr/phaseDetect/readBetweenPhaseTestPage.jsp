<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="phaseDetect.readBetweenPhaseTest">

    <script type="text/javascript">

    var sendMsg = '<cti:msg2 key=".send" javaScriptEscape="true"/>';
    var sendingMsg = '<cti:msg2 key=".sending" javaScriptEscape="true"/>';
    var readingMsg = '<cti:msg2 key=".reading" javaScriptEscape="true"/>';
    var errorDetectMsg = '<cti:msg2 key=".errorDetect" javaScriptEscape="true"/>';
    var errorReadMsg = '<cti:msg2 key=".errorRead" javaScriptEscape="true"/>';
    function sendDetect() {
            $('sendDetectButton').value = sendingMsg;
            $('sendDetectButton').disable();
            $('actionResultDiv').hide();
            $('spinner').show();

            var params = {'phase': $F('phase')};
            
            new Ajax.Request('/amr/phaseDetect/startTest', {
                method: 'post',
                parameters: params,
                onSuccess: function(resp, json) {
                    if(json.errorOccurred){
                        $('spinner').hide();
                        $('actionResultDiv').innerHTML = errorDetectMessage;
                        $('actionResultDiv').show();
                        $('sendDetectButton').value = sendMsg;
                        $('sendDetectButton').enable();
                    } else {
                    	$('sendDetectButton').value = sendMsg;
                        $(json.phase).show();
                        $('spinner').hide();
                        startTimers();
                    }
                },
                onException: function(resp, json) {
                    $('spinner').hide();
                    $('actionResultDiv').innerHTML = errorDetectMessage;
                    $('actionResultDiv').show();
                    $('sendDetectButton').value = sendMsg;
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
        	$('sendDetectButton').value = sendMsg;
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
            new Ajax.Updater('actionResultDiv', '/amr/phaseDetect/readPhase', {method: 'post', parameters:{'phase': $F('phase')}, evalScripts: 'true',
            	onSuccess: function(resp, json) {
                    if(json.success){
	                    $('read' + json.phase).show();
	                    $('readButton').value = readingMsg;
	                    $('readButton').disable();
                        if(json.complete){
                        	$('complete').value = 'true';
                        }
                    } else {
                    	$('actionResultDiv').show();
                    	$('actionResultDiv').innerHTML = errorReadMessage;
                    }
	            },
	            onException: function(resp, json) {
	                $('actionResultDiv').innerHTML = errorReadMessage;
	                $('actionResultDiv').show();
	            }
            });
        }

        function sendClearCommand(){
            $('spinner').show();
            $('actionResultDiv').innerHTML = '';
            new Ajax.Updater('actionResultDiv', '/amr/phaseDetect/sendClearFromTestPage', {method: 'get', evalScripts: 'true',
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
        	$('readButton').value = '${read}';
            $('readButton').hide();
            $('clearButton').enable();
            $('clearButton').show();
            if($F('complete') == 'true'){
                $('resultsButton').enable();
            }
        }
    </script>
    
    <tags:sectionContainer2 nameKey="section">
        <table>
            <tr>
                <td valign="top" class="smallBoldLabel"><i:inline key=".noteLabel"/></td>
                 <td style="font-size:11px;"><i:inline key=".noteText"/></td>
            </tr>
        </table>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".substation">
                ${data.substationName}
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".intervalLength">
                ${data.intervalLength}
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".deltaVoltage">
                ${data.deltaVoltage}
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".numOfIntervals">
                ${data.numIntervals}
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".phase">
                <select id="phase" name="phase">                    
                    <option <c:if test="${not empty setPhaseA}">selected</c:if> value="A">${phaseA}</option>
                    <option <c:if test="${not empty setPhaseB}">selected</c:if> value="B">${phaseB}</option>
                    <option <c:if test="${not empty setPhaseC}">selected</c:if> value="C">${phaseC}</option>
                </select>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".phaseDetectSent">
                <b><span id="A" style="padding-right: 3px;<c:if test="${!state.phaseADetectSent}">display: none;</c:if>">
                    <font color="green">${phaseA}</font>
                </span>
                <span id="B" style="padding-right: 3px;<c:if test="${!state.phaseBDetectSent}">display: none;</c:if>">
                    <font color="green">${phaseB}</font>
                </span>
                <span id="C" style="padding-right: 3px;<c:if test="${!state.phaseCDetectSent}">display: none;</c:if>">
                    <font color="green">${phaseC}</font>
                </span></b>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".phasesRead">
                <b><span id="readA" style="padding-right: 3px;<c:if test="${!state.phaseARead}">display: none;</c:if>">
                    <font color="green">${phaseA}</font>
                </span>
                <span id="readB" style="padding-right: 3px;<c:if test="${!state.phaseBRead}">display: none;</c:if>">
                    <font color="green">${phaseB}</font>
                </span>
                <span id="readC" style="padding-right: 3px;<c:if test="${!state.phaseCRead}">display: none;</c:if>">
                    <font color="green">${phaseC}</font>
                </span></b>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".intervalTimer">
                <b>
                    <font id="intervalTimerFont" color="black">
                        <span id="intervalTimerSpan">${data.intervalLength}</span>
                    </font>
                    &nbsp;<i:inline key=".seconds"/>&nbsp;<span id="intervalTimerNote" style="display: none;">
                    <i:inline key=".voltIntervalNote"/></span>
                </b>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".detectionTimer">
                <b>
                    <font id="detectTimerFont" color="black">
                        <span id="detectTimerSpan">${data.intervalLength * data.numIntervals}</span>
                    </font>
                    &nbsp;<i:inline key=".seconds"/>&nbsp;<span id="detectTimerNote" style="display: none;">
                    <i:inline key=".voltDetectNote"/></span>
                </b>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".nextAction">
                <cti:msg2 var="send" key=".send"/>
                <cti:msg2 var="read" key=".read"/>
                <cti:msg2 var="reset" key=".reset"/>
                <cti:msg2 var="clearPhaseData" key=".clearPhaseData"/>
                <c:set var="testStep" value="${state.testStep}"/>
            
                <div id="magicButtonDiv" style="float: left;padding-right: 10px;">
                    <input type="hidden" name="complete" id="complete" value="${state.phaseDetectComplete}">
                    <input type="button" id="sendDetectButton" <c:if test="${testStep != 'send'}">style="display: none;"</c:if> value="${send}" onclick="sendDetect();">
                    <input type="button" id="readButton" <c:if test="${testStep != 'read'}">style="display: none;"</c:if> value="${read}" onclick="sendRead();">
                    <input type="button" id="clearButton" <c:if test="${testStep != 'clear' && testStep != 'results'}">style="display: none;"</c:if> value="${clearPhaseData}" onclick="sendClearCommand();">
                    <input type="button" id="resetButton" style="display: none;" value="${reset}" onclick="reset();">
                    <img style="display: none;" id="spinner" src="<c:url value="/WebConfig/yukon/Icons/spinner.gif"/>">
                </div>
                <div id="actionResultDiv"  style="float: left;">
                    <c:if test="${showReadProgress}">
                        <jsp:include page="readPhaseResults.jsp"></jsp:include>
                    </c:if>
                </div>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    <br>
    <form action="/amr/phaseDetect/phaseDetectResults" method="get">
        <cti:button nameKey="cancelTest" name="cancel" type="submit"/>
        <cti:button nameKey="resultsButton" id="resultsButton" disabled="${testStep != 'results'}" type="submit"/>
    </form>
</cti:standardPage>