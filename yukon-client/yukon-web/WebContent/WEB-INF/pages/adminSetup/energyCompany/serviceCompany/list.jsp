<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="serviceCompany.list">
        <tags:boxContainer2 nameKey="infoContainer">
            
            <table class="compact-results-table row-highlighting">
                <thead>
                <tr>
                    <th><cti:msg key="yukon.web.modules.adminSetup.serviceCompany.companyName"/></th>
                    <th><cti:msg key="yukon.web.modules.adminSetup.serviceCompany.mainPhone"/></th>
                    <th><cti:msg key="yukon.web.modules.adminSetup.serviceCompany.mainFax"/></th>
                    <th><cti:msg key="yukon.web.modules.adminSetup.serviceCompany.contact"/></th>
                    <th><cti:msg key="yukon.web.modules.adminSetup.serviceCompany.addressSection.title"/></th>
                </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach items="${serviceCompanies}" var="serviceCompany">
                
                    <tr>
                        <td style="width:30%">
                            <cti:url var="serviceCompanyViewUrl" value="${baseUrl}/view">
                                <cti:param name="ecId" value="${ecId}"/>
                                <cti:param name="serviceCompanyId" value="${serviceCompany.companyId}"/>
                            </cti:url>
                            <a href="${serviceCompanyViewUrl}">
                                <spring:escapeBody htmlEscape="true">
                                    ${serviceCompany.companyName}
                                </spring:escapeBody>
                            </a>
                        </td>
                        <td>
                            <spring:escapeBody htmlEscape="true">
                                <cti:formatPhoneNumber value="${serviceCompany.mainPhoneNumber}"/>
                            </spring:escapeBody>
                        </td>
                        <td>
                            <spring:escapeBody htmlEscape="true">
                                <cti:formatPhoneNumber value="${serviceCompany.mainFaxNumber}" />
                            </spring:escapeBody>
                        </td>
                        <td>
                            <span class="meta">
                                <tags:notNullDataLine ignore="(none)" inLine="true" value="${fn:escapeXml(serviceCompany.primaryContact.contFirstName)}"></tags:notNullDataLine>
                                <tags:notNullDataLine ignore="(none)" value="${fn:escapeXml(serviceCompany.primaryContact.contLastName)}"></tags:notNullDataLine>
                                <tags:notNullDataLine ignore="(none)" value="${serviceCompany.emailContactNotification}"></tags:notNullDataLine>
                            </span>
                        </td>
                        <td>
                            <span class="meta">
                                <tags:liteAddress ignore="(none)" address="${serviceCompany.address}"></tags:liteAddress>
                            </span>
                        </td>
                    </tr>
                    
                </c:forEach>
                </tbody>
            </table>
            
            <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_HARDWARES">
                <div class="action-area">
                    <cti:url var="createserviceCompanyUrl" value="${baseUrl}/new">
                        <cti:param name="ecId" value="${ecId}"/>
                    </cti:url>
                    <cti:button nameKey="create" icon="icon-plus-green" onclick="javascript:window.location='${createserviceCompanyUrl}'"/>
                </div>
            </cti:checkRolesAndProperties>
            
        </tags:boxContainer2>
</cti:standardPage>