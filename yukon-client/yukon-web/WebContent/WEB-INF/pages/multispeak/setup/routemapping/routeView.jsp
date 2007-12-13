<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="routeUrl"
    value="/spring/multispeak/setup/routemapping/route" />

Assigned Routes
<form name="routeform" action="${routeUrl}" method="post">
    <table>
        <tr>
            <td>
                <select id="routeIdSelectList" name="routeid" size="10"
                    style="width:185px">
                    <c:forEach var="route" items="${list}">
                        <option value="${route.id}">
                            ${route.name}
                        </option>
                    </c:forEach>
                </select>
            </td>
            <td>
                <div style="padding-bottom: 5px">
                    <input type="button" value="&#9650" size="5"
                        name="up"
                        onclick="javascript:yukonGeneral_moveOptionPositionInSelect(routeIdSelectList, -1)" />
                </div>
                <div>
                    <input type="button" value="&#9660" size="5"
                        name="down"
                        onclick="javascript:yukonGeneral_moveOptionPositionInSelect(routeIdSelectList, 1)" />
                </div>
                <div style="padding-top: 65px">
                    <input type="button" value="Delete" size="5"
                        name="remove"
                        onclick="javascript:SubstationToRouteMappings_removeRoute()" />
                </div>
                <div style="padding-top: 5px">
                    <input id="update_button" type="button"
                        value="Apply" size="5" name="Update"
                        onclick='javascript:SubstationToRouteMappings_updateRoutes("${routeUrl}")' />
                </div>
            </td>
        </tr>
        <tr>
            <td>
                Available Routes<br>
                <select id="avRoutesSelectList" name="avroutes"
                    style="width:185px">
                    <c:forEach var="avroute" items="${avlist}">
                        <option value="${avroute.id}">
                            ${avroute.name}
                        </option>
                    </c:forEach>
                </select>
            </td>
            <td valign="bottom">
                <input type="button" value="Add" size="5" name="add"
                    onclick="javascript:SubstationToRouteMappings_addRoute()" />
            </td>
        </tr>
    </table>

</form>
