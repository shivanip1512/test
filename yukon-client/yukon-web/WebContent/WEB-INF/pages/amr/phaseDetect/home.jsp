<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="phaseDetect.home">
    
    <cti:includeCss link="/WebConfig/yukon/styles/yukon.css"/>

    <cti:url var="routesUrl" value="/spring/amr/phaseDetect/routes"/>

	<script type="text/javascript">
        function selectThisSub() {
		    var selection = $F('substations');
		    $("selectedSub").value = selection;
		    var button = $('nextButton');
		    var params = {'substationId': selection};
		    
		    new Ajax.Updater('routesDiv', '${routesUrl}', {method: 'get', evalScripts: true, parameters: params,
		    	onComplete: function(resp, json) {
                    checkRoutes();
                }
            });
        }

        function checkRoutes(){
        	var button = $('nextButton');
			var checkBoxes = $$('input[id^="read_route_"]');
			var checkedCount = 0;

			var numOfRoutes = checkBoxes.length;
			if(numOfRoutes > 0) { 
                /* Check to see if they have any selected */
    			for (i = 0; i < checkBoxes.length; i++) {
    				var checkBox = checkBoxes[i];
    				if(checkBox.checked){
    					checkedCount++;
    				}
    			}
    			if(checkedCount < 1) {
    				button.disable();
    				$('subWithNoRoutesSelectedErrorDiv').hide();
    				$('noRouteSelectedErrorDiv').show();
    			} else {
    				$('noRouteSelectedErrorDiv').hide();
    				$('subWithNoRoutesSelectedErrorDiv').hide();
    				button.enable();
    			}
			} else {
			    /* If they have a sub selected show error msg */
				button.disable();
                var selection = $F('substations'); 
                if(selection == '-1'){
                    $('noRouteSelectedErrorDiv').hide();
                    $('subWithNoRoutesSelectedErrorDiv').hide();
                } else {
                	$('noRouteSelectedErrorDiv').hide();
                    $('subWithNoRoutesSelectedErrorDiv').show();
                }
			}
        }
	</script>

    <form action="/spring/amr/phaseDetect/saveSubstationAndReadMethod" method=post>
        <tags:sectionContainer2 nameKey="selectSubstation">
            <table style="padding-bottom: 5px;">
                <tr>
                    <td valign="top" class="smallBoldLabel" style="padding-right: 5px;"><span class="errorMessage">
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
		        <span id="errorSpan" class="errorMessage">
                    <i:inline key=".noRouteSelected"/>
                </span>
            </div>
            <div id="subWithNoRoutesSelectedErrorDiv" style="display: none;">
                <span id="errorSpan" class="errorMessage">
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
			                           <option value="${substation.id}">${substation.name}</option>
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
                        <input type="radio" name="readPhasesWhen" value="after" 
                            title="${readAfterTitle}" 
                            checked="checked">
                            <i:inline key=".readMetersAfterAllTests"/>
                    </td>
                </tr>
		        <tr>
	                <td style="padding-top: 3px;">
                        <cti:msg2 key=".readBetweenTitle" var="readBetweenTitle"/>
	                    <input type="radio" name="readPhasesWhen" value="between" 
                            title="${readBetweenTitle}">
                            <i:inline key=".readMetersBetweenTests"/>
	                </td>
		        </tr>
		    </table>
        </tags:sectionContainer2>
        <cti:button id="nextButton" nameKey="next" name="next" type="submit" disabled="true" />
	</form>
</cti:standardPage>