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
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/buttons.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/icons.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/lib/jQueryUI/jquery-ui-1.10.4.custom.min.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/JavaScript/lib/jQuery/plugins/tipsy/stylesheets/tipsy.css"/>" >
        
        <%-- Include overrides.css last so that, you know, they actually override.  cascade! --%>
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/overrides.css"/>" >
            
        <cti:includeScript link="MODERNIZR" force="true"/>
        
        <cti:includeScript link="JQUERY" force="true"/>
        <cti:includeScript link="JQUERY_UI" force="true"/>
        <cti:includeScript link="JQUERY_UI_DIALOG_HELPER" force="true"/>
        <cti:includeScript link="JQUERY_CHECK_ALL" force="true"/>
        <cti:includeScript link="JQUERY_FORM" force="true"/>
        <cti:includeScript link="JQUERY_PLACEHOLDER" force="true"/>
        <cti:includeScript link="JQUERY_TRAVERSABLE" force="true"/>
        <cti:includeScript link="PROTOTYPE" force="true"/>
        <cti:includeScript link="YUKON" force="true"/>
        <cti:includeScript link="JQUERY_TIPSY" force="true"/>
        <script type="text/javascript" src="<cti:url value="/JavaScript/yukon.ui.util.js"/>"></script>
        <script type="text/javascript" src="<cti:url value="/JavaScript/yukon.cookies.js"/>"></script>
        <script type="text/javascript" src="<cti:url value="/JavaScript/yukon.dialog.js"/>"></script>

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
                                <a href="#"></a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </header>
        
        <jsp:doBody />

        <footer id="yukon-footer" class="yukon-footer">
            <div class="utility">
                <nav>
                    <ul>
                        <li><a id="contact-us" href="javascript:void(0)"><i:inline key="yukon.web.layout.standard.contactUs"/></a></li>

                        <i:simplePopup titleKey="layout.standard.contactUs" id="contact-us-dialog" on="#contact-us" options="{position: {my:'right bottom', at:'right top', of: '#contact-us'}}">
                            <div class="stacked">
                                <label><strong><i:inline key="yukon.web.modules.support.support.email.header"/></strong>&nbsp;<a href="mailto:<cti:msg2 key="yukon.web.modules.support.support.email.value"/>"><i:inline key="yukon.web.modules.support.support.email.value"/></a></label>
                            </div>
                            <div class="stacked">
                                <div><label><strong><i:inline key="yukon.web.modules.support.support.phone.header"/></strong>&nbsp;<i:inline key="yukon.web.modules.support.support.phone.value"/></label></div>
                                <div><label><strong><i:inline key="yukon.web.modules.support.support.hours.header"/></strong>&nbsp;<i:inline key="yukon.web.modules.support.support.hours.value"/></label></div>
                            </div>
                        </i:simplePopup>
                    </ul>
                </nav>
            </div>
            <div class="footer">
                <div class="content clearfix">
                    <div class="left">
                        <div class="legal">
                            <p class="copyright" style="margin-top:16px;">
                                <cti:msg2 key="yukon.web.layout.standard.copyrightFull" />
                            </p>
                        </div>
                    </div>
                    <div class="right">
                        <div class="branding"><a class="footer-logo" href="/home"></a></div>
                    </div>
                </div>
            </div>
        </footer>
    </div>
    </body>

</html>
</cti:msgScope>