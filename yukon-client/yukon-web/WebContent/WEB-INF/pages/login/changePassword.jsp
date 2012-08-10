<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<cti:msgScope paths="modules.login.changePassword">
<html>

    <head>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
        <title><cti:msg2 key=".pageTitle"/></title>

        <link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/StandardStyles.css">
        <link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/YukonGeneralStyles.css">
        <link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/shared/loginStyles.css">

        <script type="text/javascript" src="/JavaScript/lib/jQuery/1.6.4/jquery.min.js"></script>
        <script type="text/javascript" src="/JavaScript/lib/prototype/1.7.0.0/prototype.js"></script>
        <script type="text/javascript" src="/JavaScript/yukon/ui/general.js"></script>

        <cti:css key="yukon.web.login.loginStyles"/>
    </head>

    <body class="blank_module">
    
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
                <cti:msg2 var="passwordResetTitle"  key=".passwordReset" />
                <tags:abstractContainer title="${passwordResetTitle}" type="rounded">
                    <cti:flashScopeMessages/>

                    <form:form commandName="loginBackingBean" action="changePassword">
                        <input type="hidden" name="k" value="${k}" >
                        <form:hidden path="userId" />
                        <form:hidden path="username" />

                        <tags:nameValueContainer2 id="passwordFields">
                            <tags:nameValue2 nameKey=".username">
                                ${fn:escapeXml(loginBackingBean.username)}
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".newPassword">
                                <tags:password path="password1" cssClass="password_editor_field" autocomplete="false" maxlength="64" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".confirmPassword">
                                <tags:password path="password2" cssClass="password_editor_field" autocomplete="false" maxlength="64" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey="defaults.blank" excludeColon="true">
                                <br>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                        
                        <cti:button nameKey="changePassword" type="submit" name="changePassword"/>
                    </form:form>
                </tags:abstractContainer>
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
</cti:msgScope>