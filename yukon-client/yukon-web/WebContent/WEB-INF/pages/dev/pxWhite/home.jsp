<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="pxWhiteSimulator">
	    
    <cti:url var="updateSettingsUrl" value="updateSettings" />
	<form:form id="pxWhiteForm" action="${updateSettingsUrl}" modelAttribute="settings" method="post">
        <cti:csrfToken/>
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th>Endpoint</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="endpoint" items="${endpoints}" varStatus="status">
                    <tr>
                        <td><a href="${endpoint.doc}" target="_blank">${endpoint.suffix}</a></td>
                        <td>
                            <tags:selectWithItems path="selectedStatuses[${endpoint}]" items="${endpoint.statuses}"/>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <div class="page-action-area">
            <cti:button label="Update Settings" type="submit"/>
        </div>
	</form:form>
</cti:standardPage>