<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="pxMWSimulator">

<style>
.w100 {
    width:100px;
}
</style>

<c:if test="${not empty userMessage}">
    <tags:alertBox type="success" includeCloseButton="true">${userMessage}</tags:alertBox>
</c:if>
     <div class="notes">
        <br/>For the Endpoint you would like to test using simulator, select the Status to be returned and enter any parameters that are needed as a comma separated string.
        <br/>Default JSON values are defined in yukon.dev.simulators.pxMWSimulator.js
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
	                            <tags:selectWithItems path="selectedStatuses[${endpoint}]" items="${endpoint.statuses}" inputClass="js-selected-status w100"/>
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
                            <c:if test="${endpoint.hasJsonParam()}">
                                <cti:button label="Show/Hide JSON" classes="js-enter-json fn ML0" data-endpoint="${endpoint}"/>
                                <div><textarea id="${endpoint}_json" cols="60" rows="10" class="dn js-json-text" data-endpoint="${endpoint}"></textarea></div>
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

    <div class="autoCreationNotes">
        <br/>This is for describing the functionality of the PxMW Auto Creation feature
        <br/>Select a device type and a number of devices to create then press submit
    </div>
    <br/>

    <cti:url var="autoCreationUrl" value="deviceAutoCreation"/>
    <form:form id="autoCreationForm" action="${autoCreationUrl}" method="post">
        <tags:nameValueContainer>
            <tags:nameValue name="PXMW Cellular LCR Auto Creation" nameColumnWidth="250px">
                <select name="paoType">
                    <c:forEach var="type" items="${autoCreationTypes}">
                        <option value="${type}"><cti:msg2 key="${type.formatKey}"/></option>
                    </c:forEach>
                </select>
                <input type="text" name="textInput" value="10"/>
                <cti:button label="Submit" classes="js-auto-creation-submit fn vam"/>
            </tags:nameValue>
        </tags:nameValueContainer>
    </form:form>

    <pre class="js-test-endpoint-results dn"></pre>
    
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.pxMWSimulator.js" />
</cti:standardPage>