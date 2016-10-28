<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="rfnTest.viewDataStreamingSimulator">
    <c:if test="${not simulatorRunning}">
        <form action="startDataStreamingSimulator">
            <cti:csrfToken/>
            
            <h3>Verification Response</h3>
            <input type="checkbox" name="isOverloadGatewaysOnVerification"/> Overload Gateways<br>
            <input type="checkbox" name="isNetworkManagerFailOnVerification"/> Network Manager Fail<br>
            <input type="checkbox" name="isDeviceErrorOnVerification">
            Device Error 
            <select name="deviceErrorOnVerification">
                <c:forEach var="deviceError" items="${deviceErrors}">
                    <option value="${deviceError}">${deviceError}</option>
                </c:forEach>
            </select>
            <br>
            
            <h3>Config Response</h3>
            <input type="checkbox" name="isOverloadGatewaysOnConfig"/> Overload Gateways<br>
            <input type="checkbox" name="isNetworkManagerFailOnConfig"/> Network Manager Fail<br>
            <input type="checkbox" name="isAcceptedWithError"/> Accepted With Error (Overloaded Gateways). Only for "re-send".<br>
            <input type="checkbox" name="isDeviceErrorOnConfig">
            Device Error 
            <select name="deviceErrorOnConfig">
                <c:forEach var="deviceError" items="${deviceErrors}">
                    <option value="${deviceError}">${deviceError}</option>
                </c:forEach>
            </select>
            <br><br>
            <cti:button label="Start Simulator" type="submit"/>
        </form>
    </c:if>
    <c:if test="${simulatorRunning}">
        <h3>Verification Response</h3>
        
        <c:choose>
            <c:when test="${settings.overloadGatewaysOnVerification}">
                <c:set var="checked" value="checked"/>
            </c:when>
            <c:otherwise>
                <c:set var="checked" value=""/>
            </c:otherwise>
        </c:choose>
        <input type="checkbox" name="isOverloadGatewaysOnVerification" disabled="disabled" ${checked}/> Overload Gateways<br>
        
        <c:choose>
            <c:when test="${settings.networkManagerFailOnVerification}">
                <c:set var="checked" value="checked"/>
            </c:when>
            <c:otherwise>
                <c:set var="checked" value=""/>
            </c:otherwise>
        </c:choose>
        <input type="checkbox" name="isNetworkManagerFailOnVerification" disabled="disabled" ${checked}/> Network Manager Fail<br>
        
        <c:choose>
            <c:when test="${not empty settings.deviceErrorOnVerification}">
                <c:set var="checked" value="checked"/>
            </c:when>
            <c:otherwise>
                <c:set var="checked" value=""/>
            </c:otherwise>
        </c:choose>
        <input type="checkbox" name="isDeviceErrorOnVerification" disabled="disabled" ${checked}>
        Device Error 
        <select name="deviceErrorOnVerification" disabled="disabled">
                <option value="${settings.deviceErrorOnVerification}">${settings.deviceErrorOnVerification}</option>
        </select>
        <br>
        
        <h3>Config Response</h3>
        <c:choose>
            <c:when test="${settings.overloadGatewaysOnConfig}">
                <c:set var="checked" value="checked"/>
            </c:when>
            <c:otherwise>
                <c:set var="checked" value=""/>
            </c:otherwise>
        </c:choose>
        <input type="checkbox" name="isOverloadGatewaysOnConfig" disabled="disabled" ${checked}/> Overload Gateways<br>
        
        <c:choose>
            <c:when test="${settings.networkManagerFailOnConfig}">
                <c:set var="checked" value="checked"/>
            </c:when>
            <c:otherwise>
                <c:set var="checked" value=""/>
            </c:otherwise>
        </c:choose>
        <input type="checkbox" name="isNetworkManagerFailOnConfig" disabled="disabled" ${checked}/> Network Manager Fail<br>
        
        <c:choose>
            <c:when test="${settings.acceptedWithError}">
                <c:set var="checked" value="checked"/>
            </c:when>
            <c:otherwise>
                <c:set var="checked" value=""/>
            </c:otherwise>
        </c:choose>
          <input type="checkbox" name="isAcceptedWithError" disabled="disabled" ${checked}/> Accepted With Error (Overloaded Gateways). Only for "re-send".<br>
        
        <c:choose>
            <c:when test="${not empty settings.deviceErrorOnConfig}">
                <c:set var="checked" value="checked"/>
            </c:when>
            <c:otherwise>
                <c:set var="checked" value=""/>
            </c:otherwise>
        </c:choose>
        <input type="checkbox" name="isDeviceErrorOnConfig" disabled="disabled" ${checked}>
        Device Error 
        <select name="deviceErrorOnConfig" disabled="disabled">
            <option value="${settings.deviceErrorOnConfig}">${settings.deviceErrorOnConfig}</option>
        </select>
        <br><br>
        <cti:button label="Stop Simulator" href="stopDataStreamingSimulator"/>
    </c:if>
</cti:standardPage>