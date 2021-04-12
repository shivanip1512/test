<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="pxMWSimulator">

<c:if test="${not empty userMessage}">
    <tags:alertBox type="success" includeCloseButton="true">${userMessage}</tags:alertBox>
</c:if>
     <div class="notes">
        <br/>For the Endpoint you would like to test using simulator, select the Status to be returned and enter any parameters that are needed as a comma separated string.
        <br/>Click on the Test button to test the Endpoint and the JSON result will be displayed below.
        <br/>The Clear Cache button will clear the cache.
        <br/>
        <br/>
        <br/>Configure the PX URL - Admin/Configuration/Demand Response
        <br/>PX URL:https://eas-all-apim-eus-dev.developer.azure-api.net
        <br/>Simulator URL: http://localhost:8080/yukon/dev/pxMiddleware
        <br/>
        <br/>Currently using <span class="fwb bg-color-grey txt-color-white">${urlType}</span>: ${url}
     </div>
     <br/>
	    
    <cti:url var="updateSettingsUrl" value="updateSettings"/>
	<form:form id="pxMWForm" action="${updateSettingsUrl}" modelAttribute="settings" method="post">
        <cti:csrfToken/>
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th>Endpoint</th>
                     <c:if test="${isLocalHost}">
                        <th>Simulated Status</th>
                    </c:if>
                    <th>Parameters</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="endpoint" items="${endpoints}" varStatus="status">
                    <tr>
                        <td style="width:200px" class="wbba"><a href="${endpoint.doc}" target="_blank">${endpoint.suffix}</a></td>
                        <c:if test="${isLocalHost}">
	                        <td>
	                            <tags:selectWithItems path="selectedStatuses[${endpoint}]" items="${endpoint.statuses}" inputClass="js-selected-status"/>
	                        </td>
	                    </c:if>
                        <td>
                            <c:if test="${not empty endpoint.params}">
                                <c:set var="params" value=""/>
                                <c:forEach var="endpointParam" items="${endpoint.params}">
                                    <c:set var="params" value="${not empty params ? params += ',' += endpointParam.value : endpointParam.value}"/>
                                </c:forEach>
                                <input type="text" id="${endpoint}_parameters" size="40" value="${params}"/>
                                <cti:uniqueIdentifier var="uid" prefix="parameter-help-"/>
                                <cti:icon icon="icon-help" classes="fn" data-popup="#${uid}"/>
                                <div id="${uid}" data-title="Parameters Help" data-width="500" class="dn">
                                    <c:set var="containsOptionalParams" value="false"/>
                                    <b>Parameters must be a comma separated string.</b><br/>
                                    <b>Parameters text box must contain valid:</b><br/>
                                    <c:forEach var="testParam" items="${endpoint.params}">
                                        <c:choose>
                                            <c:when test="${!fn:contains(testParam.key, '*')}">
                                                <span class="ML15">- ${testParam.key}</span><br/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="containsOptionalParams" value="true"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                    <c:if test="${containsOptionalParams}">
                                        <b>Optional Parameters are as follows:</b><br/>
                                        <c:forEach var="testParam" items="${endpoint.params}">
                                            <c:if test="${fn:contains(testParam.key, '*')}">
                                                <span class="ML15">- ${testParam.key}</span><br/>
                                            </c:if>
                                        </c:forEach>
                                    </c:if>
                                </div>
                            </c:if>
                        </td>
                        <td>
                            <cti:button label="Test" classes="js-test-endpoint" data-endpoint="${endpoint}" data-params="${params}"/>
                            <c:if test="${endpoint == 'SECURITY_TOKEN'}">
                                <cti:button label="Clear Cache" classes="js-clear-cache"/>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
	</form:form>

    <br/>
    <pre class="js-test-endpoint-results dn"></pre>
    
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.pxMWSimulator.js" />
</cti:standardPage>