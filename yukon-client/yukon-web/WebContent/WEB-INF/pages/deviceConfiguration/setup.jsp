<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<cti:msgScope paths="modules.tools.configs.config">
<div id="supportedTypePopup">
    <form:form id="supported-types-form" modelAttribute="deviceConfigTypes" action="addSupportedTypes">
        <cti:csrfToken/>
        <form:hidden path="configId"/>

        <div class="stacked">
            <i:inline key=".directions"/>
        </div>
        
        <h3 class="stacked"><i:inline key=".supportedTypes"/></h3>
        
        <div class="column-8-8-8">
            <div class="column one">
                <tags:sectionContainer2 nameKey="meter" styleClass="stacked">
                    <div class="column-12-12">
                        <div class="column one">
                            <%-- FOR EVENS --%>
                            <c:forEach items="${meters}" begin="0" step="2" var="meter" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${deviceConfigTypes.supportedTypes[meter]}"/>
                                        <tags:checkbox path="supportedTypes[${meter}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(meter.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="column two nogutter">
                            <%-- FOR ODDS --%>
                            <c:forEach items="${meters}" begin="1" step="2" var="meter" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${deviceConfigTypes.supportedTypes[meter]}"/>
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
                    <div class="column-12-12">
                        <div class="column one">
                            <%-- FOR EVENS --%>
                            <c:forEach items="${rtus}" begin="0" step="2" var="rtu" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${deviceConfigTypes.supportedTypes[rtu]}"/>
                                        <tags:checkbox path="supportedTypes[${rtu}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(rtu.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="column two nogutter">
                            <%-- FOR ODDS --%>
                            <c:forEach items="${rtus}" begin="1" step="2" var="rtu" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${deviceConfigTypes.supportedTypes[rtu]}"/>
                                        <tags:checkbox path="supportedTypes[${rtu}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(rtu.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="regulator" styleClass="stacked">
                    <c:forEach items="${regulators}" var="regulator" varStatus="status">
                        <div class="category">
                            <label>
                                <c:set var="disabled" value="${deviceConfigTypes.supportedTypes[regulator]}"/>
                                <tags:checkbox path="supportedTypes[${regulator}]" disabled="${disabled}"/>
                                <span class="checkBoxLabel"><i:inline key="${regulator}"/></span>
                            </label>
                        </div>
                    </c:forEach>
                </tags:sectionContainer2>
            </div>
            <div class="column three nogutter">
                <tags:sectionContainer2 nameKey="cbc" styleClass="stacked">
                    <div class="column-12-12">
                        <div class="column one">
                            <%-- FOR EVENS --%>
                            <c:forEach items="${cbcs}" begin="0" step="2" var="cbc" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${deviceConfigTypes.supportedTypes[cbc]}"/>
                                        <tags:checkbox path="supportedTypes[${cbc}]" disabled="${disabled}"/>
                                        <span class="checkBoxLabel">${fn:escapeXml(cbc.dbString)}</span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="column two nogutter">
                            <%-- FOR ODDS --%>
                            <c:forEach items="${cbcs}" begin="1" step="2" var="cbc" varStatus="status">
                                <div class="category">
                                    <label>
                                        <c:set var="disabled" value="${deviceConfigTypes.supportedTypes[cbc]}"/>
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
</div>
</cti:msgScope>