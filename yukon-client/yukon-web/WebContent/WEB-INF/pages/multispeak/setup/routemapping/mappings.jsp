<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:standardPage title="Substation To Route Mappings"
    module="multispeak">
    <cti:standardMenu />
    <cti:includeScript link="/JavaScript/mspmappings.js" />

    <table style="margin: auto" cellpadding="25px">
        <tr>
            <td valign="top">
                <div id="sub_element">
                    <jsp:include
                        page="/spring/multispeak/setup/routemapping/substation">
                        <jsp:param name="view" value="" />
                    </jsp:include>
                </div>
            </td>
            <td valign="top">
                <div id="route_element">
                    <jsp:include
                        page="/spring/multispeak/setup/routemapping/route">
                        <jsp:param name="view" value="" />
                    </jsp:include>
                </div>
            </td>
        </tr>
    </table>

</cti:standardPage>
