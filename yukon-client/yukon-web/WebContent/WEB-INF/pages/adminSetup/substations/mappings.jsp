<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:standardPage page="substationToRouteMapping" module="adminSetup">
    
    <cti:includeScript link="/JavaScript/yukon.substation.route.mapping.js" />

    <table>
        <tr>
            
            <td class="vat">
                <div id="sub_element">
                    <jsp:include page="/admin/substations/routeMapping/viewSubstation">
                        <jsp:param name="view" value="" />
                    </jsp:include>
                </div>
            </td>
            
            <td style="width:50px;">&nbsp;</td>
            
            <td class="vat">
                <div id="route_element">
                    <jsp:include page="/admin/substations/routeMapping/viewRoute">
                        <jsp:param name="view" value="" />
                    </jsp:include>
                </div>
            </td>
            
        </tr>
    </table>

</cti:standardPage>