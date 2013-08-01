<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<dialog:ajaxPage okEvent="yukonDialogSubmit" module="tools" nameKey="addSupportedTypes" page="configs.config" 
    id="supportedTypePopup"
    options="{ 'width' : '988' }">
    <form:form commandName="configurationDeviceTypesBackingBean" action="addSupportedTypes">
        <form:hidden path="configId"/>

        <div class="stacked">
            <i:inline key=".directions"/>
        </div>
        
        <h3 class="stacked"><i:inline key=".supportedTypes"/></h3>
        
        <div class="column_8_8_8">
            <div class="column one">
                <tags:sectionContainer2 nameKey="meter" styleClass="stacked">
                    <div class="column_12_12">
                        <div class="column one">
                            <%-- FOR EVENS --%>
                            <c:forEach items="${meters}" begin="0" step="2" end="${fn:length(meters)}" var="meter" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${configurationDeviceTypesBackingBean.supportedTypes[meter]}"/>
                                        <tags:checkbox path="supportedTypes[${meter}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(meter.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="column two nogutter">
                            <%-- FOR ODDS --%>
                            <c:forEach items="${meters}" begin="1" step="2" end="${fn:length(meters)}" var="meter" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${configurationDeviceTypesBackingBean.supportedTypes[meter]}"/>
                                        <tags:checkbox path="supportedTypes[${meter}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(meter.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </tags:sectionContainer2>
            </div>
            <div class="column two">
                <tags:sectionContainer2 nameKey="rtu" styleClass="stacked">
                    <div class="column_12_12">
                        <div class="column one">
                            <%-- FOR EVENS --%>
                            <c:forEach items="${rtus}" begin="0" step="2" end="${fn:length(rtus)}" var="rtu" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${configurationDeviceTypesBackingBean.supportedTypes[rtu]}"/>
                                        <tags:checkbox path="supportedTypes[${rtu}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(rtu.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="column two nogutter">
                            <%-- FOR ODDS --%>
                            <c:forEach items="${rtus}" begin="1" step="2" end="${fn:length(rtus)}" var="rtu" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${configurationDeviceTypesBackingBean.supportedTypes[rtu]}"/>
                                        <tags:checkbox path="supportedTypes[${rtu}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(rtu.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </tags:sectionContainer2>
            </div>
            <div class="column three nogutter">
                <tags:sectionContainer2 nameKey="cbc" styleClass="stacked">
                    <div class="column_12_12">
                        <div class="column one">
                            <%-- FOR EVENS --%>
                            <c:forEach items="${cbcs}" begin="0" step="2" end="${fn:length(cbcs)}" var="cbc" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${configurationDeviceTypesBackingBean.supportedTypes[cbc]}"/>
                                        <tags:checkbox path="supportedTypes[${cbc}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(cbc.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="column two nogutter">
                            <%-- FOR ODDS --%>
                            <c:forEach items="${cbcs}" begin="1" step="2" end="${fn:length(cbcs)}" var="cbc" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${configurationDeviceTypesBackingBean.supportedTypes[cbc]}"/>
                                        <tags:checkbox path="supportedTypes[${cbc}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(cbc.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </tags:sectionContainer2>
            </div>
        </div>
    </form:form>
</dialog:ajaxPage>