<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:standardPage module="consumer" page="contactus">
    <cti:standardMenu />

    <h3><cti:msg key="yukon.dr.consumer.contactus.header" /></h3>
    <div style="border: 1px solid #ccc;"></div>
    <br>
    <div align="center">
        <spring:escapeBody htmlEscape="true">${contactUs.companyName}</spring:escapeBody><br>
        <ct:formattedAddress address="${contactUs.address}"/><br>
        <br>
        <div><cti:msg key="yukon.dr.consumer.contactus.phonenumber"/> <cti:formatPhoneNumber value="${contactUs.phoneNumber}"/></div>
        <div><cti:msg key="yukon.dr.consumer.contactus.faxnumber"/> <cti:formatPhoneNumber value="${contactUs.faxNumber}"/></div>
         <div><a href="mailto:${contactUs.email}"><spring:escapeBody htmlEscape="true">${contactUs.email}</spring:escapeBody></a></div>
    </div>    
    
</cti:standardPage>    