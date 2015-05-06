<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<cti:url var="usernameActionUrl" value="/login/changelogin/updateusername"/>
<cti:url var="passwordActionUrl" value="/login/changelogin/updatepassword"/>

<div id="changeLogin">
    <c:if test="${not empty loginChangeMsg}">
        <c:choose>
            <c:when test="${success}">
                <Span class="ConfirmMsg" id="changeLoginErrorDiv">
            </c:when>
            <c:otherwise>
                <Span class="ErrorMsg" id="changeLoginErrorDiv">
            </c:otherwise>
           </c:choose>
                <cti:msg key="${loginChangeMsg}"/>
                <c:if test="${!empty param.retrySeconds}">
                    <br/>
                    <cti:msg key="yukon.web.changelogin.error.RETRY" argument="${param.retrySeconds}"></cti:msg>
                </c:if>
        </Span>
        <br/>
    </c:if>
    
    <cti:checkRolesAndProperties  value="RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_USERNAME">
    <cti:msg key="yukon.web.changelogin.changeUsername" var="changeUsernameTitle"/>
    <tags:boxContainer title="${changeUsernameTitle}" hideEnabled="false">
        <form action="${usernameActionUrl}" method="POST">
            <cti:csrfToken/>
            <br>
            <table>
                <tr>
                    <td align="right">
                        <cti:msg key="yukon.web.changelogin.username"/>
                    </td>
                    <td align="left">
                        <input maxlength="64" type="text" autocomplete="off" name="username" value="${fn:escapeXml(user.username)}">
                    </td>
                </tr>
                <tr>
                    <td align="right">
                        <cti:msg key="yukon.web.changelogin.oldPassword"/>
                    </td>
                    <td align="left">
                        <input maxlength="64" type="password" name="oldPassword" value="" 
                            <c:if test="${disablePasswordChange}">disabled</c:if>
                            autocomplete="off">
                    </td>
                </tr>
                <tr>
                    <td align="center" colspan="2">
                        <br>
                        <cti:msg key="yukon.web.changelogin.save" var="save"/>
                        <cti:msg key="yukon.web.changelogin.cancel" var="cancel"/>
                        <cti:button type="submit" label="${save}"/>
                        <cti:button type="reset" label="${cancel}"/>
                    </td>
                </tr>
            </table>
            
            <input type="hidden" name="redirectUrl" value="${redirectUrl}"></input>
        </form>
    </tags:boxContainer>
    </cti:checkRolesAndProperties>
    <br>

    <cti:checkRolesAndProperties  value="RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_PASSWORD">
        <cti:msg key="yukon.web.changelogin.changePassword" var="changePasswordTitle"/>
        <tags:boxContainer title="${changePasswordTitle}" hideEnabled="false">
            <form action="${passwordActionUrl}" method="POST">
                <cti:csrfToken/>
                <br>
                <table>
                    <tr>
                        <td><cti:msg key="yukon.web.changelogin.oldPassword"/></td>
                        <td>
                            <input maxlength="64" type="password" name="oldPassword" value="" 
                                <c:if test="${disablePasswordChange}">disabled</c:if>
                                autocomplete="off">
                        </td>
                    </tr>
                    <tr>
                        <td><cti:msg key="yukon.web.changelogin.newPassword"/></td>
                        <td>
                            <input maxlength="64" type="password" name="newPassword" value="" 
                                <c:if test="${disablePasswordChange}">disabled</c:if>
                                autocomplete="off">
                        </td>
                    </tr>
                    <tr>
                        <td><cti:msg key="yukon.web.changelogin.confirm"/></td>
                        <td>
                            <input maxlength="64" type="password" name="confirm" value="" 
                                <c:if test="${disablePasswordChange}">disabled</c:if>
                                autocomplete="off">
                        </td>
                    </tr>
                </table>
                <div class="page-action-area">
                    <cti:msg key="yukon.web.changelogin.save" var="save"/>
                    <cti:msg key="yukon.web.changelogin.cancel" var="cancel"/>
                    <cti:button type="submit" value="${save}" label="${save}"/>
                    <cti:button type="reset" value="${cancel}" label="${cancel}"/>
                </div>
                <input type="hidden" name="redirectUrl" value="${redirectUrl}"></input>
            </form>
        </tags:boxContainer>
    </cti:checkRolesAndProperties>
<div>