<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:url value="/support/createRfBundle" var="createRfBundleURL"/>

    <div class="column one">
    <tags:alertBox type="warning" key=".rfSupportBundle.warning"/>
    
        <tags:sectionContainer2 nameKey="rfSupportBundle">
            <cti:tabs>
                <cti:msg2 key=".supportBundle.createNewHeading" var="createNewHeading"/>
                <cti:tab title="${createNewHeading}">

                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".supportBundle.custNameLbl">
                            <input name="rfCustomerName" class="js-rf-customer-name" type="text" value="" autocomplete="on">
                            <br>
                            <span class="errors dn js-rf-customer-name-errors js-rf-customer-name"></span>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".rfSupportBundle.dateRange">
                            <dt:date name="date" wrapperClass="fn vam" value="${todaysDate}"/>
                            <span class="fr cp"><cti:icon icon="icon-help" data-popup="#date-help"/></span>
	                        <cti:msg2 var="helpTitle" key=".rfSupportBundle.dateRange"/>
	                        <cti:msg2 var="helpText" key=".rfSupportBundle.dateRange.helpText"/>
	                        <div id="date-help" class="dn" data-title="${helpTitle}">${helpText}</div>
	                    </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <div class="page-action-area"> 
                        <cti:button nameKey="supportBundle.createBundleBtn" classes="primary action js-execute-command-rf" busy="true"/>
                    </div>
                </cti:tab>
            </cti:tabs>
        </tags:sectionContainer2>
    </div>
