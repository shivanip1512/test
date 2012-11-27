<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<cti:url var="routeUrl" value="/adminSetup/substations/routeMapping/viewRoute" />
<cti:url var="deleteUrl" value="/adminSetup/substations/routeMapping/removeRoute" />
<cti:url var="updateUrl" value="/adminSetup/substations/routeMapping/update" />

<b>Assigned Routes</b>
<form name="routeform" action="${routeUrl}" method="post">
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
            <td>
                <div style="padding-bottom: 5px">
                    <input type="button" value="&#9650" class="formSubmit" name="up"
                        onclick="javascript:yukonGeneral_moveOptionPositionInSelect(routeIdSelectList, -1)" />
                </div>
                <div>
                    <input type="button" value="&#9660" class="formSubmit" name="down"
                        onclick="javascript:yukonGeneral_moveOptionPositionInSelect(routeIdSelectList, 1)" />
                </div>
                <div style="padding-top: 55px">
                    <input type="button" value="Delete" class="formSubmit" name="removeRoute"
                        onclick="javascript:SubstationToRouteMappings_removeRoute()" />
                </div>
                <div style="padding-top: 15px">
                    <input id="update_button" type="button" class="formSubmit" value="Apply" name="Update"
                        onclick='javascript:SubstationToRouteMappings_updateRoutes("${updateUrl}")' />
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
            <td valign="bottom">
                <input type="button" value="Add" name="add" class="formSubmit"
                    onclick="javascript:SubstationToRouteMappings_addRoute()" />
            </td>
        </tr>
    </table>

</form>
