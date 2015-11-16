<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="amr" page="phaseDetect.home">
    
    <cti:url var="routesUrl" value="/amr/phaseDetect/routes"/>

    <script type="text/javascript">
        function selectThisSub() {
            var selection = $('#substations').val(),
                params = {'substationId': selection};
            $('#selectedSub').val(selection);
            $.ajax({
                url: '${routesUrl}',
                type: 'GET',
                data: params
            }).done( function (data, textStatus, jqXHR) {
                $('#routesDiv').html(data);
                checkRoutes();
            });
        }

        function checkRoutes () {
            var button = $('#nextButton'),
                checkBoxes = $('input[id^="read_route_"]'),
                checkedCount = 0,
                numOfRoutes,
                i,
                checkBox,
                selection;

            numOfRoutes = checkBoxes.length;
            if (numOfRoutes > 0) { 
                /* Check to see if they have any selected */
                for (i = 0; i < checkBoxes.length; i += 1) {
                    checkBox = checkBoxes[i];
                    if ($(checkBox).prop('checked')) {
                        checkedCount += 1;
                    }
                }
                if (checkedCount < 1) {
                    button.prop({'disabled': true});
                    $('#subWithNoRoutesSelectedErrorDiv').hide();
                    $('#noRouteSelectedErrorDiv').show();
                } else {
                    $('#noRouteSelectedErrorDiv').hide();
                    $('#subWithNoRoutesSelectedErrorDiv').hide();
                    button.prop({'disabled': false});
                }
            } else {
                /* If they have a sub selected show error msg */
                button.prop({'disabled': true});
                selection = $('#substations').val(); 
                if (selection == '-1') {
                    $('#noRouteSelectedErrorDiv').hide();
                    $('#subWithNoRoutesSelectedErrorDiv').hide();
                } else {
                    $('#noRouteSelectedErrorDiv').hide();
                    $('#subWithNoRoutesSelectedErrorDiv').show();
                }
            }
        }
    </script>

    <cti:url var="saveSubstationAndReadMethodUrl" value="/amr/phaseDetect/saveSubstationAndReadMethod"/>
    <form action="${saveSubstationAndReadMethodUrl}" method=post>
        <cti:csrfToken/>
        <tags:sectionContainer2 nameKey="selectSubstation">
            <table style="padding-bottom: 5px;">
                <tr>
                    <td valign="top" class="strong-label-small" style="padding-right: 5px;"><span class="error">
                        <i:inline key=".noteLabel"/>
                    </span></td>
                    <td style="font-size:11px;"><span>
                        <i:inline key=".noteText"/>
                    </span></td>
                </tr>
            </table>
            <input type="hidden" name="selectedSub" id="selectedSub" value="-1">
            
            <%-- Error Divs --%>
            <div id="noRouteSelectedErrorDiv" style="display: none;">
                <span id="errorSpan" class="error">
                    <i:inline key=".noRouteSelected"/>
                </span>
            </div>
            <div id="subWithNoRoutesSelectedErrorDiv" style="display: none;">
                <span id="errorSpan" class="error">
                    <i:inline key=".subWithNoRoutesSelected"/>
                </span>
            </div>
            
            <table style="padding-right: 20px;padding-bottom: 10px;">
                <tr valign="top">
                    <td style="padding-top: 3px;">
                       <tags:nameValueContainer2>
                           <tags:nameValue2 nameKey=".substation">
                               <select id="substations" onchange="selectThisSub();">
                                   <option value="-1">(none)</option>
                                   <c:forEach var="substation" items="${substations}">
                                       <option value="${substation.id}">${fn:escapeXml(substation.name)}</option>
                                   </c:forEach>
                               </select>
                           </tags:nameValue2>
                       </tags:nameValueContainer2>
                    </td>
                </tr>
                <tr>
                    <td style="padding-top: 3px;">
                        <div id="routesDiv"></div>
                    </td>
                </tr>
                <tr>
                    <td style="padding-top: 3px;">
                        <cti:msg2 key=".readAfterTitle" var="readAfterTitle"/>
                        <label>
                            <input type="radio" name="readPhasesWhen" value="after" title="${readAfterTitle}" checked="checked">
                            <i:inline key=".readMetersAfterAllTests"/>
                        </label>
                    </td>
                </tr>
                <tr>
                    <td style="padding-top: 3px;">
                        <cti:msg2 key=".readBetweenTitle" var="readBetweenTitle"/>
                        <label>
                            <input type="radio" name="readPhasesWhen" value="between" title="${readBetweenTitle}">
                            <i:inline key=".readMetersBetweenTests"/>
                        </label>
                    </td>
                </tr>
            </table>
        </tags:sectionContainer2>
        <div class="page-action-area">
            <cti:button id="nextButton" nameKey="next" name="next" type="submit" disabled="true" classes="primary action"/>
        </div>
    </form>
</cti:standardPage>