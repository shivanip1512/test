<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div>
    <div class="widgetInternalSectionHeader">
        Account Information
    </div>
    <div class="widgetInternalSection">
        <c:if test='${info.firstName != null && info.firstName != null}'>
            ${info.firstName} ${info.lastName} <br>
        </c:if>
        <tags:notNullDataLine value="${info.address.locationAddress1}"/>
        <tags:notNullDataLine value="${info.address.locationAddress2}"/>
        <c:if test='${info.address.cityName != null || info.address.stateCode != null || info.address.zipCode != null}'>
	        <c:if test='${info.address.cityName != null}'>
	                ${info.address.cityName},
	        </c:if>
			${info.address.stateCode} ${info.address.zipCode}
	        <c:if test='${infoMapURL != null}'>
	            (<a href="${infoMapURL}" target="_blank">map</a>)
	        </c:if>
	    </c:if>
    </div>
    <div class="widgetInternalSectionHeader">
        Service Location
    </div>
    <div class="widgetInternalSection">
        <tags:nameValueContainer>
            <c:if test='${serviceInfo.customerNumber != null}'>
                <tags:nameValue name="CustomerNumber">${serviceInfo.customerNumber}</tags:nameValue>
            </c:if>
            <c:if test='${serviceInfo.accountNumber != null}'>
                <tags:nameValue name="AccountNumber">${serviceInfo.accountNumber}</tags:nameValue>
            </c:if>
            <c:if test='${serviceInfo.siteNumber != null}'>
                <tags:nameValue name="SiteNumber">${serviceInfo.siteNumber}</tags:nameValue>
            </c:if>
        </tags:nameValueContainer>
        <tags:notNullDataLine value="${serviceInfo.address.locationAddress1}"/>
        <tags:notNullDataLine value="${serviceInfo.address.locationAddress2}"/>
		<c:if test='${serviceInfo.address.cityName != null || serviceInfo.address.stateCode != null || serviceInfo.address.zipCode != null}'>
	        <c:if test='${serviceInfo.address.cityName != null}'>
	                ${serviceInfo.address.cityName},
	        </c:if>
			${serviceInfo.address.stateCode} ${serviceInfo.address.zipCode}
	        <c:if test='${serviceInfoMapURL != null}'>
	            (<a href="${serviceInfoMapURL}" target="_blank">map</a>)
	        </c:if>
	    </c:if>
    </div>
</div>
