<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:standardPage module="consumer" title="Consumer Energy Services">
    <cti:standardMenu />

    <table class="contentTable">
        <tr>
            <td class="leftColumn">
                <h3><cti:msg key="yukon.dr.consumer.contactus.header" /></h3>
                <div style="border: 1px solid #ccc;"></div>
                <br>
                <div align="center">
                    ${contactUs.companyName}<br>
                    <ct:formattedAddress address="${contactUs.address}"/><br>
                    <br>
                    <div><cti:msg key="yukon.dr.consumer.contactus.phonenumber"/> <cti:formatPhoneNumber value="${contactUs.phoneNumber}"/></div>
                    <div><cti:msg key="yukon.dr.consumer.contactus.faxnumber"/> <cti:formatPhoneNumber value="${contactUs.faxNumber}"/></div>
                     <div><a href="mailto:${contactUs.email}">${contactUs.email}</a></div>
                </div>    
            </td>
            <td class="rightColumn">
                <cti:customerAccountInfoTag accountNumber="${customerAccount.accountNumber}" />
                <br>
                <img src="<cti:theme key="yukon.dr.consumer.general.rightColumnLogo" default="/WebConfig/yukon/Family.jpg" url="true"/>"></img>
            </td>
        </tr>
    </table>
    
</cti:standardPage>    