<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator, modules.operator.controlAudit">

<cti:msg2 var="newOpLabel" key=".newOperation"/>
<cti:msg2 var="downloadLabel" key=".download"/>

<c:if test="${fn:length(audit.controlled) > 0}">
    <c:set var="percent" value="${fn:length(audit.controlled) / fn:length(inventoryCollection.list)}"/>
    <tags:sectionContainer2 nameKey="controlledDevices"  hideEnabled="true" styleClass="stacked cl">
        <div class="form-control stacked">
            <span class="name">
                <i:inline key="yukon.common.Devices"/>:
                <span class="badge badge-controlled">${fn:length(audit.controlled)}</span> 
                (<fmt:formatNumber pattern="##0.###%" value="${percent}" type="percent" maxFractionDigits="3"/>)
            </span>
            <div class="dib fr">
                <cti:url var="url" value="/stars/operator/inventory/controlAudit/new-action">
                    <cti:param name="auditId" value="${audit.taskId}"/>
                    <cti:param name="type" value="CONTROLLED"/>
                </cti:url>
                <cti:button href="${url}" label="${newOpLabel}" icon="icon-cog-go"/>
                <cti:url var="url" value="/stars/operator/inventory/controlAudit/download">
                    <cti:param name="auditId" value="${audit.taskId}"/>
                    <cti:param name="type" value="CONTROLLED"/>
                </cti:url>
                <cti:button href="${url}" label="${downloadLabel}" icon="icon-page-white-excel"/>
            </div>
        </div>
        <cti:url var="url" value="/stars/operator/inventory/controlAudit/page">
            <cti:param name="auditId" value="${audit.taskId}"/>
            <cti:param name="type" value="CONTROLLED"/>
        </cti:url>
        <div data-url="${url}">
            <dr:controlAuditResult result="${audit.controlledPaged}" type="CONTROLLED" auditId="${audit.taskId}"/>
        </div>
    </tags:sectionContainer2>
</c:if>

<c:if test="${fn:length(audit.uncontrolled) > 0}">
    <c:set var="percent" value="${fn:length(audit.uncontrolled) / fn:length(inventoryCollection.list)}"/>
    <tags:sectionContainer2 nameKey="uncontrolledDevices"  hideEnabled="true" styleClass="stacked cl">
        <div class="form-control stacked">
            <span class="name">
                <i:inline key="yukon.common.Devices"/>:
                <span class="badge badge-uncontrolled">${fn:length(audit.uncontrolled)}</span> 
                (<fmt:formatNumber pattern="##0.###%" value="${percent}" type="percent" maxFractionDigits="3"/>)
            </span>
            <div class="dib fr">
                <cti:url var="url" value="/stars/operator/inventory/controlAudit/new-action">
                    <cti:param name="auditId" value="${audit.taskId}"/>
                    <cti:param name="type" value="UNCONTROLLED"/>
                </cti:url>
                <cti:button href="${url}" label="${newOpLabel}" icon="icon-cog-go"/>
                <cti:url var="url" value="/stars/operator/inventory/controlAudit/download">
                    <cti:param name="auditId" value="${audit.taskId}"/>
                    <cti:param name="type" value="UNCONTROLLED"/>
                </cti:url>
                <cti:button href="${url}" label="${downloadLabel}" icon="icon-page-white-excel"/>
            </div>
        </div>
        <cti:url var="url" value="/stars/operator/inventory/controlAudit/page">
            <cti:param name="auditId" value="${audit.taskId}"/>
            <cti:param name="type" value="UNCONTROLLED"/>
        </cti:url>
        <div data-url="${url}">
            <dr:controlAuditResult result="${audit.uncontrolledPaged}" type="UNCONTROLLED" auditId="${audit.taskId}"/>
        </div>
    </tags:sectionContainer2>
</c:if>

<c:if test="${fn:length(audit.unknown) > 0}">
    <c:set var="percent" value="${fn:length(audit.unknown) / fn:length(inventoryCollection.list)}"/>
    <tags:sectionContainer2 nameKey="unknownDevices"  hideEnabled="true" styleClass="stacked cl">
        <div class="form-control stacked">
            <span class="name">
                <i:inline key="yukon.common.Devices"/>:
                <span class="badge badge-unknown">${fn:length(audit.unknown)}</span> 
                (<fmt:formatNumber pattern="##0.###%" value="${percent}" type="percent" maxFractionDigits="3"/>)
            </span>
            <div class="dib fr">
                <cti:url var="url" value="/stars/operator/inventory/controlAudit/new-action">
                    <cti:param name="auditId" value="${audit.taskId}"/>
                    <cti:param name="type" value="UNKNOWN"/>
                </cti:url>
                <cti:button href="${url}" label="${newOpLabel}" icon="icon-cog-go"/>
                <cti:url var="url" value="/stars/operator/inventory/controlAudit/download">
                    <cti:param name="auditId" value="${audit.taskId}"/>
                    <cti:param name="type" value="UNKNOWN"/>
                </cti:url>
                <cti:button href="${url}" label="${downloadLabel}" icon="icon-page-white-excel"/>
            </div>
        </div>
        <cti:url var="url" value="/stars/operator/inventory/controlAudit/page">
            <cti:param name="auditId" value="${audit.taskId}"/>
            <cti:param name="type" value="UNKNOWN"/>
        </cti:url>
        <div data-url="${url}">
            <dr:controlAuditResult result="${audit.unknownPaged}" type="UNKNOWN" auditId="${audit.taskId}"/>
        </div>
    </tags:sectionContainer2>
</c:if>

<c:if test="${fn:length(audit.unsupported) > 0}">
    <c:set var="percent" value="${fn:length(audit.unsupported) / fn:length(inventoryCollection.list)}"/>
    <tags:sectionContainer2 nameKey="unsupportedDevices"  hideEnabled="true" styleClass="stacked cl">
        <div class="form-control stacked">
            <span class="name">
                <i:inline key="yukon.common.Devices"/>:
                <span class="badge  badge-unsupported">${fn:length(audit.unsupported)}</span> 
                (<fmt:formatNumber pattern="##0.###%" value="${percent}" type="percent" maxFractionDigits="3"/>)
            </span>
            <div class="dib fr">
                <cti:url var="url" value="/stars/operator/inventory/controlAudit/new-action">
                    <cti:param name="auditId" value="${audit.taskId}"/>
                    <cti:param name="type" value="UNSUPPORTED"/>
                </cti:url>
                <cti:button href="${url}" label="${newOpLabel}" icon="icon-cog-go"/>
                <cti:url var="url" value="/stars/operator/inventory/controlAudit/download">
                    <cti:param name="auditId" value="${audit.taskId}"/>
                    <cti:param name="type" value="UNSUPPORTED"/>
                </cti:url>
                <cti:button href="${url}" label="${downloadLabel}" icon="icon-page-white-excel"/>
            </div>
        </div>
        <cti:url var="url" value="/stars/operator/inventory/controlAudit/page">
            <cti:param name="auditId" value="${audit.taskId}"/>
            <cti:param name="type" value="UNSUPPORTED"/>
        </cti:url>
        <div data-url="${url}">
            <dr:controlAuditResult result="${audit.unsupportedPaged}" type="UNSUPPORTED" auditId="${audit.taskId}"/>
        </div>
    </tags:sectionContainer2>
</c:if>

</cti:msgScope>