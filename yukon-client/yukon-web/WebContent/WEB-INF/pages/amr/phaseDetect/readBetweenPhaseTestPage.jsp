<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:standardPage module="amr" page="phaseDetect.readBetweenPhaseTest">
<cti:includeScript link="/JavaScript/yukon.ui.progressbar.js"/>

    <script type="text/javascript">

    var sendMsg = '<cti:msg2 key=".send" javaScriptEscape="true"/>';
    var sendingMsg = '<cti:msg2 key=".sending" javaScriptEscape="true"/>';
    var readingMsg = '<cti:msg2 key=".reading" javaScriptEscape="true"/>';
    var errorDetectMsg = '<cti:msg2 key=".errorDetect" javaScriptEscape="true"/>';
    var errorReadMsg = '<cti:msg2 key=".errorRead" javaScriptEscape="true"/>';
    function sendDetect () {
        var params = {'phase': jQuery('#phase').val()};

        jQuery('#sendDetectButton').val(sendingMsg);
        jQuery('#sendDetectButton').prop({'disabled': true});
        jQuery('#actionResultDiv').hide();
        jQuery('#spinner').show();

        jQuery.ajax({
            url: '/amr/phaseDetect/startTest',
            data: params,
            type: 'POST',
            dataType: 'json'
        }).done( function (data, textStatus, jqXHR) {
            if (data.errorOccurred) {
                jQuery('#spinner').hide();
                jQuery('#actionResultDiv').html(errorDetectMsg);
                jQuery('#actionResultDiv').show();
                jQuery('#sendDetectButton').val(sendMsg);
                jQuery('#sendDetectButton').prop({'disabled': false});
            } else {
                jQuery('#sendDetectButton').val(sendMsg);
                jQuery('#' + data.phase).show();
                jQuery('#spinner').hide();
                startTimers();
            }
        }).fail( function (jqXHR, textStatus) {
            jQuery('#spinner').hide();
            jQuery('#actionResultDiv').html(errorDetectMsg);
            jQuery('#actionResultDiv').show();
            jQuery('#sendDetectButton').val(sendMsg);
            jQuery('#sendDetectButton').prop({'disabled': false});
        });
    }

    function startTimers () {
        jQuery('#intervalTimerNote').show();
        jQuery('#intervalTimerFont').css('color', 'red');
        jQuery('#detectTimerNote').show();
        jQuery('#detectTimerFont').css('color', 'red');
        setTimeout(updateTimers, 1000);
    }

    function updateTimers () {
        var intervalTimer = jQuery('#intervalTimerSpan').html(),
            detectTimer = jQuery('#detectTimerSpan').html(),
            timeoutSet = false;
        if (intervalTimer > 0) {
            intervalTimer = intervalTimer - 1;
            jQuery('#intervalTimerSpan').html(intervalTimer);
            setTimeout(updateTimers, 1000);
            timeoutSet = true;
        }
        if (detectTimer > 0) {
            detectTimer = detectTimer - 1;
            jQuery('#detectTimerSpan').html(detectTimer);
            if (!timeoutSet) {
                setTimeout(updateTimers, 1000);
            }
        } else {
            jQuery('#sendDetectButton').hide();
            jQuery('#readButton').prop({'disabled': false});
            jQuery('#readButton').show();
        }
    }

    function reset () {
        jQuery('#sendDetectButton').val(sendMsg);
        jQuery('#sendDetectButton').prop({'disabled': false});
        jQuery('#sendDetectButton').show();
        jQuery('#resetButton').hide();
        jQuery('#actionResultDiv').hide();
        jQuery('#intervalTimerNote').hide();
        jQuery('#detectTimerNote').hide();
        jQuery('#intervalTimerSpan').html('${data.intervalLength}');
        jQuery('#intervalTimerFont').css('color', 'black');
        jQuery('#detectTimerSpan').html('${data.intervalLength * data.numIntervals}');
        jQuery('#detectTimerFont').css('color', 'black');
    }

    function sendRead () {
        var params = {'phase': jQuery('#phase').val()};
        jQuery('#actionResultDiv').show();
        jQuery.ajax({
            url: '/amr/phaseDetect/readPhase',
            type: 'POST',
            data: params
        }).done( function (data, textStatus, jqXHR) {
            var json = yukon.ui.util.getHeaderJSON(jqXHR);
            if (json.success) {
                jQuery('#read' + json.phase).show();
                jQuery('#readButton').val(readingMsg);
                jQuery('#readButton').prop({'disabled': true});
                jQuery('#actionResultDiv').html(data);
                if (json.complete) {
                    jQuery('#complete').val('true');
                }
            } else {
                jQuery('#actionResultDiv').show();
                jQuery('#actionResultDiv').html(errorReadMsg);
            }
        }).fail( function (jqXHR, textStatus) {
            jQuery('#actionResultDiv').html(errorReadMsg);
            jQuery('#actionResultDiv').show();
        });
    }

    function sendClearCommand () {
        jQuery('#spinner').show();
        jQuery('#actionResultDiv').html('');
        jQuery.ajax({
            url: '/amr/phaseDetect/sendClearFromTestPage',
            type: 'GET'
        }).done( function (data, textStatus, jqXHR) {
            var json = yukon.ui.util.getHeaderJSON(jqXHR);
            jQuery('#spinner').hide();
            jQuery('#actionResultDiv').show();
            if (json.success) {
                jQuery('#clearButton').hide();
                jQuery('#resetButton').show();
            }
        }).fail( function (jqXHR, textStatus) {
            jQuery('#spinner').hide();
            jQuery('#actionResultDiv').show();
        });
    }

    function readFinished () {
        jQuery('#cancelReadButton').prop({'disabled': true});
        jQuery('#readButton').val('${read}');
        jQuery('#readButton').hide();
        jQuery('#clearButton').prop({'disabled': false});
        jQuery('#clearButton').show();
        if (jQuery('#complete').val() == 'true') {
            jQuery('#resultsButton').prop({'disabled': false});
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
        <tags:nameValueContainer2 tableClass="with-form-controls">
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
            <tags:nameValue2 nameKey=".nextAction">
                <cti:msg2 var="clearPhaseData" key=".clearPhaseData"/>
                <c:set var="testStep" value="${state.testStep}"/>
            
                <div id="magicButtonDiv" style="float: left;padding-right: 10px;">
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
                <div id="actionResultDiv"  style="float: left;">
                    <c:if test="${showReadProgress}">
                        <jsp:include page="readPhaseResults.jsp"></jsp:include>
                    </c:if>
                </div>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    
    <div class="page-action-area">
        <form action="/amr/phaseDetect/phaseDetectResults" method="get">
            <cti:button nameKey="resultsButton" id="resultsButton" disabled="${testStep != 'results'}" type="submit" classes="primary action"/>
            <cti:button nameKey="cancelTest" name="cancel" type="submit"/>
        </form>
    </div>
</cti:standardPage>