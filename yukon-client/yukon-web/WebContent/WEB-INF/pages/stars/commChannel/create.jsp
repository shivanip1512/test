<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.commChannelInfoWidget">
    <cti:url var="action" value="/stars/device/commChannel/save" />
    <form:form modelAttribute="commChannel" action="${action}" method="post" cssClass="commChannel-create-form">
        <cti:csrfToken />
        <input type="hidden" name="commChannel" value="${selectedCommType}">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input id="js-comm-channel-name" path="name" maxlength="60" inputClass="w300 wrbw dib"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".type">
                <tags:selectWithItems items="${commChannelTypes}" id="js-comm-channel-type" path="type"/>
            </tags:nameValue2>
            <div id='js-commChannel-container' class='noswitchtype'>
                <%@ include file="../../widget/commChannelInfoWidget/configuration.jsp" %>
            </div>
        </tags:nameValueContainer2>
    </form:form>
    <cti:includeScript link="/resources/js/pages/yukon.assets.commChannel.js"/>
</cti:msgScope>