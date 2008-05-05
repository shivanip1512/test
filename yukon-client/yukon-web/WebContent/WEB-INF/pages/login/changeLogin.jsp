<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:url var="actionUrl" value="/spring/login/changelogin"/>

<div id="changeLogin">

    <form action="${actionUrl}" method="POST">
        <table cellspacing="5">
            <tr>
                <td align="right">
                    <cti:msg key="yukon.web.login.username"/>
                </td>
                <td>
                    <input type="text" name="username" value="${user.username}"></input>
                </td>
                <td>
                    <cti:msg key="yukon.web.login.required"/>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <cti:msg key="yukon.web.login.oldPassword"/>
                </td>
                <td>
                    <input type="password" name="oldPassword" value="" <c:if test="${disablePasswordChange}">disabled</c:if>></input>
                </td>
                <td>
                    <c:if test="${!disablePasswordChange}">
                        <cti:msg key="yukon.web.login.required"/>
                    </c:if>
                </td>
            </tr>
            <tr>
                <td align="right">
                    <cti:msg key="yukon.web.login.newPassword"/>
                </td>
                <td>
                    <input type="password" name="newPassword" value="" <c:if test="${disablePasswordChange}">disabled</c:if>></input>
                </td>
                <td></td>
            </tr>
            <tr>
                <td align="right">
                    <cti:msg key="yukon.web.login.confirm"/>
                </td>
                <td>
                    <input type="password" name="confirm" value="" <c:if test="${disablePasswordChange}">disabled</c:if>></input>
                </td>
                <td></td>
            </tr>
            <tr>
                <td align="right" colspan="2">
                    <input type="submit" value="<cti:msg key="yukon.web.login.save"/>">
                    <input type="reset" value="<cti:msg key="yukon.web.login.cancel"/>"></input>
                </td>
            </tr>
        </table>
        
        <input type="hidden" name="userId" value="${user.userID}"></input>
        <input type="hidden" name="redirectUrl" value="${redirectUrl}"></input>
    </form>
    
    <c:if test="${not empty loginError}">
        <br>
        <div id="changeLoginErrorDiv"><cti:msg key="${loginError}"/></div>
    </c:if>
<div>