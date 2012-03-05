<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title><cti:msg key="yukon.web.login.pageTitle"/></title>

        <link rel="stylesheet" type="text/css" href="WebConfig/yukon/styles/StandardStyles.css">
        <link rel="stylesheet" type="text/css" href="WebConfig/yukon/styles/YukonGeneralStyles.css">
        <link rel="stylesheet" type="text/css" href="WebConfig/yukon/styles/shared/loginStyles.css">
            
        <cti:css key="yukon.web.login.loginStyles"/>
    </head>

    <body class="blank_module" onLoad="document.forms.form1.USERNAME.focus()">

        <div id="Header">
            <div class="stdhdr_left">
                <div id="TopLeftLogo"><cti:logo key="yukon.web.layout.standard.upperleftlogo"></cti:logo></div>
                <div id="TopLeftLogo2"><cti:logo key="yukon.web.layout.standard.uppermiddlelogo"></cti:logo></div>
            </div>
            <div class="stdhdr_right">
                <div id="TopRightLogo"><cti:logo key="yukon.web.layout.standard.upperrightlogo"></cti:logo></div>
            </div>
            <div class="stdhdr_clear"></div>
        </div>
        <div id='Menu'>

            <div id='topMenu'>
                <div>
                    <div class='stdhdr_leftSide'></div>
                    <div class='stdhdr_rightSide'></div>
                    <div style='clear: both'></div>
                </div>
            </div>
            <div id='bottomBar'>
                <div>
                    <div class='stdhdr_leftSide'></div>
                    <div style='clear: both'></div>
                </div>
            </div>
        </div>

        <div class="loginMain">

            <div class="loginTopSection">
                <div class="formTopLogo">
                    <cti:logo key="yukon.web.login.formTopLogo"></cti:logo>
                </div>
                <div class="loginTitleText">
                    <cti:msg key="yukon.web.login.titleText"></cti:msg>
                </div>
            </div>

            <div class="loginMainSection">
            
                <cti:msg var="loginTitle" key="yukon.web.login.login"></cti:msg>
                <ct:abstractContainer title="${loginTitle}" type="rounded">
                    <cti:flashScopeMessages/>
                    
                    <c:if test="${!empty param.failed}">
                        <div class="loginErrorMsg">
                           <cti:msg key="yukon.web.login.invalidLogin"></cti:msg>
                        </div>
                    </c:if>

                    <c:if test="${!empty param.retrySeconds}">
                        <div class="loginErrorMsg">
                           <cti:msg key="yukon.web.login.retry" argument="${param.retrySeconds}"></cti:msg>
                        </div>
                    </c:if>
                    
                    <c:if test="${!empty param.invalid}">
                        <div class="loginErrorMsg">
                           <cti:msg key="yukon.web.login.invalidUrlAccess"></cti:msg>
                        </div>
                    </c:if>

                    <div class="loginIntroText">
                        <cti:msg key="yukon.web.login.enterLogin"></cti:msg>
                    </div>

                    <form name="form1" method="post" action="<cti:url value="/servlet/LoginController"/>">
                        <table class="loginTable">
                            <tr>
                                <td align="right">
			                        <cti:msg key="yukon.web.login.username"></cti:msg>
                                </td>
                                <td align="left" valign="bottom">
                                    <input type="text" autocomplete="off" id="USERNAME" name="USERNAME" class="loginTextInput">
                                </td>
                            </tr>
                            <tr>
                                <td align="right">
			                        <cti:msg key="yukon.web.login.password"></cti:msg>
                                </td>
                                <td align="left" valign="bottom">
                                    <input type="password" autocomplete="off" name="PASSWORD">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" align="center">
                                    <input type="checkbox" name="rememberme">
			                        <cti:msg key="yukon.web.login.rememberMe"></cti:msg>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" align="center">
			                        <cti:msg var="submitText" key="yukon.web.login.submit"></cti:msg>
                                    <input type="submit" name="login" value="${submitText}">
                                </td>
                            </tr>
                        </table>
                        <cti:checkGlobalRolesAndProperties value="ENABLE_PASSWORD_RECOVERY">
                            <div class="loginHelp">
                                <cti:msg var="forgotPasswordText" key="yukon.web.login.forgotPassword"/>
                                <c:choose>
                                    <c:when test="${useOldForgottenPasswordPage}">
                                        <a href="<cti:url value="/spring/login/forgotPassword"/>">
            		                        ${forgotPasswordText}
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="<cti:url value="/spring/login/forgottenPassword"/>">
                                            ${forgotPasswordText}
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </cti:checkGlobalRolesAndProperties>
                        <input type="hidden" name="REDIRECTED_FROM" value="<spring:escapeBody htmlEscape="true">${param.REDIRECTED_FROM}</spring:escapeBody>">
                        <input type="hidden" name="ACTION" value="LOGIN">
                    </form>

                </ct:abstractContainer>
            </div>
            <div class="loginTopSection">
                <div class="formBottomLogo">
                    <cti:logo key="yukon.web.login.formBottomLogo"></cti:logo>
                </div>
            </div>
            
            
        </div>

        <div class="loginCopyright">
            <cti:msg key="yukon.web.layout.standard.copyrightFull"></cti:msg>
        </div>

    </body>
</html>
