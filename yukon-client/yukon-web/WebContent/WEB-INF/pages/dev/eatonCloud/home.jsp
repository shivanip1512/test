<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="eatonCloudSimulator">

<style>
.w100 {
    width:100px;
}
.PL5 {
	padding-left:5px;
}
</style>

<c:if test="${not empty userMessage}">
    <tags:alertBox type="success" includeCloseButton="true">${userMessage}</tags:alertBox>
</c:if>
     <div class="notes">
        <br/>For the Endpoint you would like to test using simulator, select the Status to be returned and enter any parameters that are needed as a comma separated string.
        <br/>Default JSON values are defined in yukon.dev.simulators.eatonCloudSimulator.js
        <br/>Click on the Test button to test the Endpoint and the JSON result will be displayed below.
        <br/>The Clear Cache button will clear the cache.
        <br/>
        <br/>Configure the Brightlayer Utilities Suite Demand Response URL - Admin/Configuration/Demand Response
        <br/>Brightlayer Utilities Suite Demand Response URL:https://eas-dev.eastus.cloudapp.azure.com/api
        <br/>Simulator URL local: http://localhost:8080/yukon/dev/api
        <br/>Simulator URL QA: http://localhost:8080/dev/api
        <br/>
        <br/>Currently using <span class="fwb bg-color-grey txt-color-white">${urlType}</span>: ${url}
     </div><br/>
     <tags:alertBox classes="js-success-message dn" type="success" includeCloseButton="true"></tags:alertBox>
     <div class="js-secret-information user-message info dn">
     	Token cached by Service Manager: <span class="js-cached-token">${cachedToken}</span>
     	<br/>
        Token1:<span class="js-secret1Token">${secret1Token}</span> cached by <span class="js-cachedBy">${cachedBy}</span> 
        Secret1: <span class="js-secret1">${secret1}</span> Expiration: <span class="js-secret1Expiration"></span>
        <br/>
        Token2:<span class="js-secret2Token">${secret2Token}</span> cached by <span class="js-cachedBy">${cachedBy}</span> 
        Secret2: <span class="js-secret2">${secret2}</span> Expiration: <span class="js-secret2Expiration"></span>
     </div>
     <br/>
	    
    <cti:url var="updateSettingsUrl" value="updateSettings"/>
	<form:form id="eatonCloudForm" action="${updateSettingsUrl}" modelAttribute="settings" method="post">
        <cti:csrfToken/>
        <table class="compact-results-table no-stripes">
            <thead>
                <tr>
                    <th>Endpoint</th>
                     <c:if test="${isLocalHost}">
                        <th style="width:100px;">Simulated Status</th>
                    </c:if>
                    <th>Parameters</th>
                    <th width="15%"></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="endpoint" items="${endpoints}" varStatus="status">
                    <tr>
                        <td style="width:200px" class="wbba"><a href="${endpoint.doc}" target="_blank">${endpoint.suffix}</a></td>
                        <c:if test="${isLocalHost}">
	                        <td>
	                            <tags:selectWithItems path="selectedStatuses[${endpoint}]" items="${endpoint.statuses}" inputClass="js-selected-status w100"/>
	                            <c:if test="${endpoint.displaySuccessPercentage()}">
	                            	<c:set var="dnClass" value="${settings.selectedStatuses[endpoint] != 'OK' ? 'dn' : ''}"/>
	                            	<div class="js-success-percentage-fields ${dnClass}">
		                            	<tags:input path="successPercentages[${endpoint}]" size="3" inputClass="js-success-percentage" units="%"/>
		                            	<cti:icon icon="icon-help" classes="fn cp ML0 MR0" data-popup="#successPercentageHelp"/>
	                                	<div id="successPercentageHelp" data-title="Success Percentage" data-width="500" class="dn">
	                                		The percentage of responses that should return a success.
	                                	</div>
                                	</div>
	                            </c:if>
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
                                <cti:icon icon="icon-help" classes="fn cp ML0 MR0" data-popup="#${uid}"/>
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
                            <c:if test="${endpoint.hasJsonParam()}">
                                <cti:button label="Show/Hide JSON" classes="js-enter-json fn ML0 MR0 PR5 PL5" data-endpoint="${endpoint}"/>
                                <div><textarea id="${endpoint}_json" cols="60" rows="10" class="dn js-json-text" data-endpoint="${endpoint}"></textarea></div>
                            </c:if>
                        </td>
                        <td>
                        	<c:if test="${endpoint.showTestButton()}">
                            	<cti:button label="Test" classes="js-test-endpoint MR0" data-endpoint="${endpoint}" data-params="${params}"/>
                            </c:if>
                            <c:if test="${endpoint == 'SECURITY_TOKEN'}">
                                <cti:button label="Clear" classes="js-clear-cache MR0"/>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
	</form:form>

    <br/>
    
    <pre class="code js-test-endpoint-results dn"></pre>
    
    
      <div class="notes">
        <br/>Creates devices by using auto creation service.
        <br/>Setup Development Database can be also used to create LCRs and add them to the account if the simulator service is running.
     </div>
    <br/>
    <cti:url var="autoCreationUrl" value="deviceAutoCreation"/>
    <form:form id="autoCreationForm" action="${autoCreationUrl}" method="post">
        <tags:nameValueContainer>
            <tags:nameValue name="Device Creation" nameColumnWidth="250px">
                <select name="paoType">
                    <c:forEach var="type" items="${autoCreationTypes}">
                        <option value="${type}"><cti:msg2 key="${type.formatKey}"/></option>
                    </c:forEach>
                </select>
                <input type="text" name="textInput" value="10"/>
                <cti:button label="Submit" type="submit" classes="fn vam"/>
                <form:form id="autoCreationForm" action="${autoCreationUrl}" method="post"/>
                <cti:csrfToken/>
            </tags:nameValue>
        </tags:nameValueContainer>
    </form:form>


	<br />
	<cti:url var="rotateSecretsUrl" value="rotateSecrets" />
	<form:form id="rotateSecretsForm" action="${rotateSecretsUrl}"
		method="post">
		<tags:nameValueContainer>
			<cti:button label="Start Automatic Secret Rotation" type="submit" />
			<form:form id="rotateSecretsForm" action="${rotateSecretsUrl}"
				method="post" />
			<cti:csrfToken />
		</tags:nameValueContainer>
	</form:form>
	<br />
	<cti:url var="validateSecretsUrl" value="validateSecrets" />
	<form:form id="validateSecretsForm" action="${rotateSecretsUrl}"
		method="post">
		<tags:nameValueContainer>
			<cti:button label="Start Automatic Secret Validation" type="submit" />
			<form:form id="validateSecretsForm" action="${validateSecretsUrl}"
				method="post" />
			<cti:csrfToken />
		</tags:nameValueContainer>
	</form:form>
	<br />
	<cti:url var="autoReadUrl" value="deviceAutoRead" />
	<form:form id="autoReadForm" action="${autoReadUrl}" method="post">
		<tags:nameValueContainer>
			<cti:button label="Read all Eaton Cloud LCRs" type="submit" />
			<form:form id="autoReadForm" action="${autoReadUrl}" method="post" />
			<cti:csrfToken />
		</tags:nameValueContainer>
	</form:form>

	<br />
	<cti:url var="forceRuntimeCalcUrl" value="forceRuntimeCalc" />
	<form:form id="forceRuntimeCalcForm" action="${forceRuntimeCalcUrl}"
		method="post">
		<tags:nameValueContainer>

			<cti:button label="Force Runtime Calculation" type="submit" />
			<form:form id="forceRuntimeCalcForm" action="${forceRuntimeCalcUrl}"
				method="post" />
			<cti:csrfToken />

		</tags:nameValueContainer>
	</form:form>

	<cti:includeScript
			link="/resources/js/pages/yukon.dev.simulators.eatonCloudSimulator.js" />
</cti:standardPage>