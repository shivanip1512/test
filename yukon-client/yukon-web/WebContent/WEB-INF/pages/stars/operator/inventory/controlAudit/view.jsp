<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="controlAudit">
<cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>

<script type="text/javascript">
jQuery(function() {
    jQuery(document).on('click', '.f-ajaxPaging', function(event) {
        
        event.stopPropagation();
        var url = jQuery(event.currentTarget).data('url');
        jQuery.ajax({
            url: url,
            method: 'get',
            success: function(data) {
                var parent = jQuery(event.currentTarget).closest(".one");
                parent.html(data);
            }
        });
        return false;
    });
});
</script>

<div class="column_12_12">
    <div class="column one">
        <tags:formElementContainer nameKey="settings">
            <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
            <form:form commandName="settings" action="runAudit">
                <cti:inventoryCollection inventoryCollection="${settings.collection}"/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey="yukon.common.from">
                        <dt:dateTime path="from" value="${settings.from}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey="yukon.common.To">
                        <dt:dateTime path="to" value="${settings.to}"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="pageActionArea"><cti:button type="submit" nameKey="runAudit"/></div>
            </form:form>
        </tags:formElementContainer>
    </div>
    <div class="column two nogutter">
        <c:if test="${not empty auditId}">
            <!-- Pie Chart -->
            <c:url var="chartUrl" scope="page" value="/stars/operator/inventory/controlAudit/chart">
                <c:param name="auditId" value="${auditId}"/>
            </c:url>
            <div style="max-height: 220px;"><!-- flot chart makes two boxes for some reason -->
                <flot:ajaxChart url="${chartUrl}"/>
            </div>
        </c:if>
    </div>
</div>

<c:if test="${fn:length(audit.controlledRows) > 0}">
    <tags:sectionContainer2 nameKey="controlledDevices" styleClass="stacked cl" hideEnabled="true">
        <div class="column_18_6">
                <div class="column one">
                    <dr:controlAuditResult result="${audit.controlledPaged}" type="CONTROLLED" auditId="${auditId}"/>
                </div>
                <div class="column two nogutter">
                    <tags:nameValueContainer2 tableClass="sectionContainerNameValue stats">
                        <c:set var="percent" value="${fn:length(audit.controlledRows) / fn:length(settings.collection.list)}"/>
                        <tags:nameValue2 nameKey=".deviceCount">${fn:length(audit.controlledRows)} (<fmt:formatNumber value="${percent}" type="percent" maxFractionDigits="1"/>)</tags:nameValue2>
                    </tags:nameValueContainer2>
                    <ul class="labeledImageStack">
                        <li class="clearfix">
                            <cti:url var="newOperationControlled" value="newOperation">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="CONTROLLED"/>
                            </cti:url>
                            <a href="${newOperationControlled}" class="button naked"><i class="icon icon-cog-go"></i><span class="label"><i:inline key=".newOperation"/></span></a>
                        </li>
                        <li class="clearfix">
                            <cti:url var="download" value="download">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="CONTROLLED"/>
                            </cti:url>
                            <a href="${download}" class="button naked"><i class="icon icon-page-excel"></i><span class="label"><i:inline key=".download"/></span></a>
                        </li>
                    </ul>
                </div>
        </div>
    </tags:sectionContainer2>    
</c:if>

