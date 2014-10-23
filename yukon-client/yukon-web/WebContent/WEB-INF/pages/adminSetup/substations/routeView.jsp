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
    <div class="column-18-6 clearfix">
        <div class="column one">
            <div class="stacked-md">
                <select id="routeIdSelectList" name="routeid" size="10" style="width:200px">
                    <c:forEach var="route" items="${list}">
                        <option value="${route.id}">
                            ${route.name}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <span>Available Routes</span>
                <select id="avRoutesSelectList" name="avroutes" style="width:160px">
                    <c:forEach var="avroute" items="${avlist}">
                        <option value="${avroute.id}">
                            ${fn:escapeXml(avroute.name)}
                        </option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="column two nogutter clearfix pr">
            <div>
                <div class="pa T0">
                    <div class="clearfix">
                        <cti:button renderMode="buttonImage" icon="icon-bullet-go-up" onclick="yukon.ui.util.yukonGeneral_moveOptionPositionInSelect(routeIdSelectList, -1);"/>
                    </div>
                    <div class="clearfix stacked" >
                        <cti:button renderMode="buttonImage" icon="icon-bullet-go-down" onclick="yukon.ui.util.yukonGeneral_moveOptionPositionInSelect(routeIdSelectList, 1);"/>
                    </div>
                </div>
                <div class="pa" style="top:112px;">
                    <div class="clearfix stacked">
                        <cti:button nameKey="delete" onclick="SubstationToRouteMappings_removeRoute()"/>
                    </div>
                    <div class="clearfix">
                        <cti:button label="Apply" onclick="SubstationToRouteMappings_updateRoutes('${updateUrl}')"/>
                    </div>
                </div>
            </div>
            <div class="pa" style="top:212px;">
                <cti:button nameKey="add" onclick="SubstationToRouteMappings_addRoute();"/>
            </div>
        </div>
       
    </div>
</form>
</cti:msgScope>