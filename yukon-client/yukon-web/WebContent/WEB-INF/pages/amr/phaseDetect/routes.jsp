<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
                    Route Name
                </th>
                <th nowrap="nowrap">
                    Devices
                </th>
            </tr>
        </thead>
	    <tbody> 
		    <c:forEach var="route" items="${routes}">
		        <tr class="<tags:alternateRow even="altTableCell" odd="tableCell"/>">
		            <td nowrap="nowrap" style="padding-right: 10px;">
		                ${route.name}
		            </td>
		            <td nowrap="nowrap">
	                    ${routeSizeMap[route.id]}
		            </td>
		        </tr>
		    </c:forEach>
	    </tbody>
	</table>
    <br>
</c:if>