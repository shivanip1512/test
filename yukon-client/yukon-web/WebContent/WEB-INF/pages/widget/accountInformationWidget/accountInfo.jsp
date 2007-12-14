<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div>
    <div class="widgetInternalSectionHeader">
        <c:out value="Customer Information"/>
    </div>
    <c:choose>
    	<c:when test="${empty customerInfoError}" >
    		<div class="widgetInternalSection">
            	<tags:nameValueContainer altRowOn="true">
            	<tags:nameValue name="Name">
        <c:if test='${info.firstName != null && info.firstName != null}'>
            ${info.firstName} ${info.lastName} <br>
        </c:if>
        </tags:nameValue>
        <tags:nameValue name="Address">
        <tags:notNullDataLine value="${info.address.locationAddress1}"/>
        <tags:notNullDataLine value="${info.address.locationAddress2}"/>
        <c:if test='${info.address.cityName != null || info.address.stateCode != null || info.address.zipCode != null}'>
	        <c:if test='${info.address.cityName != null}'>
	                ${info.address.cityName},
	        </c:if>
			${info.address.stateCode} ${info.address.zipCode}
<!-- 	        <c:if test='${infoMapURL != null}'>
	            (<a href="${infoMapURL}" target="_blank">map</a>)
	        </c:if>
-->
	    </c:if>
	    		</tags:nameValue>
	    		</tags:nameValueContainer>
    		</div>
    	</c:when>
    	<c:otherwise>
    		<div id="customerInfoError">
    		<ct:hideReveal styleClass="titledContainerOverride" title="Information not available."
                showInitially="false">
                <span class="internalSectionHeader">${customerInfoError}</span><br>
                <span class="internalSectionHeader">Please view ${logName} for more information.</span>
            </ct:hideReveal>
    		</div>
    	</c:otherwise>
    </c:choose>

    <div class="widgetInternalSectionHeader">
  		<c:out value="Service Location" />
    </div>
    <c:choose>
    	<c:when test="${empty serviceLocationError}" >
		    <div class="widgetInternalSection">
        		<tags:nameValueContainer altRowOn="true">
            <c:if test='${serviceInfo.customerNumber != null}'>
                <tags:nameValue name="Customer Number">${serviceInfo.customerNumber}</tags:nameValue>
            </c:if>
            <c:if test='${serviceInfo.accountNumber != null}'>
                <tags:nameValue name="Account Number">${serviceInfo.accountNumber}</tags:nameValue>
            </c:if>
            <c:if test='${serviceInfo.siteNumber != null}'>
                <tags:nameValue name="Site Number">${serviceInfo.siteNumber}</tags:nameValue>
            </c:if>
         <tags:nameValue name="Address">
        <tags:notNullDataLine value="${serviceInfo.address.locationAddress1}"/>
        <tags:notNullDataLine value="${serviceInfo.address.locationAddress2}"/>
		<c:if test='${serviceInfo.address.cityName != null || serviceInfo.address.stateCode != null || serviceInfo.address.zipCode != null}'>
	        <c:if test='${serviceInfo.address.cityName != null}'>
	                ${serviceInfo.address.cityName},
	        </c:if>
			${serviceInfo.address.stateCode} ${serviceInfo.address.zipCode}
<!-- 	        <c:if test='${serviceInfoMapURL != null}'>
	            (<a href="${serviceInfoMapURL}" target="_blank">map</a>)
	        </c:if>
-->
	    </c:if>
			    </tags:nameValue>
		        </tags:nameValueContainer>
		    </div>
	    </c:when>
       	<c:otherwise>
    		<div id="customerInfoError">
    		  <ct:hideReveal styleClass="titledContainerOverride" title="Information not available."
                showInitially="false">
                <span class="internalSectionHeader">${serviceLocationError}</span><br>
                <span class="internalSectionHeader">Please view ${logName} for more information.</span>
              </ct:hideReveal>
    		</div>
    	</c:otherwise>
  	</c:choose>
</div>
