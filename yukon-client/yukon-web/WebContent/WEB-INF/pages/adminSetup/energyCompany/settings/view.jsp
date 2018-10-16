<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="energyCompanySettings">

<c:set var="legend"><cti:icon icon="icon-asterisk-orange"/>&nbsp;<i:inline key=".legend"/></c:set>

<form:form modelAttribute="settingsBean" action="save" id="settingsForm" method="post">
    <cti:csrfToken/>
    <form:hidden path="ecId"/>
    
    <c:forEach items="${categories}" var="category">

        <cti:msg2 key="${category}" var="title"/>
        <tags:sectionContainer title="${title}" controls="${legend}">
        
            <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="setting">
                <c:if test="${category eq setting.extra.category}">
                    <div class="setting">
                        <form:hidden path="settings[${setting.extra}].type"/>
                        
                        <div class="column-6-12-6 clearfix">
                            <div class="column one">
                                <div class="stacked"><i:inline key="${setting.extra}"/></div>
                                <div class="detail hint">
                                    <c:if test="${not empty settingsBean.settings[setting.extra].lastChanged}">
                                        <i:inline key=".updated"/>:&nbsp;<cti:formatDate type="DATEHM" value="${settingsBean.settings[setting.extra].lastChanged}"/>
                                    </c:if>
                                </div>
                            </div>
                            <div class="column two">
                                <div class="default-indicator">
                                    <c:if test="${settingsBean.settings[setting.extra].nonDefault}">
                                        <cti:icon icon="icon-asterisk-orange"/>
                                    </c:if>
                                </div>
                                <div class="value-description">
                                    <div class="value">
                                        <c:if test="${setting.extra.usesEnabledField}">
                                            <form:select cssClass="js-setUnsetSelect" path="settings['${setting.extra}'].enabled">
                                                <form:option value="true"><cti:msg2 key="yukon.common.enabled"/></form:option>
                                                <form:option value="false"><cti:msg2 key="yukon.common.disabled"/></form:option>
                                            </form:select>
                                            <form:errors path="settings['${setting.extra}'].enabled" cssClass="error" element="span"/>
                                        </c:if>
                                        <c:if test="${!setting.extra.usesEnabledField}">
                                            <form:hidden path="settings['${setting.extra}'].enabled"/>
                                        </c:if>
                                        <tags:simpleInputType id="${setting.extra}" input="${setting.valueType}" path="${setting.path}"/>
                                    </div>
                                    <div class="description detail">
                                        <p>
                                            <i:inline key="yukon.common.energyCompanySetting.${setting.extra}.description"/>
                                            <c:if test="${settingsBean.settings[setting.extra].nonDefault}">
                                                <span class="default"><i:inline key=".default"/>:&nbsp;(${fn:escapeXml(setting.extra.defaultValue)})</span>
                                             </c:if>
                                            <form:errors path="${setting.path}" cssClass="error" element="div"/>
                                        </p>
                                    </div>
                                </div>
                            </div>
                            <div class="column three nogutter comments">
                                <form:textarea rows="3" cols="27" 
                                    placeholder="comments" 
                                    id="${setting.extra}_comments" 
                                    path="settings[${setting.extra}].comments"
                                    class="${status.error ? 'error' : ''}"/> 
                                <form:errors path="settings[${setting.extra}].comments" cssClass="error" element="div"/>
                            </div>
                        </div>
                    </div>
                    
                </c:if>
            </c:forEach>
        </tags:sectionContainer>
    </c:forEach>
    <div class="page-action-area stickyPanel" style="min-height: 24px;">
        <cti:button nameKey="save" name="save" type="submit" classes="primary action"/>
        <cti:button nameKey="cancel" name="cancel" href="view?ecId=${ecId}"/>
    </div>
</form:form>
</cti:standardPage>