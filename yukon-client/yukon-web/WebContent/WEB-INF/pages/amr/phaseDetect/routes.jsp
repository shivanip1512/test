<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<tags:standardPageFragment module="amr" pageName="phaseDetect.home" fragmentName="routes">
    <c:if test="${show}">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".devicesSelected">${currentSubstation}</tags:nameValue2>
            <tags:nameValueGap2 gapHeight="3px"/>
            <tags:nameValue2 nameKey=".deviceCount">${deviceCount}</tags:nameValue2>
        </tags:nameValueContainer2>
        <br>
        <table class="resultsTable" style="padding-top: 5px;">
            <thead>
                <tr>
                    <th nowrap="nowrap">
                        <i:inline key=".routeName"/>
                    </th>
                    <th>
                        <i:inline key=".pageDescription"/>
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
</tags:standardPageFragment>