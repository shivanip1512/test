<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="pxMWSimulator">
	    
    <cti:url var="updateSettingsUrl" value="updateSettings"/>
	<form:form id="pxMWForm" action="${updateSettingsUrl}" modelAttribute="settings" method="post">
        <cti:csrfToken/>
        <table class="compact-results-table">
            <div>Admin/Configuration/Demand Response PX URL: http://localhost:8080/yukon/dev/PxMW (dev)</div>
            <div>&nbsp</div>
            <thead>
                <tr>
                    <th>Endpoint</th>
                    <th>Status</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="endpoint" items="${endpoints}" varStatus="status">
                    <tr>
                        <td><a href="${endpoint.doc}" target="_blank">${endpoint.suffix}</a></td>
                        <td>
                            <tags:selectWithItems path="selectedStatuses[${endpoint}]" items="${endpoint.statuses}" inputClass="js-selected-status"/>
                        </td>
                        <td>
                            <cti:button label="Test" classes="js-test-endpoint" data-endpoint="${endpoint}"/>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
	</form:form>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.pxMWSimulator.js" />
</cti:standardPage>