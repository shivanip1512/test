<%@ attribute name="module" required="true"%>
<%@ attribute name="page" required="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.${module}.${page}">
<html>

    <head>
    <meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
        <title><cti:msg2 key=".pageTitle"/></title>

        <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/normalize.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/layout.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/yukon.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/Buttons/css/buttons.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/Icons/silk/icons.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/lib/jQuery/yukon/jquery-ui-1.9.2.custom.css"/>" >
        
        <%-- Include functional-overrides.css last so that, you know, they actually override.  cascade! --%>
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/functional-overrides.css"/>" >
            
        <cti:includeScript link="JQUERY" force="true"/>
        <cti:includeScript link="JQUERY_UI" force="true"/>
        <cti:includeScript link="JQUERY_UI_DIALOG_HELPER" force="true"/>
        <cti:includeScript link="JQUERY_UI_CHECK_ALL" force="true"/>
        <cti:includeScript link="JQUERY_UI_ACTION_WHEN" force="true"/>
        <cti:includeScript link="JQUERY_FORM" force="true"/>
        <cti:includeScript link="JQUERY_PLACEHOLDER" force="true"/>
        <cti:includeScript link="JQUERY_TRAVERSABLE" force="true"/>
        <cti:includeScript link="JQUERY_TIP_TIP" force="true"/>
        <cti:includeScript link="PROTOTYPE" force="true"/>
        <cti:includeScript link="YUKON_UI" force="true"/>
        <script type="text/javascript" src="/JavaScript/yukonGeneral.js"></script>
        <script type="text/javascript" src="/JavaScript/simpleCookies.js"></script>
        <script type="text/javascript" src="/JavaScript/simpleDialog.js"></script>

        <cti:css key="yukon.web.login.loginStyles"/>
    </head>

    <body class="blank_module">
    <div id="page">
        <header class="yukon-header">
            <div class="outer" role="banner">
                <div class="inner"></div>
            </div>
            <div class="nav">
                <div class="navOut">
                    <nav role="navigation">
                        <ul>
                            <li class="logo" style="background: transparent;">
                                <a href="/dashboard">
                                    <img src="/WebConfig/yukon/layout/YukonBW2.png" alt="Yukon Home">
                                </a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </header>
        
        <jsp:doBody />

        <footer id="yukon-footer" class="yukon-footer">
            <div class="utility">
                <div class="footerNav">
                    <nav>
                        <ul>
                            <li><a href="/billing">Contact Us</a></li>
                        </ul>
                    </nav>
                </div>
            </div>
            <div class="footer">
                <div class="footerNav clearfix">
                    <div class="wrapper">
                        <div class="legal">
                            <p class="copyright">
                                <cti:msg2 key="yukon.web.layout.standard.copyrightFull" />
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </footer>
    </div>
    </body>

</html>
</cti:msgScope>