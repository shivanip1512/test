<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="phaseDetect.routeSelection">
    <script type="text/javascript">
        function checkRoutes(){
        	var button = $('nextButton');
			var checkBoxes = $$('input[id^="read_route_"]');
			var checkedCount = 0;
			for (i = 0; i < checkBoxes.length; i++) {
				var checkBox = checkBoxes[i];
				if(checkBox.checked){
					checkedCount++;
				}
			}
			if(checkedCount < 1) {
				button.disable();
				$('errorDiv').show();
			} else {
				$('errorDiv').hide();
				button.enable();
			}
        }
	</script>
    
    <form action="/amr/phaseDetect/saveBroadcastRoutes" method="post">
        <tags:sectionContainer2 nameKey="selectBroadcastRoutes">
            <table style="padding-bottom: 5px;">
                <tr>
                    <td valign="top" class="smallBoldLabel">
                        <i:inline key=".noteLabel"/>
                    </td>
                    <td style="font-size:11px;">
                        <i:inline key=".noteText"/>
                    </td>
                </tr>
            </table>
            
            <div id="errorDiv" style="display: none;padding-bottom: 5px;">
		        <span id="errorSpan" class="errorMessage">
                    <i:inline key=".noRouteSelected"/>
                </span>
            </div>
            
            <table class="resultsTable" style="padding-top: 5px;width: 10%;">
                <thead>
                    <tr>
                        <th nowrap="nowrap">
                            <i:inline key=".routeName"/>
                        </th>
                    </tr>
                </thead>
                <tbody> 
                    <c:forEach var="route" items="${routes}">
                        <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
                            <td nowrap="nowrap" style="padding-right: 10px;">
                                <input id="read_route_${route.id}" name="read_route_${route.id}" checked="checked" type="checkbox" onclick="checkRoutes()">
                                ${route.name}
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
                
        </tags:sectionContainer2>
        <cti:button nameKey="cancelTest" type="submit" name="cancel"/>
        <cti:button nameKey="next" type="submit" id="nextButton"/>
    </form>
</cti:standardPage>
    