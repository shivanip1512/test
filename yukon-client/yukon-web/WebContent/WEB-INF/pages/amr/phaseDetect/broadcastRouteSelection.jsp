<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="phaseDetect.routeSelection">
    <script type="text/javascript">
        function checkRoutes () {
            var button = $('#nextButton'),
                checkBoxes = $('input[id^="read_route_"]'),
                checkedCount = 0,
                i,
                checkBox;
            for (i = 0; i < checkBoxes.length; i += 1) {
                checkBox = checkBoxes[i];
                if ($(checkBox).prop('checked')) {
                    checkedCount += 1;
                }
            }
            if (checkedCount < 1) {
                button.prop({'disabled': true});
                $('#errorDiv').show();
            } else {
                $('#errorDiv').hide();
                button.prop({'disabled': false});
            }
        }
    </script>
    
    <cti:url var="saveBroadcastRoutesUrl" value="/amr/phaseDetect/saveBroadcastRoutes"/>
    <form action="${saveBroadcastRoutesUrl}" method="post">
        <cti:csrfToken/>
        <tags:sectionContainer2 nameKey="selectBroadcastRoutes">
            <table class="stacked">
                <tr>
                    <td class="strong-label-small"><i:inline key=".noteLabel"/></td>
                    <td><span class="notes"><i:inline key=".noteText"/></span></td>
                </tr>
            </table>
            
            <div id="errorDiv" class="dn stacked">
                <span id="errorSpan" class="error">
                    <i:inline key=".noRouteSelected"/>
                </span>
            </div>
            
            <h3><i:inline key=".routeName"/></h3>
            <ul class="simple-list">
                <c:forEach var="route" items="${routes}">
                    <li><label><input id="read_route_${route.id}" name="read_route_${route.id}" checked="checked" type="checkbox" onclick="checkRoutes()">${route.name}</label></li>
                </c:forEach>
            </ul>
        </tags:sectionContainer2>
        
        <div class="page-action-area">
            <cti:button nameKey="next" type="submit" id="nextButton" classes="primary action"/>
            <cti:button nameKey="cancelTest" type="submit" name="cancel"/>
        </div>
    </form>
    
</cti:standardPage>