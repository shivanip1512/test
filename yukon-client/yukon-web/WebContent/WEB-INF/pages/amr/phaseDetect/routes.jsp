<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:if test="${show}">
    <tags:nameValueContainer>
        <tags:nameValue name="Devices Selected" nameColumnWidth="150px">${currentSubstation}</tags:nameValue>
        <tags:nameValueGap gapHeight="3px"/>
        <tags:nameValue name="Device Count" nameColumnWidth="150px">${deviceCount}</tags:nameValue>
    </tags:nameValueContainer>
    <br>
    <table class="resultsTable" style="padding-top: 5px;">
        <thead>
            <tr>
                <th nowrap="nowrap">
                    <i:inline key="yukon.web.modules.amr.phaseDetect.routeName"/>
                </th>
                <th>
                    <i:inline key="yukon.web.modules.amr.phaseDetect.devices"/>
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
		            <td>
	                    ${routeSizeMap[route.id]}
		            </td>
		        </tr>
		    </c:forEach>
	    </tbody>
	</table>
    <br>
</c:if>