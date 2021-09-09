<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="modules.support">

    <c:if test="${!empty errorMessage}">
        <tags:alertBox type="error">${errorMessage}</tags:alertBox>
    </c:if>
    <div id="rf-js-message"></div>
    <tags:sectionContainer2 nameKey="rfSupportBundle">
    	<div class="notes MB5"><i:inline key=".rfSupportBundle.note"/></div>
        <cti:tabs>
            <cti:msg2 key=".supportBundle.createNewHeading" var="createNewHeading"/>
            <cti:tab title="${createNewHeading}" >
            
                <cti:url value="/support/createRfBundle" var="createRfBundleURL"/>
                <form:form id="rfSupportBundle-form" class="js-block-this" modelAttribute="rfSupportBundle" action="${createRfBundleURL}" method="POST">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".supportBundle.custNameLbl">
                            <tags:input id="rfCustomerName" path="customerName"/> 
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".rfSupportBundle.dateRange">
                            <dt:date path="date" value="${rfSupportBundle.date}" maxDate="${now}"/>
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

            <cti:msg2 key='.supportBundle.previousHeading' var="previousHeading" />
            <cti:tab title="${previousHeading}" headerClasses="js-previous-rf-bundles">
                <div id="rf-support-bundle-tab">
                   <jsp:include page="rfPreviousBundleTab.jsp"/>
                </div>
            </cti:tab>
        </cti:tabs>
    </tags:sectionContainer2>

</cti:msgScope>