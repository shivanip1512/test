<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.support">

    <c:if test="${!empty errorMessage}">
        <tags:alertBox type="error">${errorMessage}</tags:alertBox>
    </c:if>
    <div id="rf-js-message"></div>
    <tags:alertBox type="warning" key=".rfSupportBundle.warning"/>
    <tags:sectionContainer2 nameKey="rfSupportBundle">
        <cti:tabs>
            <cti:msg2 key=".supportBundle.createNewHeading" var="createNewHeading"/>
            <cti:tab title="${createNewHeading}">
            
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
            <cti:url value="/support/downloadBundle" var="downloadBundleURl" />
            <cti:tab title="${previousHeading}">
                <form id="previousRfBundlesForm" action="${downloadBundleURl}" method="POST">
                    <cti:csrfToken />
                    <input type="hidden" name="isRfBundle" value="true"></input>
                    <c:if test="${empty rfBundleList}">
                        <span class="empty-list"><i:inline key=".supportBundle.noPreviousBundlesLbl" /></span>
                    </c:if>
                    <c:if test="${not empty rfBundleList}">
                        <ul class="simple-list">
                            <c:forEach var="rfBundleName" varStatus="status" items="${rfBundleList}">
                                <li>
                                    <c:set var="checked" value="${status.first} ? 'checked=checked' : ''"/>
                                    <label><input type="radio" name="fileName" value="${rfBundleName}" ${checked}>${fn:escapeXml(rfBundleName)}</label>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                    <div class="page-action-area">
                        <cti:button nameKey="supportBundle.downloadBtn" type="submit" disabled="${empty rfBundleList}" icon="icon-download" />
                    </div>
                </form>
            </cti:tab>
        </cti:tabs>
    </tags:sectionContainer2>

</cti:msgScope>