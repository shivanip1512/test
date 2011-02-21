<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="serviceCompany.VIEW">
    
    <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;">
        <form:form id="updateForm" commandName="serviceCompany" action="">
        <%-- CUSTOMER CONTACT --%>
        <cti:dataGridCell>
            <tags:formElementContainer nameKey="generalInfoSection">
                <tags:nameValueContainer2 id="customerContactTable">
                    <tags:inputNameValue nameKey=".companyName" path="companyName"/>
                    <span class="meta">
                        <tags:liteAddress address="${serviceCompany.address}" ignore="(none)"/>
                        <br/>
                        <cti:formatPhoneNumber value="${serviceCompany.mainPhoneNumber}"/>
                        <br/>
                        <cti:formatPhoneNumber value="${serviceCompany.mainFaxNumber}"/>
                        <br/>
                    </span>
                    <spring:htmlEscape defaultHtmlEscape="">${serviceCompany.hiType}</spring:htmlEscape>
                </tags:nameValueContainer2>
            </tags:formElementContainer>
        </cti:dataGridCell>
    
        <tags:formElementContainer nameKey="addressContainer">
            ::ADDRESS::
            <br/>
            <br/>
            <br/>
        </tags:formElementContainer>
        
        <tags:formElementContainer nameKey="contactContainer">
        ::NAME::
            <tags:notNullDataLine ignore="(none)" inLine="true" value="${serviceCompany.primaryContact.contFirstName}"></tags:notNullDataLine>
            <tags:notNullDataLine ignore="(none)" value="${serviceCompany.primaryContact.contLastName}"></tags:notNullDataLine>
            <tags:notNullDataLine ignore="(none)" value="${serviceCompany.primaryContact.contactID}"></tags:notNullDataLine>
        </tags:formElementContainer>
        
        <tags:formElementContainer nameKey="designationCodeContainer">
            <ul class="inline_list">
                    <c:forEach items="${contractorZipCodes}" var="designationCode">
                        <li>
                            ${designationCode.designationCodeValue}
                        </li>
                    </c:forEach>
            </ul>
        </tags:formElementContainer>
    </form:form>
    </cti:dataGrid>
    
    <cti:url var="serviceCompanyEditUrl" value="${baseUrl}/edit">
        <cti:param name="ecId" value="${ecId}"/>
        <cti:param name="serviceCompanyId" value="${serviceCompany.companyId}"/>
    </cti:url>
    <cti:button key="edit" onclick="javascript:window.location ='${serviceCompanyEditUrl}'"/>
    
    <cti:url var="serviceCompanyIndexUrl" value="${baseUrl}/home">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    <cti:button key="cancel" onclick="javascript:window.location ='${serviceCompanyIndexUrl}'"/>
    
</cti:standardPage>