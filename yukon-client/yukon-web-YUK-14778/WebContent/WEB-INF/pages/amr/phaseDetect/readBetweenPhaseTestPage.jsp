<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="phaseDetect.readBetweenPhaseTest">
<cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>

    <script type="text/javascript">

    var sendMsg = '<cti:msg2 key=".send" javaScriptEscape="true"/>';
    var sendingMsg = '<cti:msg2 key=".sending" javaScriptEscape="true"/>';
    var readingMsg = '<cti:msg2 key=".reading" javaScriptEscape="true"/>';
    var errorDetectMsg = '<cti:msg2 key=".errorDetect" javaScriptEscape="true"/>';
    var errorReadMsg = '<cti:msg2 key=".errorRead" javaScriptEscape="true"/>';
    function sendDetect () {
        var params = {'phase': $('#phase').val()};

        $('#sendDetectButton').val(sendingMsg);
        $('#sendDetectButton').prop({'disabled': true});
        $('#actionResultDiv').hide();
        $('#spinner').show();

        $.ajax({
            url: yukon.url('/amr/phaseDetect/startTest'),
            data: params,
            type: 'POST',
            dataType: 'json'
        }).done( function (data, textStatus, jqXHR) {
            if (data.errorOccurred) {
                $('#spinner').hide();
                $('#actionResultDiv').html(errorDetectMsg);
                $('#actionResultDiv').show();
                $('#sendDetectButton').val(sendMsg);
                $('#sendDetectButton').prop({'disabled': false});
            } else {
                $('#sendDetectButton').val(sendMsg);
                $('#' + data.phase).show();
                $('#spinner').hide();
                startTimers();
            }
        }).fail( function (jqXHR, textStatus) {
            $('#spinner').hide();
            $('#actionResultDiv').html(errorDetectMsg);
            $('#actionResultDiv').show();
            $('#sendDetectButton').val(sendMsg);
            $('#sendDetectButton').prop({'disabled': false});
        });
    }

    function startTimers () {
        $('#intervalTimerNote').show();
        $('#intervalTimerFont').css('color', 'red');
        $('#detectTimerNote').show();
        $('#detectTimerFont').css('color', 'red');
        setTimeout(updateTimers, 1000);
    }

    function updateTimers () {
        var intervalTimer = $('#intervalTimerSpan').html(),
            detectTimer = $('#detectTimerSpan').html(),
            timeoutSet = false;
        if (intervalTimer > 0) {
            intervalTimer = intervalTimer - 1;
            $('#intervalTimerSpan').html(intervalTimer);
            setTimeout(updateTimers, 1000);
            timeoutSet = true;
        }
        if (detectTimer > 0) {
            detectTimer = detectTimer - 1;
            $('#detectTimerSpan').html(detectTimer);
            if (!timeoutSet) {
                setTimeout(updateTimers, 1000);
            }
        } else {
            $('#sendDetectButton').hide();
            $('#readButton').prop({'disabled': false});
            $('#readButton').show();
        }
    }

    function reset () {
        $('#sendDetectButton').val(sendMsg);
        $('#sendDetectButton').prop({'disabled': false});
        $('#sendDetectButton').show();
        $('#resetButton').hide();
        $('#actionResultDiv').hide();
        $('#intervalTimerNote').hide();
        $('#detectTimerNote').hide();
        $('#intervalTimerSpan').html('${data.intervalLength}');
        $('#intervalTimerFont').css('color', 'black');
        $('#detectTimerSpan').html('${data.intervalLength * data.numIntervals}');
        $('#detectTimerFont').css('color', 'black');
    }

    function sendRead () {
        var params = {'phase': $('#phase').val()};
        $('#actionResultDiv').show();
        $.ajax({
            url: yukon.url('/amr/phaseDetect/readPhase'),
            type: 'POST',
            data: params
        }).done( function (data, textStatus, jqXHR) {
            var json = yukon.ui.util.getHeaderJSON(jqXHR);
            if (json.success) {
                $('#read' + json.phase).show();
                $('#readButton').val(readingMsg);
                $('#readButton').prop({'disabled': true});
                $('#actionResultDiv').html(data);
                if (json.complete) {
                    $('#complete').val('true');
                }
            } else {
                $('#actionResultDiv').show();
                $('#actionResultDiv').html(errorReadMsg);
            }
        }).fail( function (jqXHR, textStatus) {
            $('#actionResultDiv').html(errorReadMsg);
            $('#actionResultDiv').show();
        });
    }

    function sendClearCommand () {
        $('#spinner').show();
        $('#actionResultDiv').html('');
        $.ajax({
            url: yukon.url('/amr/phaseDetect/sendClearFromTestPage'),
            type: 'GET'
        }).done( function (data, textStatus, jqXHR) {
            var json = yukon.ui.util.getHeaderJSON(jqXHR);
            $('#spinner').hide();
            $('#actionResultDiv').show();
            if (json.success) {
                $('#clearButton').hide();
                $('#resetButton').show();
            }
        }).fail( function (jqXHR, textStatus) {
            $('#spinner').hide();
            $('#actionResultDiv').show();
        });
    }

    function readFinished () {
        $('#cancelReadButton').prop({'disabled': true});
        $('#readButton').val('${read}');
        $('#readButton').hide();
        $('#clearButton').prop({'disabled': false});
        $('#clearButton').show();
        if ($('#complete').val() == 'true') {
            $('#resultsButton').prop({'disabled': false});
        }
    }
    </script>
    
    <tags:sectionContainer2 nameKey="section">
        <table>
            <tr>
                <td valign="top" class="strong-label-small"><i:inline key=".noteLabel"/></td>
                <td style="font-size:11px;"><i:inline key=".noteText"/></td>
            </tr>
        </table>
        <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
            <tags:nameValue2 nameKey=".substation">${fn:escapeXml(data.substationName)}</tags:nameValue2>
            <tags:nameValue2 nameKey=".intervalLength">${data.intervalLength}</tags:nameValue2>
            <tags:nameValue2 nameKey=".deltaVoltage">${data.deltaVoltage}</tags:nameValue2>
            <tags:nameValue2 nameKey=".numOfIntervals">${data.numIntervals}</tags:nameValue2>
            <tags:nameValue2 nameKey=".phase">
                <select id="phase" name="phase">
                    <option <c:if test="${not empty setPhaseA}">selected</c:if> value="A">${phaseA}</option>
                    <option <c:if test="${not empty setPhaseB}">selected</c:if> value="B">${phaseB}</option>
                    <option <c:if test="${not empty setPhaseC}">selected</c:if> value="C">${phaseC}</option>
                </select>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".phaseDetectSent">
                <strong>
                    <span id="A" class="green <c:if test="${!state.phaseADetectSent}">dn</c:if>">${phaseA}</span>&nbsp;
                    <span id="B" class="green <c:if test="${!state.phaseBDetectSent}">dn</c:if>">${phaseB}</span>&nbsp;
                    <span id="C" class="green <c:if test="${!state.phaseCDetectSent}">dn</c:if>">${phaseC}</span>
                </strong>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".phasesRead">
                <strong>
                    <span id="readA" class="green <c:if test="${!state.phaseARead}">dn</c:if>">${phaseA}</span>&nbsp;
                    <span id="readB" class="green <c:if test="${!state.phaseBRead}">dn</c:if>">${phaseB}</span>&nbsp;
                    <span id="readC" class="green <c:if test="${!state.phaseCRead}">dn</c:if>">${phaseC}</span>
                </strong>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".intervalTimer">
                <strong>
                    <span id="intervalTimerFont">
                        <span id="intervalTimerSpan">${data.intervalLength}</span>
                    </span>&nbsp;
                    <i:inline key=".seconds"/>&nbsp;
                    <span id="intervalTimerNote" style="display: none;"><i:inline key=".voltIntervalNote"/></span>
                </strong>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".detectionTimer">
                <b>
                    <span id="detectTimerFont">
                        <span id="detectTimerSpan">${data.intervalLength * data.numIntervals}</span>
                    </span>&nbsp;
                    <i:inline key=".seconds"/>&nbsp;
                    <span id="detectTimerNote" style="display: none;"><i:inline key=".voltDetectNote"/></span>
                </b>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".nextAction" valueClass="full-width">
                <cti:msg2 var="clearPhaseData" key=".clearPhaseData"/>
                <c:set var="testStep" value="${state.testStep}"/>
            
                <div id="magicButtonDiv" class="clearfix">
                    <input type="hidden" name="complete" id="complete" value="${state.phaseDetectComplete}">
                    <c:choose>
                        <c:when test="${testStep != 'send'}"><cti:button id="sendDetectButton" nameKey="send" onclick="sendDetect();" classes="dn"/></c:when>
                        <c:otherwise><cti:button id="sendDetectButton" nameKey="send" onclick="sendDetect();"/></c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${testStep != 'read'}"><cti:button id="readButton" nameKey="read" onclick="sendRead();" classes="dn"/></c:when>
                        <c:otherwise><cti:button id="readButton" nameKey="read" onclick="sendRead();"/></c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${testStep != 'clear' && testStep != 'results'}"><cti:button id="clearButton" label="${clearPhaseData}" onclick="sendClearCommand();" classes="dn"/></c:when>
                        <c:otherwise><cti:button id="clearButton" label="${clearPhaseData}" onclick="sendClearCommand();"/></c:otherwise>
                    </c:choose>
                    <cti:button id="resetButton" classes="dn" nameKey="reset" onclick="reset();"/>
                    <img style="display: none;" id="spinner" src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>">
                </div>
                <div id="actionResultDiv" class="clearfix buffered">
                    <c:if test="${showReadProgress}">
                        <jsp:include page="readPhaseResults.jsp"></jsp:include>
                    </c:if>
                </div>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    
    <div class="page-action-area">
        <form action="<cti:url value="/amr/phaseDetect/phaseDetectResults"/>" method="get">
            <cti:button nameKey="resultsButton" id="resultsButton" disabled="${testStep != 'results'}" type="submit" classes="primary action"/>
            <cti:button nameKey="cancelTest" name="cancel" type="submit"/>
        </form>
    </div>
</cti:standardPage>