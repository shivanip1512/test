<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ tag body-content="empty" description="This tag creates a captcha object on your page.  Use this tag with the captchaService to better protect your page." %>

<%@ attribute name="captchaPublicKey" required="true" type="java.lang.String" description="This is the public key used to setup the captcha" %>
<%@ attribute name="captchaTheme" required="true" type="java.lang.String" description="This is theme used to display the captcha.  These can be found on recaptcha's web site." %>
<%@ attribute name="captchaEnabled" required="true" type="java.lang.Boolean" description="This attribute should be direcctly tied to the EnabledCaptcha role property." %>

<c:if test="${captchaEnabled}">
    <script type="text/javascript">
     var RecaptchaOptions = {
        theme : '${captchaTheme}',
        custom_theme_widget: 'recaptcha_widget'
     };
    </script> 
    
    <script type="text/javascript" src="http://www.google.com/recaptcha/api/challenge?k=${captchaPublicKey}"></script>
    <script type="text/javascript">
        jQuery(".recaptcha_only_if_image").hide();
    </script>
</c:if>