<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:standardPage page="substationToRouteMapping" module="adminSetup">
	
    <cti:includeScript link="/JavaScript/mspmappings.js" />
    <cti:includeScript link="/JavaScript/yukonGeneral.js"/>

    <table>
        <tr>
            
            <td valign="top">
                <div id="sub_element">
                    <jsp:include page="/spring/multispeak/setup/routemapping/substation">
                        <jsp:param name="view" value="" />
                    </jsp:include>
                </div>
            </td>
            
            <td style="width:50px;">&nbsp;</td>
            
            <td valign="top">
                <div id="route_element">
                    <jsp:include page="/spring/multispeak/setup/routemapping/route">
                        <jsp:param name="view" value="" />
                    </jsp:include>
                </div>
            </td>
            
        </tr>
    </table>

</cti:standardPage>
