<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:standardPage module="amr" page="phaseDetect.sendTest">
<cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>

<script type="text/javascript">
    var readingMsg = '<cti:msg2 key=".reading" javaScriptEscape="true"/>';
    var sendMsg = '<cti:msg2 key=".send" javaScriptEscape="true"/>';
    var errorDetectMsg = '<cti:msg2 key=".errorDetect" javaScriptEscape="true"/>';

    function sendDetect () {
        var params = {'phase': $('#phase').val()};

        $('#sendDetectButton').val(sendMsg);
        $('#sendDetectButton').prop({'disabled': true});
        $('#actionResultDiv').hide();
        $('#spinner').show();

        $.ajax({
            url: yukon.url('/amr/phaseDetect/startTest'),
            data: params,
            type: 'POST',
            dataType: 'json'
        }).done( function (data, textStatus, jqXHR) {
           $('#complete').val(data.complete);
           if (data.errorOccurred == 'true') {
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
        }).fail(function (jqXHR, textStatus) {
            $('#spinner').hide();
            $('#actionResultDiv').html(errorDetectMsg);
            $('#actionResultDiv').show();
            $('#sendDetectButton').val(sendMsg);
            $('#sendDetectButton').prop({'disabled': false});
        });

    }

    function startTimers() {
        $('#intervalTimerNote').show();
        $('#intervalTimerFont').css('color', 'red');
        $('#detectTimerNote').show();
        $('#detectTimerFont').css('color', 'red');
        setTimeout(updateTimers, 1000);
    }

    function updateTimers() {
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
            if ($('#complete').val() == 'true') {
                $('#sendDetectButton').hide();
                $('#readButton').show();
            } else {
                $('#sendDetectButton').hide();
                $('#resetButton').show();
            }
        }
    }

    function reset() {
        $('#sendDetectButton').val(sendMsg);
        $('#sendDetectButton').prop({'disabled': false});
        $('#sendDetectButton').show();
        $('#resetButton').hide();
        $('#actionResultDiv').hide();
        $('#intervalTimerNote').hide();
        $('#detectTimerNote').hide();
        $('#intervalTimerSpan').html('${data.intervalLength}');
        $('#intervalTimerFont').css('color', '#555');
        $('#detectTimerSpan').html('${data.intervalLength * data.numIntervals}');
        $('#detectTimerFont').css('color', '#555');
    }

    function sendRead() {
        $('#actionResultDiv').show();
        $.ajax({
            url: yukon.url('/amr/phaseDetect/readPhase'),
            type: 'POST'
        }).done(function (data, textStatus, jqXHR) {
            var json = yukon.ui.util.getHeaderJSON(jqXHR);
            if (json.success) {
                $('#readButton').val(readingMsg);
                $('#readButton').prop({'disabled': true});
                $('#actionResultDiv').html(data);
            } else {
                $('#actionResultDiv').html(errorReadMsg);
            }
        }).fail(function (jqXHR, textStatus) {
            $('#actionResultDiv').html(errorReadMsg);
        });
    }

    function readFinished() {
        $('#cancelReadButton').prop({'disabled': true});
        $('#resultsButton').prop({'disabled': false});
    }
</script>

    <c:set var="testStep" value="${state.testStep}"/>

    <tags:sectionContainer2 nameKey="phaseDetectionTest">
        <table>
            <tr>
                <td valign="top" class="strong-label-small"><i:inline key=".noteLabel"/></td>
                <td style="font-size:11px;">
                    <i:inline key=".noteText"/>
                </td>
            </tr>
        </table>
        <tags:nameValueContainer2 tableClass="with-form-controls">
            <tags:nameValue2 nameKey=".substation">${fn:escapeXml(data.substationName)}</tags:nameValue2>
            <tags:nameValue2 nameKey=".intervalLength">${data.intervalLength}</tags:nameValue2>
            <tags:nameValue2 nameKey=".deltaVoltage">${data.deltaVoltage}</tags:nameValue2>
            <tags:nameValue2 nameKey=".numOfIntervals">${data.numIntervals}</tags:nameValue2>
            <tags:nameValue2 nameKey=".phase">
                <select id="phase" name="phase">
                    <c:forEach var="phase" items="${phases}">
                        <option value="${phase}"><cti:msg2 key="${phase}"/></option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".phaseDetectSent">
                <strong>
                    <span id="A" class="green <c:if test="${!state.phaseADetectSent}">dn</c:if>">${phaseA}</span>&nbsp;
                    <span id="B" class="green <c:if test="${!state.phaseBDetectSent}">dn</c:if>">${phaseB}</span>&nbsp;
                    <span id="C" class="green <c:if test="${!state.phaseCDetectSent}">dn</c:if>">${phaseC}</span>
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
                <strong>
                    <span id="detectTimerFont">
                        <span id="detectTimerSpan">${data.intervalLength * data.numIntervals}</span>
                    </span>&nbsp;
                    <i:inline key=".seconds"/>&nbsp;
                    <span id="detectTimerNote" style="display: none;"><i:inline key=".voltDetectNote"/></span>
                </strong>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".nextAction">
                <cti:msg2 var="send" key=".send"/>
                <cti:msg2 var="read" key=".read"/>
                <cti:msg2 var="reset" key=".reset"/>
                <div id="magicButtonDiv" style="float: left;padding-right: 10px;">
                    <input type="hidden" name="complete" id="complete" value="${state.phaseDetectComplete}">
                    <c:choose>
                        <c:when test="${testStep != 'send'}"><cti:button id="sendDetectButton" label="${send}" onclick="sendDetect();" classes="dn"/></c:when>
                        <c:otherwise><cti:button id="sendDetectButton" label="${send}" onclick="sendDetect();"/></c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${testStep != 'read'}"><cti:button id="readButton" label="${read}" onclick="sendRead();" classes="dn"/></c:when>
                        <c:otherwise><cti:button id="readButton" label="${read}" onclick="sendRead();"/></c:otherwise>
                    </c:choose>
                    
                    <cti:button id="resetButton" classes="dn" label="${reset}" onclick="reset();"/>
                    <img style="display: none;" id="spinner" src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>">
                </div>
                <div id="actionResultDiv"  style="float: left;">
                    <c:if test="${showReadProgress}">
                        <jsp:include page="readPhaseResults.jsp"></jsp:include>
                    </c:if>
                </div>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    
    <div class="page-action-area">
        <cti:url var="phaseDetectResultsUrl" value="/amr/phaseDetect/phaseDetectResults"/>
        <form action="${phaseDetectResultsUrl}" method="get">
            <cti:button nameKey="phaseDetectResults" id="resultsButton" disabled="${testStep != 'results'}" type="submit" classes="primary action"/>
            <cti:button nameKey="cancelTest" name="cancel" type="submit"/>
        </form>
    </div>
</cti:standardPage>