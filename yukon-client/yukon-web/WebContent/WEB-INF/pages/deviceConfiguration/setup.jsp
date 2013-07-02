<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="tools" page="configs.setup">
    <cti:standardMenu/>
    
    <form:form commandName="configurationDeviceTypesBackingBean" action="create">
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
                                        <tags:checkbox path="supportedTypes[${meter}]"/>
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
                                        <tags:checkbox path="supportedTypes[${meter}]"/>
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
                                        <tags:checkbox path="supportedTypes[${rtu}]"/><span class="checkBoxLabel"><spring:escapeBody htmlEscape="true">${rtu.dbString}</spring:escapeBody></span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="column two nogutter">
                            <%-- FOR ODDS --%>
                            <c:forEach items="${rtus}" begin="1" step="2" end="${fn:length(rtus)}" var="rtu" varStatus="status">
                                <div class="category">
                                    <label>
                                        <tags:checkbox path="supportedTypes[${rtu}]"/><span class="checkBoxLabel"><spring:escapeBody htmlEscape="true">${rtu.dbString}</spring:escapeBody></span>
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
                                        <tags:checkbox path="supportedTypes[${cbc}]"/><span class="checkBoxLabel"><spring:escapeBody htmlEscape="true">${cbc.dbString}</spring:escapeBody></span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="column two nogutter">
                            <%-- FOR ODDS --%>
                            <c:forEach items="${cbcs}" begin="1" step="2" end="${fn:length(cbcs)}" var="cbc" varStatus="status">
                                <div class="category">
                                    <label>
                                        <tags:checkbox path="supportedTypes[${cbc}]"/><span class="checkBoxLabel"><spring:escapeBody htmlEscape="true">${cbc.dbString}</spring:escapeBody></span>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </tags:sectionContainer2>
            </div>
        </div>
        
        <div class="pageActionArea clear">
            <div>
                <cti:button nameKey="cancel" href="/deviceConfiguration/home"/>
                <cti:button nameKey="next" type="submit" classes="primary action"/>
            </div>
        </div>
    </form:form>
</cti:standardPage>