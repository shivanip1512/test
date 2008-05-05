<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:url var="changeLoginUrl" value="/spring/login/changelogin"/>
<c:url var="consumerChangeLoginUrl" value="/spring/stars/consumer/changelogin"/>

<cti:standardPage module="consumer" title="Consumer Energy Services">
    <cti:standardMenu />
    
    <table class="contentTable">
        <tr>
            <td class="leftColumn">
                <h3><cti:msg key="yukon.dr.consumer.changelogin.header" /></h3>
                <div align="center" style="border-top: 1px solid #ccc;">
                    <br>
                    <jsp:include page="${changeLoginUrl}?redirectUrl=${consumerChangeLoginUrl}"/>
                </div>
            </td>
            <td class="rightColumn">
                <cti:customerAccountInfoTag accountNumber="${customerAccount.accountNumber}" />
                <br>
                <img src="<cti:theme key="yukon.dr.consumer.changelogin.rightColumnLogo" default="/WebConfig/yukon/Family.jpg" url="true"/>"></img>
            </td>
        </tr>
    </table>    
    
</cti:standardPage>    
