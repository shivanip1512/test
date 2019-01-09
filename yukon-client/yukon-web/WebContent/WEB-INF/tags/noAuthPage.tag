<%@ tag trimDirectiveWhitespaces="true"%>

<%@ attribute name="module" required="true"%>
<%@ attribute name="page" required="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.${module}.${page}">
<html class="blank-module no-js">

<head>

<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<meta charset="UTF-8">

<title><cti:msg2 key=".pageTitle"/></title>

<link rel="shortcut icon" href="<cti:url value="/resources/favicon.ico"/>" type="image/x-icon">

<cti:includeCss link="NORMALIZE" force="true"/>
<cti:includeCss link="LAYOUT" force="true"/>
<cti:includeCss link="YUKON" force="true"/>
<cti:includeCss link="BUTTONS" force="true"/>
<cti:includeCss link="ICONS" force="true"/>
<cti:includeCss link="JQUERY_UI_MIN" force="true"/>
<cti:includeCss link="TIPSY" force="true"/>

<%-- Include overrides.css last so that, you know, they actually override.  cascade! --%>
<cti:includeCss link="OVERRIDES" force="true"/>

<cti:includeScript link="MODERNIZR" force="true"/>

<cti:includeScript link="JQUERY" force="true"/>
<cti:includeScript link="JQUERY_TIPSY" force="true"/>
<cti:includeScript link="JQUERY_COOKIE" force="true"/>
<cti:includeScript link="JQUERY_UI" force="true"/>
<cti:includeScript link="JQUERY_CHOSEN" force="true"/>
<cti:includeScript link="JQUERY_ACTUAL" force="true"/>
<cti:includeScript link="JQUERY_CHECK_ALL" force="true"/>
<cti:includeScript link="JQUERY_FORM" force="true"/>
<cti:includeScript link="JQUERY_PLACEHOLDER" force="true"/>
<cti:includeScript link="JS_TIMEZONE_DETECT" force="true"/>

<tags:jsGlobals/>

<cti:includeScript link="YUKON" force="true"/>
<cti:includeScript link="YUKON_COOKIE" force="true"/>
<cti:includeScript link="YUKON_UI_UTIL" force="true"/>
<cti:includeScript link="YUKON_UI" force="true"/>

</head>

<body>
  <div class="yukon-page">
    <header class="yukon-header">
      <div class="toolbar-outer">
        <div class="toolbar-inner"></div>
      </div>
      <div class="nav-outer">
        <div class="nav-inner">
          <nav role="navigation">
            <ul class="menus">
              <li class="menu logo" style="background: transparent;"><a href="#"></a></li>
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
            <li><a id="contact-us" href="javascript:void(0)" data-popup="#contact-us-dialog"><i:inline key="yukon.web.layout.standard.contactUs"/></a></li>
            <cti:msg2 var="dialogTitle" key="layout.standard.contactUs"/>
            <div id="contact-us-dialog" class="dn" data-title="${dialogTitle}"
                data-position-my="right bottom" 
                data-position-at="right top" 
                data-position-of="#contact-us">
                
              <cti:globalSetting var="email" globalSettingType="CONTACT_EMAIL"/>
              <div class="stacked">
                <label>
                  <strong><i:inline key="yukon.web.modules.support.support.email.header"/></strong>&nbsp;
                  <a href="mailto:${fn:escapeXml(email)}">${fn:escapeXml(email)}</a>
                </label>
              </div>
              <cti:globalSetting var="phone" globalSettingType="CONTACT_PHONE"/>
              <div class="stacked">
                <div>
                  <label>
                    <strong><i:inline key="yukon.web.modules.support.support.phone.header"/></strong>&nbsp;
                    ${fn:escapeXml(phone)}
                  </label>
                </div>
              </div>
            </div>
          </ul>
        </nav>
      </div>
      <div class="footer">
        <div class="content clearfix">
          <div class="left">
            <div class="legal">
              <p class="copyright" style="margin-top: 16px;">
                <jsp:useBean id="now" class="java.util.Date"/>
                <fmt:formatDate var="year" value="${now}" pattern="yyyy"/>
                <cti:msg2 key="yukon.web.layout.standard.copyrightFull" arguments="${year}"/>
              </p>
            </div>
          </div>
          <div class="right">
            <div class="branding">
              <a class="footer-logo" href="<cti:url value="/home"/>"></a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</body>

</html>
</cti:msgScope>