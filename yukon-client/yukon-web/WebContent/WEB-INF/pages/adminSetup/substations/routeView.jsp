<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.adminSetup.substationToRouteMapping">

<cti:url var="routeUrl" value="/adminSetup/substations/routeMapping/viewRoute" />
<cti:url var="deleteUrl" value="/adminSetup/substations/routeMapping/removeRoute" />
<cti:url var="updateUrl" value="/adminSetup/substations/routeMapping/update" />

<b>Assigned Routes</b>
<form name="routeform" class="pr" action="${routeUrl}" method="post">
    <cti:csrfToken/>
    <table>
        <tr>
            <td>
                <select id="routeIdSelectList" name="routeid" size="10" style="width:200px">
                    <c:forEach var="route" items="${list}">
                        <option value="${route.id}">
                            ${route.name}
                        </option>
                    </c:forEach>
                </select>
            </td>
            <td class="vab">
                <div class="pa T0">
	                <div style="padding-bottom: 5px">
                        <cti:button renderMode="buttonImage" icon="icon-bullet-go-up" onclick="yukon.ui.util.yukonGeneral_moveOptionPositionInSelect(routeIdSelectList, -1);"/>
	                </div>
	                <div>
                        <cti:button renderMode="buttonImage" icon="icon-bullet-go-down" onclick="yukon.ui.util.yukonGeneral_moveOptionPositionInSelect(routeIdSelectList, 1);"/>
	                </div>
                </div>
                <div style="padding-top: 55px">
                    <cti:button nameKey="delete" onclick="SubstationToRouteMappings_removeRoute()"/>
                </div>
                <div style="padding-top: 15px">
                    <cti:button label="Apply" onclick="SubstationToRouteMappings_updateRoutes('${updateUrl}')"/>
                </div>
            </td>
        </tr>
        
        <tr><td><div style="height:20px;"></div></td></tr>
        
        <tr>
            <td>
                Available Routes<br>
                <select id="avRoutesSelectList" name="avroutes"
                    style="width:200px">
                    <c:forEach var="avroute" items="${avlist}">
                        <option value="${avroute.id}">
                            ${fn:escapeXml(avroute.name)}
                        </option>
                    </c:forEach>
                </select>
            </td>
            <td class="vab">
                <cti:button nameKey="add" onclick="SubstationToRouteMappings_addRoute();"/>
            </td>
        </tr>
    </table>

</form>
</cti:msgScope>