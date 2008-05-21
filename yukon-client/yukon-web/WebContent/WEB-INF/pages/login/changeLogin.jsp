<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<c:url var="actionUrl" value="/spring/login/changelogin"/>
<c:url var="usernameActionUrl" value="${actionUrl}/updateusername"/>
<c:url var="passwordActionUrl" value="${actionUrl}/updatepassword"/>

<div id="changeLogin">

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
                        <input style="font-family: sans-serif;" maxlength="64" type="text" name="username" value="${user.username}" AUTOCOMPLETE=OFF></input>
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
    
    <br>
    
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
    
    <c:if test="${not empty loginError}">
        <br>
        <div id="changeLoginErrorDiv"><cti:msg key="${loginError}"/></div>
    </c:if>
<div>