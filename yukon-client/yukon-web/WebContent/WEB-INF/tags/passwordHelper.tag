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

<div class="password_manager">
	<div class="fl">
		<jsp:doBody />
	</div>
</div>

<div class="description">
	<h3>
		<i:inline key="yukon.web.modules.passwordPolicy.requirements" />
	</h3>
	<ul>
		<li class="MIN_PASSWORD_LENGTH_NOT_MET MAX_PASSWORD_LENGTH_EXCEEDED"><i:inline
				key="yukon.web.modules.passwordPolicy.length.description"
				arguments="${passwordPolicy.minPasswordLength}" /></li>
		<c:if test="${passwordPolicy.passwordHistory > 0}">
			<li class="PASSWORD_USED_TOO_RECENTLY"><i:inline
					key="yukon.web.modules.passwordPolicy.history.description"
					arguments="${passwordPolicy.passwordHistory}" /></li>
		</c:if>
		<li class="PASSWORD_DOES_NOT_MEET_POLICY_QUALITY"><i:inline
				key="yukon.web.modules.passwordPolicy.quality.description"
				arguments="${passwordPolicy.passwordQualityCheck}" /></li>
		<li>

			<ul class="no_fail">
				<c:forEach items="${passwordPolicy.policyRules}" var="rule">
					<li class="${rule}"><i:inline
							key="${rule.formatKey}.description" /></li>
				</c:forEach>
			</ul>
		</li>
	</ul>
</div>