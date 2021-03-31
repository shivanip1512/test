<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:url value="/support/createRfBundle" var="createRfBundleURL"/>
<cti:url value="/support/viewBundleProgress" var="viewBundleProgressURL"/>    
    <div class="column one">
        <tags:sectionContainer2 nameKey="rfSupportBundle">
            <cti:tabs>
                <cti:msg2 key=".supportBundle.createNewHeading" var="createNewHeading"/>
                <cti:tab title="${createNewHeading}" >
                    <form:form id="createRfBundleForm" modelAttribute="supportBundle" action="${createRfBundleURL}" method="POST">
                        <cti:csrfToken/>
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".supportBundle.custNameLbl">
                                <tags:input path="customerName"/>
                            </tags:nameValue2>
		                       
		                    <tags:nameValue2 nameKey=".supportBundle.dateRange">
		                       <dt:date name="date" wrapperClass="fn vam"/>
		                    </tags:nameValue2>
                    
                        </tags:nameValueContainer2>
                        <div class="page-action-area"> 
                            <c:if test="${!inProgress}">
                                <cti:button nameKey="supportBundle.createBundleBtn" type="submit" classes="primary action js-execute-command" busy="true"/>
                              
                            </c:if>
                          
                               
                            <c:if test="${inProgress}">
                              
                                <i:inline key=".supportBundle.bundleInProgressMsg"/>
                                <cti:button id="viewProgress" nameKey="supportBundle.viewProgressBtn" href="${viewBundleProgressURL}" />
                            </c:if>
                        </div>
                    </form:form>
                </cti:tab>
                <cti:msg2 key='.supportBundle.previousHeading' var="previousHeading"/>
                <cti:url value="/support/downloadBundle" var="downloadBundleURl"/>
                <cti:tab title="${previousHeading}" >
                    <form id="previousBundlesForm" action="${downloadBundleURl}" method="POST">
                        <cti:csrfToken/>
                        <c:if test="${empty bundleList}">
                            <span class="empty-list"><i:inline key=".supportBundle.noPreviousBundlesLbl"/></span>
                        </c:if>
                        <c:if test="${not empty bundleList}">
                            <ul class="simple-list">
                                <c:forEach var="bundleName" varStatus="status" items="${bundleList}">
                                    <li>
                                        <c:if test="${status.first}"><c:set var="checked">checked="checked"</c:set></c:if>
                                        <c:if test="${!status.first}"><c:set var="checked"></c:set></c:if>
                                        <label><input type="radio" name="fileName" value="${bundleName}" ${checked}>${fn:escapeXml(bundleName)}</label>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:if>
                        <div class="page-action-area">
                            <cti:button nameKey="supportBundle.downloadBtn" type="submit" disabled="${empty bundleList}" icon="icon-download"/>
                        </div>
                    </form>
                </cti:tab>
            </cti:tabs>
        </tags:sectionContainer2>
    </div>
