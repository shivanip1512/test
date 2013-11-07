<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="phaseDetect.routeSelection">
    <script type="text/javascript">
        function checkRoutes () {
            var button = jQuery('#nextButton'),
                checkBoxes = jQuery('input[id^="read_route_"]'),
                checkedCount = 0,
                i,
                checkBox;
            for (i = 0; i < checkBoxes.length; i += 1) {
                checkBox = checkBoxes[i];
                if (jQuery(checkBox).prop('checked')) {
                    checkedCount += 1;
                }
            }
            if (checkedCount < 1) {
                button.prop({'disabled': true});
                jQuery('#errorDiv').show();
            } else {
                jQuery('#errorDiv').hide();
                button.prop({'disabled': false});
            }
        }
    </script>
    
    <form action="/amr/phaseDetect/saveBroadcastRoutes" method="post">
        <tags:sectionContainer2 nameKey="selectBroadcastRoutes">
            <table style="padding-bottom: 5px;">
                <tr>
                    <td valign="top" class="strong-label-small">
                        <i:inline key=".noteLabel"/>
                    </td>
                    <td style="font-size:11px;">
                        <i:inline key=".noteText"/>
                    </td>
                </tr>
            </table>
            
            <div id="errorDiv" style="display: none;padding-bottom: 5px;">
                <span id="errorSpan" class="error">
                    <i:inline key=".noRouteSelected"/>
                </span>
            </div>
            
            <table class="results-table" style="padding-top: 5px;width: 10%;">
                <thead>
                    <tr>
                        <th nowrap="nowrap">
                            <i:inline key=".routeName"/>
                        </th>
                    </tr>
                </thead>
                <tbody> 
                    <c:forEach var="route" items="${routes}">
                        <tr>
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
    