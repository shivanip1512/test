<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<div class="detail stacked form-control">
    <cti:msg2 key=".rfnChannelConfiguration.enabledChannels.helpText" />
</div>
<div class="stacked">
    <div class="user-message info js-reporting dn"><i:inline key=".rfnChannelConfiguration.error.reportingExceeded"/></div>
    <div class="user-message info js-midnight dn"><i:inline key=".rfnChannelConfiguration.error.midnightExceeded"/></div>
</div>

<cti:displayForPageEditModes modes="VIEW">
    <c:forEach var="input" items="${categoryEditBean.channelInputs}" varStatus="loopStatus">
        <c:if test="${categoryEditBean.channelInputs[loopStatus.index].read != 'DISABLED' }">
            <div class="clearfix stacked form-control">
                <cti:msg2 key="${input.attribute}" />
                <div class="fr">
                    <cti:msg2 key="${categoryEditBean.channelInputs[loopStatus.index].read}" />
                </div>
            </div>
        </c:if>
    </c:forEach>
</cti:displayForPageEditModes>

<cti:displayForPageEditModes modes="EDIT,CREATE">
    <spring:bind path="channelInputs">
        <c:if test="${status.error}">
            <form:errors path="channelInputs" cssClass="error" element="div" />
        </c:if>
    </spring:bind>
    <c:forEach var="input" items="${categoryEditBean.channelInputs}" varStatus="loopStatus">
        <div class="clearfix stacked form-control">
            <cti:msg2 key="${input.attribute}" />
            <div class="button-group button-group-toggle fr">
                <c:forEach var="readType" items="${field.readTypes}">
                    <c:set var="clazz" value="yes" />
                    <c:if test="${readType == 'DISABLED' }">
                        <c:set var="clazz" value="no" />
                    </c:if>
                    <c:if test="${categoryEditBean.channelInputs[loopStatus.index].read == readType }">
                        <c:set var="clazz" value=" ${clazz} on" />
                    </c:if>
                    <cti:msg2 key="${readType}" var="readLabel" />
                    <c:choose>
                        <c:when test="${input.attribute.intervalApplicable || readType != 'INTERVAL'}"> 
                            <cti:button label="${readLabel}" classes="${clazz}" 
                                data-channel="${loopStatus.index}" data-value="${readType}" />
                        </c:when>
                        <c:when test="${!input.attribute.intervalApplicable && readType == 'INTERVAL'}"> 
                            <cti:msg2 var="addTitle" key=".rfnChannelConfiguration.disabledInterval.title"/>
                            <cti:button label="${readLabel}" disabled="true" 
                                data-channel="${loopStatus.index}" data-value="${readType}" title="${addTitle}" classes="peakPoint" />
                        </c:when>
                    </c:choose>
                </c:forEach>
                <spring:bind path="channelInputs[${loopStatus.index}].read" htmlEscape="true">
                    <input type="hidden" name="${status.expression}" data-channel-read value="${status.value}" data-input />
                </spring:bind>
            </div>
            <spring:bind path="channelInputs[${loopStatus.index}].attribute" htmlEscape="true">
                <input type="hidden" name="${status.expression}" value="${status.value}"/>
            </spring:bind>
        </div>
    </c:forEach>
</cti:displayForPageEditModes>