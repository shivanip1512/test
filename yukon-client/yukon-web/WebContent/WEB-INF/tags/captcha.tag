<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ tag body-content="empty" description="This tag creates a captcha object on your page.  Use this tag with the captchaService to better protect your page." %>

<%@ attribute name="captchaSiteKey" required="true" type="java.lang.String" description="This is the site key used to setup the captcha" %>
<%@ attribute name="captchaTheme" required="true" type="java.lang.String" description="This is theme used to display the captcha.  These can be found on recaptcha's web site." %>
<%@ attribute name="captchaEnabled" required="true" type="java.lang.Boolean" description="This attribute should be direcctly tied to the EnabledCaptcha role property." %>
<%@ attribute name="locale" required="true" type="java.lang.String" %>

<c:if test="${captchaEnabled}">
    <script type="text/javascript" src="https://www.google.com/recaptcha/api.js?hl=${locale}" async defer></script>
    <div class="g-recaptcha" data-sitekey="${captchaSiteKey}" data-theme="${captchaTheme}"></div>
</c:if>