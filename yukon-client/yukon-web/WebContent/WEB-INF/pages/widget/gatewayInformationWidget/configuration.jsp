<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">

<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>
<c:if test="${empty gateway.data.ipv6Prefix}"><tags:alertBox type="info"><i:inline key=".configureMessage"/></tags:alertBox></c:if>

<cti:url var="url" value="/widget/gatewayInformationWidget/configure">
    <cti:param name="deviceId" value="${deviceId}"/>
    <cti:param name="shortName" value="gatewayInformationWidget"/>
</cti:url>

<form:form id="gateway-configuration-form" action="${url}" method="POST" modelAttribute="configuration">
    <cti:csrfToken/>
    <tags:hidden path="id" />
    <tags:nameValueContainer2>
        
        <tags:nameValue2 nameKey=".ipv6prefix">
            <div class="column-6-6-6-6 clearfix">
                <tags:input id="ipv6prefix" path="ipv6Prefix" inputClass="dn"/>
                <input id="ipv6-1" type="text" class="js-ipv6-update" maxlength="4" size="4"/> :
                <input id="ipv6-2" type="text" class="js-ipv6-update" maxlength="4" size="4"/> :
                <input id="ipv6-3" type="text" class="js-ipv6-update" maxlength="4" size="4"/> :
                <input id="ipv6-4" type="text" class="js-ipv6-update" maxlength="4" size="4" /> ::/64
            </div>
        </tags:nameValue2>

    </tags:nameValueContainer2>

</form:form>

</cti:msgScope>

<cti:includeScript link="/resources/js/widgets/yukon.widget.gateway.info.js"/>