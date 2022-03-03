<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.common,yukon.web.modules.operator.commChannelInfoWidget">
    <div class="js-global-error">
        <c:if test="${not empty uniqueErrorMsg}">
            <c:forEach var="globalErrorMsg" items="${uniqueErrorMsg}">
                <tags:alertBox>${fn:escapeXml(globalErrorMsg)}</tags:alertBox>
            </c:forEach>
        </c:if>
    </div>
    <cti:url var="action" value="/stars/device/commChannel/save" />
    <form:form modelAttribute="commChannel" action="${action}" method="post" cssClass="commChannel-create-form">
        <cti:csrfToken />
        <input type="hidden" name="commChannel" value="${commChannel.deviceType}">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input id="js-comm-channel-name" path="deviceName" maxlength="60" inputClass="w300 wrbw dib"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".type">
                <tags:selectWithItems items="${webSupportedCommChannelTypes}" id="js-comm-channel-type" path="deviceType"/>
            </tags:nameValue2>
            <div id="js-commChannel-container">
                <%@ include file="../../widget/commChannelInfoWidget/configuration.jsp" %>
            </div>
        </tags:nameValueContainer2>
    </form:form>
    <cti:includeScript link="/resources/js/common/yukon.comm.channel.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.assets.commChannel.js"/>
</cti:msgScope>