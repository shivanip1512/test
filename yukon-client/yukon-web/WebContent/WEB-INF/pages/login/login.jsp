<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Energy Services Operations Center</title>

        <link rel="stylesheet" type="text/css"
            href="WebConfig/yukon/styles/StandardStyles.css">
        <link rel="stylesheet" type="text/css"
            href="WebConfig/yukon/styles/YukonGeneralStyles.css">
        <link rel="stylesheet"
            href="WebConfig/yukon/styles/loginStyles.css"
            type="text/css">
    </head>

    <body class="blank_module"
        onLoad="document.forms.form1.USERNAME.focus()">

        <div id="Header">
            <div class="stdhdr_left">
                <div id="TopLeftLogo"></div>
                <div id="TopLeftLogo2"></div>
            </div>
            <div class="stdhdr_right">
                <div id="TopRightLogo"></div>
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
                <div class="loginTitleIntroText">
                    Welcome to
                </div>
                <div class="loginTitleText">
                    Energy Services Operations Center
                </div>
            </div>

            <div class="loginMainSection">
                <cti:titledContainer title="Login"
                    styleClass="styledContainer">
                    <c:if test="${!empty param.failed}">
                        <div class="loginErrorMsg">
                            * Invalid username/password
                        </div>
                    </c:if>

                    <div class="loginIntroText">
                        Please enter your username and password below.
                    </div>

                    <form name="form1" method="post"
                        action="<c:url value="/servlet/LoginController"/>">
                        <table class="loginTable">
                            <tr>
                                <td align="right">
                                    Username:
                                </td>
                                <td align="left" valign="bottom">
                                    <input type="text" id="USERNAME"
                                        name="USERNAME"
                                        class="loginTextInput">
                                </td>
                            </tr>
                            <tr>
                                <td align="right">
                                    Password:
                                </td>
                                <td align="left" valign="bottom">
                                    <input type="password"
                                        name="PASSWORD">
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" align="center">
                                    <input type="checkbox"
                                        name="rememberme" />
                                    Remember me
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2" align="center">
                                    <input type="submit" name="login"
                                        value="Submit">
                                </td>
                            </tr>
                        </table>

                        <cti:isStarsExist>
                            <div class="loginHelp">
                                <a href="<c:url value="/pwordreq.jsp"/>">Forgot
                                    your password?</a>
                            </div>
                        </cti:isStarsExist>
                        <input type="hidden" name="REDIRECTED_FROM"
                            value="${redirectedfrom}" />
                        <input type="hidden" name="ACTION" value="LOGIN">
                    </form>

                </cti:titledContainer>
            </div>
        </div>

        <div class="loginCopyright">
            <jsp:include page="/include/full_copyright.jsp" />
        </div>

    </body>
</html>
