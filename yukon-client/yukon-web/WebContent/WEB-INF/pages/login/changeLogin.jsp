<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:url var="actionUrl" value="/spring/login/changelogin"/>
<cti:url var="usernameActionUrl" value="${actionUrl}/updateusername"/>
<cti:url var="passwordActionUrl" value="${actionUrl}/updatepassword"/>

<div id="changeLogin">

    <cti:checkProperty  property="ResidentialCustomerRole.CONSUMER_INFO_CHANGE_LOGIN_USERNAME">
    <cti:msg key="yukon.web.changelogin.changeUsername" var="changeUsernameTitle"/>
    <ct:boxContainer title="${changeUsernameTitle}" hideEnabled="false">
        <form action="${usernameActionUrl}" method="POST">
            <br>
            <table>
                <tr>
                    <td align="right">
                        <cti:msg key="yukon.web.changelogin.username"/>
                    </td>
                    <td align="left">
                        <input style="font-family: sans-serif;" maxlength="64" type="text" autocomplete="off" name="username" value="<spring:escapeBody htmlEscape="true">${user.username}</spring:escapeBody>" AUTOCOMPLETE=OFF></input>
                    </td>
                </tr>
                <tr>
                    <td align="right">
                        <cti:msg key="yukon.web.changelogin.oldPassword"/>
                    </td>
                    <td align="left">
                        <input style="font-family: sans-serif;" maxlength="64" type="password" name="oldPassword" value="" <c:if test="${disablePasswordChange}">disabled</c:if>></input>
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="2">
                        <br>
                        <input type="submit" value="<cti:msg key="yukon.web.changelogin.save"/>">
                        <input type="reset" value="<cti:msg key="yukon.web.changelogin.cancel"/>"></input>
                    </td>
                </tr>
            </table>
            
            <input type="hidden" name="redirectUrl" value="${redirectUrl}"></input>
        </form>
    </ct:boxContainer>
    </cti:checkProperty>
    <br>

    <cti:checkProperty  property="ResidentialCustomerRole.CONSUMER_INFO_CHANGE_LOGIN_PASSWORD">
    <cti:msg key="yukon.web.changelogin.changePassword" var="changePasswordTitle"/>
    <ct:boxContainer title="${changePasswordTitle}" hideEnabled="false">
        <form action="${passwordActionUrl}" method="POST">
            <br>
            <table>
                <tr>
                    <td align="right">
                        <cti:msg key="yukon.web.changelogin.oldPassword"/>
                    </td>
                    <td align="left">
                        <input style="font-family: sans-serif;" maxlength="64" type="password" name="oldPassword" value="" <c:if test="${disablePasswordChange}">disabled</c:if>></input>
                    </td>
                </tr>
                <tr>
                    <td align="right">
                        <cti:msg key="yukon.web.changelogin.newPassword"/>   
                    </td>
                    <td align="left">
                        <input style="font-family: sans-serif;" maxlength="64" type="password" name="newPassword" value="" <c:if test="${disablePasswordChange}">disabled</c:if>></input>    
                    </td>
                </tr>
                <tr>
                    <td align="right" style="font-size: small;">
                        <cti:msg key="yukon.web.changelogin.confirm"/>
                    </td>
                    <td align="left">
                        <input style="font-family: sans-serif;" maxlength="64" type="password" name="confirm" value="" <c:if test="${disablePasswordChange}">disabled</c:if>></input>
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="2">
                        <br>
                        <input type="submit" value="<cti:msg key="yukon.web.changelogin.save"/>">
                        <input type="reset" value="<cti:msg key="yukon.web.changelogin.cancel"/>"></input>
                    </td>
                </tr>
            </table>
            
            <input type="hidden" name="redirectUrl" value="${redirectUrl}"></input>
        </form>
    </ct:boxContainer>
    </cti:checkProperty>    

    <c:if test="${not empty loginError}">
        <br>
        <div id="changeLoginErrorDiv">
            <cti:msg key="${loginError}"/>
            <c:if test="${!empty param.authTimeoutSeconds}">
                <br/>
                <cti:msg key="yukon.web.changelogin.error.AUTH_TIMEOUT" argument="${param.authTimeoutSeconds}"></cti:msg>
            </c:if>            
        </div>
    </c:if>
<div>