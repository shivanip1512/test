<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="controlAudit">
<cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>

<script type="text/javascript">
jQuery(function() {
    jQuery(document).delegate(".f_ajaxPaging", "click", function(event) {
        
        event.stopPropagation();
        
        jQuery.ajax({
            url: event.currentTarget.href,
            method: 'get',
            success: function(data) {
                var parent = jQuery(event.currentTarget).closest(".primary");
                parent.html(data);
            }
        });
        return false;
    });
});
</script>

<cti:dataGrid cols="2" tableClasses="marginBottom twoColumnLayout split">

    <cti:dataGridCell>
        <tags:formElementContainer nameKey="settings">
            <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
            <form:form commandName="settings" action="runAudit">
                <cti:inventoryCollection inventoryCollection="${settings.collection}"/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".range">
                        <span class="fl"><dt:dateTime path="from" value="${settings.from}"/></span>
                        <span class="rangeSeparator fl"><i:inline key="yukon.common.to"/></span>
                        <span class="fl"><dt:dateTime path="to" value="${settings.to}"/></span>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <div class="pageActionArea"><cti:button type="submit" nameKey="runAudit"/></div>
            </form:form>
        </tags:formElementContainer>
    </cti:dataGridCell>
    
    <c:if test="${not empty auditId}">
        <cti:dataGridCell>
            <!-- Pie Chart -->
            <c:set var="amChartsProduct" value="ampie"/>
            <c:url var="amDataFile" scope="page" value="/spring/stars/operator/inventory/controlAudit/chartData?auditId=${auditId}"/>
            <c:url var="amSettingsFile" scope="page" value="/spring/stars/operator/inventory/controlAudit/chartSettings"/>
            <c:url var="amSrc" scope="page" value="/JavaScript/amChart/${amChartsProduct}.swf">
                <c:param name="${amChartsProduct}_path" value="/JavaScript/amChart/" />
                <c:param name="${amChartsProduct}_flashWidth" value="100%" />
                <c:param name="${amChartsProduct}_flashHeight" value="100%" />
                <c:param name="${amChartsProduct}_preloaderColor" value="#000000" />
                <c:param name="${amChartsProduct}_settingsFile" value="${amSettingsFile}" />
                <c:param name="${amChartsProduct}_dataFile" value="${amDataFile}" />
            </c:url>
            
            <c:url var="expressInstallSrc" scope="page" value="/JavaScript/expressinstall.swf" />
            <cti:includeScript link="/JavaScript/swfobject.js"/>
    
            <cti:uniqueIdentifier var="uniqueId" prefix="flashDiv_"/>
            <div id="${uniqueId}">
                <div style="width:90%;text-align:center;">
                    <br>
                    <br>
                    <h4><i:inline key="yukon.common.adobeRequired"/></h4>
                    <br>
                    <i:inline key="yukon.common.adobePleaseDL"/>
                    <br>
                    <br>
                    <a href="http://www.adobe.com" target="_blank"><img border="0" src="<c:url value="/WebConfig/yukon/Icons/visitadobe.gif"/>" /></a>
                    <br>
                </div>
            </div>
            
            <script type="text/javascript">
               var so = new SWFObject("${amSrc}", "dataGraph", "400", "230", "8", "#FFFFFF");
               so.useExpressInstall('${expressInstallSrc}');
               so.write("${uniqueId}");
            </script>
        </cti:dataGridCell>
    </c:if>
    
</cti:dataGrid>

<c:if test="${fn:length(audit.uncontrolledRows) > 0}">
    <tags:formElementContainer nameKey="uncontrolledDevices" styleClass="box cl marginBottom" hideEnabled="true">
        <div class="threeQuarterLayout">
            <div class="primary">
                <dr:controlAuditResult result="${audit.uncontrolledPaged}" type="UNCONTROLLED" auditId="${auditId}"/>
            </div>
            <div class="secondary">
                <div class="columnContent">
                    <tags:nameValueContainer2 tableClass="sectionContainerNameValue stats marginBottom">
                        <c:set var="percent" value="${fn:length(audit.uncontrolledRows) / fn:length(settings.collection.list)}"/>
                        <tags:nameValue2 nameKey=".deviceCount">${fn:length(audit.uncontrolledRows)} (<fmt:formatNumber value="${percent}" type="percent" maxFractionDigits="1"/>)</tags:nameValue2>
                    </tags:nameValueContainer2>
                    <ul class="labeledImageStack">
                        <li>
                            <cti:url var="newOperationControlled" value="newOperation">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNCONTROLLED"/>
                            </cti:url>
                            <a href="${newOperationControlled}" class="small icon_cog_go labeled_icon"><i:inline key=".newOperation"/></a>
                        </li>
                        <li>
                            <cti:url var="download" value="download">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNCONTROLLED"/>
                            </cti:url>
                            <a href="${download}" class="small icon_csv labeled_icon"><i:inline key=".download"/></a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </tags:formElementContainer>
