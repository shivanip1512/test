<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="passwordPolicy" required="true" type="com.cannontech.core.authentication.model.PasswordPolicy" %>

<cti:includeScript link="/JavaScript/yukon/ui/updatePassword.js" force="true" />

<script>
jQuery(function(){
    Yukon.ui.passwordManager.init({minLength: ${passwordPolicy.minPasswordLength}});
    jQuery(".password_manager input.new").trigger('keyup');
});
</script>
<style type="text/css">
.description .list, .description .list .no_fail {list-style: none;padding-left: 16px;}
.description .list {padding-left: 0;} 
</style>

<div class="description">
    <h4><i:inline key="yukon.web.modules.passwordPolicy.requirements"/></h4>
    <ul class="list clearfix">
        <li class="MIN_PASSWORD_LENGTH_NOT_MET MAX_PASSWORD_LENGTH_EXCEEDED clearfix">
            <i class="icon icon-blank"></i>
            <span class="b-label"><i:inline key="yukon.web.modules.passwordPolicy.length.description" 
                arguments="${passwordPolicy.minPasswordLength}" /></span>
        </li>
        <c:if test="${passwordPolicy.passwordHistory > 0}">
            <li class="PASSWORD_USED_TOO_RECENTLY clearfix">
                <i class="icon icon-blank"></i>
                <span class="b-label"><i:inline key="yukon.web.modules.passwordPolicy.history.description" 
                    arguments="${passwordPolicy.passwordHistory}" /></span>
            </li>
        </c:if>
        <li class="PASSWORD_DOES_NOT_MEET_POLICY_QUALITY clearfix">
            <i class="icon icon-blank"></i>
            <span class="b-label"><i:inline key="yukon.web.modules.passwordPolicy.quality.description" 
                arguments="${passwordPolicy.passwordQualityCheck}" /></span>
        </li>
        <li class="clearfix">
            <ul class="no_fail clearfix">
                <c:forEach items="${passwordPolicy.policyRules}" var="rule">
                    <li class="${rule} clearfix">
                        <i class="icon icon-blank"></i>
                        <span class="b-label"><i:inline key="${rule.formatKey}.description" /></span>
                    </li>
                </c:forEach>
            </ul>
        </li>
    </ul>
</div>