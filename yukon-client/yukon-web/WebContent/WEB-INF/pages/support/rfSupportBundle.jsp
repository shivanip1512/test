<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.support">

    <div id="rf-success-error"></div>
    <tags:alertBox type="warning" key=".rfSupportBundle.warning"/>
    <tags:sectionContainer2 nameKey="rfSupportBundle">
        <cti:tabs>
            <cti:msg2 key=".supportBundle.createNewHeading" var="createNewHeading"/>
            <cti:tab title="${createNewHeading}">
            
                <cti:url value="/support/createRfBundle" var="createRfBundleURL"/>
                <form:form id="rfSupportBundle-form" modelAttribute="rfSupportBundle" action="${createRfBundleURL}" method="POST">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".supportBundle.custNameLbl">
                            <tags:input path="customerName"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".rfSupportBundle.dateRange">
                            <dt:date path="date" value="${rfSupportBundle.date}"/>
                            <span class="fr cp"><cti:icon icon="icon-help" data-popup="#date-help"/></span>
	                        <cti:msg2 var="helpTitle" key=".rfSupportBundle.dateRange"/>
	                        <cti:msg2 var="helpText" key=".rfSupportBundle.dateRange.helpText"/>
	                        <div id="date-help" class="dn" data-title="${helpTitle}">${helpText}</div>
	                    </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <div class="page-action-area"> 
                        <cti:button nameKey="supportBundle.createBundleBtn" classes="primary action js-execute-command-rf" busy="true"/>
                    </div>
                </form:form>
            </cti:tab>
        </cti:tabs>
    </tags:sectionContainer2>

</cti:msgScope>