</c:if>
    
<c:if test="${fn:length(audit.unknownRows) > 0}">
    <tags:formElementContainer nameKey="unknownDevices" styleClass="box cl marginBottom" hideEnabled="true">
        <div class="threeQuarterLayout">
            <div class="primary">
                <dr:controlAuditResult result="${audit.unknownPaged}" type="UNKNOWN" auditId="${auditId}"/>
            </div>
            <div class="secondary">
                <div class="columnContent">
                        <tags:nameValueContainer2 tableClass="sectionContainerNameValue stats marginBottom">
                            <c:set var="percent" value="${fn:length(audit.unknownRows) / fn:length(settings.collection.list)}"/>
                            <tags:nameValue2 nameKey=".deviceCount">${fn:length(audit.unknownRows)} (<fmt:formatNumber value="${percent}" type="percent" maxFractionDigits="1"/>)</tags:nameValue2>
                        </tags:nameValueContainer2>
                        <ul class="labeledImageStack">
                            <li>
                                <cti:url var="newOperationControlled" value="newOperation">
                                    <cti:param name="auditId" value="${audit.auditId}"/>
                                    <cti:param name="type" value="UNKNOWN"/>
                                </cti:url>
                                <a href="${newOperationControlled}" class="small icon_cog_go labeled_icon"><i:inline key=".newOperation"/></a>
                            </li>
                            <li>
                                <cti:url var="download" value="download">
                                    <cti:param name="auditId" value="${audit.auditId}"/>
                                    <cti:param name="type" value="UNKNOWN"/>
                                </cti:url>
                                <a href="${download}" class="small icon_csv labeled_icon"><i:inline key=".download"/></a>
                            </li>
                        </ul>
                </div>
            </div>
        </div>
    </tags:formElementContainer>
</c:if>

<c:if test="${fn:length(audit.controlledRows) > 0}">
    <tags:formElementContainer nameKey="controlledDevices" styleClass="box marginBottom" hideEnabled="true">
        <div class="threeQuarterLayout">
            <div class="primary">
                <dr:controlAuditResult result="${audit.controlledPaged}" type="CONTROLLED" auditId="${auditId}"/>
            </div>
            <div class="secondary">
                <div class="columnContent">
                    <tags:nameValueContainer2 tableClass="sectionContainerNameValue stats marginBottom">
                        <c:set var="percent" value="${fn:length(audit.controlledRows) / fn:length(settings.collection.list)}"/>
                        <tags:nameValue2 nameKey=".deviceCount">${fn:length(audit.controlledRows)} (<fmt:formatNumber value="${percent}" type="percent" maxFractionDigits="1"/>)</tags:nameValue2>
                    </tags:nameValueContainer2>
                    <ul class="labeledImageStack">
                        <li>
                            <cti:url var="newOperationControlled" value="newOperation">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="CONTROLLED"/>
                            </cti:url>
                            <a href="${newOperationControlled}" class="small icon_cog_go labeled_icon"><i:inline key=".newOperation"/></a>
                        </li>
                        <li>
                            <cti:url var="download" value="download">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="CONTROLLED"/>
                            </cti:url>
                            <a href="${download}" class="small icon_csv labeled_icon"><i:inline key=".download"/></a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </tags:formElementContainer>    
</c:if>
    
<c:if test="${fn:length(audit.unsupportedRows) > 0}">
    <tags:formElementContainer nameKey="unsupportedDevices" styleClass="box cl marginBottom" hideEnabled="true">
        <div class="threeQuarterLayout">
            <div class="primary">
                <dr:controlAuditResult result="${audit.unsupportedPaged}" type="UNSUPPORTED" auditId="${auditId}"/>
            </div>
            <div class="secondary">
                <div class="columnContent">
                    <tags:nameValueContainer2 tableClass="sectionContainerNameValue stats marginBottom">
                        <c:set var="percent" value="${fn:length(audit.unsupportedRows) / fn:length(settings.collection.list)}"/>
                        <tags:nameValue2 nameKey=".deviceCount">${fn:length(audit.unsupportedRows)} (<fmt:formatNumber value="${percent}" type="percent" maxFractionDigits="1"/>)</tags:nameValue2>
                    </tags:nameValueContainer2>
                    <ul class="labeledImageStack">
                        <li>
                            <cti:url var="newOperationControlled" value="newOperation">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNSUPPORTED"/>
                            </cti:url>
                            <a href="${newOperationControlled}" class="small icon_cog_go labeled_icon"><i:inline key=".newOperation"/></a>
                        </li>
                        <li>
                            <cti:url var="download" value="download">
                                <cti:param name="auditId" value="${audit.auditId}"/>
                                <cti:param name="type" value="UNSUPPORTED"/>
                            </cti:url>
                            <a href="${download}" class="small icon_csv labeled_icon"><i:inline key=".download"/></a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </tags:formElementContainer>
</c:if>
    
</cti:standardPage>