<c:if test="${fn:length(audit.uncontrolledRows) > 0}">
    <tags:sectionContainer2 nameKey="uncontrolledDevices"  hideEnabled="true" styleClass="stacked cl">
        <div class="column_18_6">
                <div class="column one">
                    <dr:controlAuditResult result="${audit.uncontrolledPaged}" type="UNCONTROLLED" auditId="${auditId}"/>
                </div>
                <div class="column two nogutter">
                    <tags:nameValueContainer2>
                        <c:set var="percent" value="${fn:length(audit.uncontrolledRows) / fn:length(settings.collection.list)}"/>
                        <tags:nameValue2 nameKey=".deviceCount">${fn:length(audit.uncontrolledRows)} (<fmt:formatNumber value="${percent}" type="percent" maxFractionDigits="1"/>)</tags:nameValue2>
                    </tags:nameValueContainer2>
                    <ul class="labeledImageStack">
                        <li class="clearfix">
                            <cti:url var="newOperationUncontrolled" value="newOperation">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNCONTROLLED"/>
                            </cti:url>
                            <a href="${newOperationUncontrolled}" class="button naked"><i class="icon icon-cog-go"></i><span class="label"><i:inline key=".newOperation"/></span></a>
                        </li>
                        <li class="clearfix">
                            <cti:url var="download" value="download">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNCONTROLLED"/>
                            </cti:url>
                            <a href="${download}" class="button naked"><i class="icon icon-page-excel"></i><span class="label"><i:inline key=".download"/></span></a>
                        </li>
                    </ul>
                </div>
        </div>
    </tags:sectionContainer2>
</c:if>
    
<c:if test="${fn:length(audit.unknownRows) > 0}">
    <tags:sectionContainer2 nameKey="unknownDevices" hideEnabled="true" styleClass="stacked cl">
        <div class="column_18_6">
                <div class="column one">
                    <dr:controlAuditResult result="${audit.unknownPaged}" type="UNKNOWN" auditId="${auditId}"/>
                </div>
                <div class="column two nogutter">
                    <tags:nameValueContainer2>
                        <c:set var="percent" value="${fn:length(audit.unknownRows) / fn:length(settings.collection.list)}"/>
                        <tags:nameValue2 nameKey=".deviceCount">${fn:length(audit.unknownRows)} (<fmt:formatNumber value="${percent}" type="percent" maxFractionDigits="1"/>)</tags:nameValue2>
                    </tags:nameValueContainer2>
                    <ul class="labeledImageStack">
                        <li class="clearfix">
                            <cti:url var="newOperationUnknown" value="newOperation">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNKNOWN"/>
                            </cti:url>
                            <a href="${newOperationUnknown}" class="button naked"><i class="icon icon-cog-go"></i><span class="label"><i:inline key=".newOperation"/></span></a>
                        </li>
                        <li class="clearfix">
                            <cti:url var="download" value="download">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNKNOWN"/>
                            </cti:url>
                            <a href="${download}" class="button naked"><i class="icon icon-page-excel"></i><span class="label"><i:inline key=".download"/></span></a>
                        </li>
                    </ul>
                </div>
        </div>
    </tags:sectionContainer2>
</c:if>

<c:if test="${fn:length(audit.unsupportedRows) > 0}">
    <tags:sectionContainer2 nameKey="unsupportedDevices" styleClass="stacked cl" hideEnabled="true">
        <div class="column_18_6">
                <div class="column one">
                    <dr:controlAuditResult result="${audit.unsupportedPaged}" type="UNSUPPORTED" auditId="${auditId}"/>
                </div>
                <div class="column two nogutter">
                    <tags:nameValueContainer2 tableClass="sectionContainerNameValue stats">
                        <c:set var="percent" value="${fn:length(audit.unsupportedRows) / fn:length(settings.collection.list)}"/>
                        <tags:nameValue2 nameKey=".deviceCount">${fn:length(audit.unsupportedRows)} (<fmt:formatNumber value="${percent}" type="percent" maxFractionDigits="1"/>)</tags:nameValue2>
                    </tags:nameValueContainer2>
                    <ul class="labeledImageStack">
                        <li class="clearfix">
                            <cti:url var="newOperationControlled" value="newOperation">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNSUPPORTED"/>
                            </cti:url>
                            <a href="${newOperationControlled}" class="button naked"><i class="icon icon-cog-go"></i><span class="label"><i:inline key=".newOperation"/></span></a>
                        </li>
                        <li class="clearfix">
                            <cti:url var="download" value="download">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNSUPPORTED"/>
                            </cti:url>
                            <a href="${download}" class="button naked"><i class="icon icon-page-excel"></i><span class="label"><i:inline key=".download"/></span></a>
                        </li>
                    </ul>
                </div>
        </div>
    </tags:sectionContainer2>
</c:if>
    
</cti:standardPage>