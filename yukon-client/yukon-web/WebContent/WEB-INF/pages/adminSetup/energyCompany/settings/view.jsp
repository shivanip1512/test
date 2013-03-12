<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="energyCompanySettings">

<div class="leftContainer twoThirds box dashboard">
    <form:form commandName="settingsBean" action="save" id="settingsForm" method="post">
        <form:hidden path="ecId"/>

            <h2 class="standardPageHeading"><i:inline key=".pageTitle"/></h2>
            <div class="highlight fr clear"><i:inline key=".legend"/></div>
            <c:forEach items="${categories}" var="category">

           <div class='categoryTitle'><i:inline key="${category}"/></div>
                <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="setting">
                    <c:if test="${category eq setting.extra.category}">
                        <form:hidden path="settings[${setting.extra}].type"/>
                        <div class="setting box">
                            <div class="setting_name box fl"><i:inline key="${setting.extra}"/></div>
                            <div class="setting_default_indicator fl">
                                <span class="highlight">
                                    <c:if test="${settingsBean.settings[setting.extra].nonDefault}">
                                        <i:inline key=".nonDefault"/>
                                    </c:if>
                                </span>
                            </div>
                            <div class="setting_details box detail fl">
                                <div>
                                    <c:if test="${setting.extra.usesEnabledField}">
                                        <form:select cssClass="f_setUnsetSelect" path="settings['${setting.extra}'].enabled">
                                            <form:option value="true"><i:inline key="yukon.common.enabled"/></form:option>
                                            <form:option value="false"><i:inline key="yukon.common.disabled"/></form:option>
                                        </form:select>
                                        <form:errors path="settings['${setting.extra}'].enabled" cssClass="errorMessage" element="span"/>
                                    </c:if>
                                    <c:if test="${!setting.extra.usesEnabledField}">
                                        <form:hidden path="settings['${setting.extra}'].enabled"/>
                                    </c:if>
                                    <tags:simpleInputType id="${setting.extra}" input="${setting.valueType}" path="${setting.path}"/>
                                    <span class="detail updated fr">
                                        <c:if test="${not empty settingsBean.settings[setting.extra].lastChanged}">
                                            <i:inline key=".updated"/>:&nbsp;<cti:formatDate type="DATEHM" value="${settingsBean.settings[setting.extra].lastChanged}"/>
                                        </c:if>
                                    </span>
                                </div>
                                <div class="description">
                                    <form:errors path="${setting.path}" cssClass="errorMessage" element="div"/>
                                    <c:if test="${settingsBean.settings[setting.extra].nonDefault}">
                                        <span class="detail">
                                            <div class="default"></em><i:inline key=".default"/>:&nbsp;(${fn:escapeXml(setting.extra.defaultValue)})</div>
                                        </span>
                                     </c:if>
                                    <span class="detail"><i:inline key="yukon.common.energyCompanySetting.${setting.extra}.description"/></span>
                                </div>
                            </div>
                            <div class="setting_comments box fl">
                                    <c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
                                    <c:set var="style" value="${empty settingsBean.settings[setting.extra].comments ? 'display:none' : ''}"/>
                                    <c:if test="${empty settingsBean.settings[setting.extra].comments}">
                                        <cti:button styleClass="f_addCommentBtn" nameKey="energyCompanySettings.addComment" type="button" name="add" value="${setting.extra}"  renderMode="labeledImage"/>
                                    </c:if>

                                    <form:textarea rows="3" cols="27" 
                                         placeholder="comments" 
                                         id="${setting.extra}_comments" 
                                         path="settings[${setting.extra}].comments"
                                         style="${style}"
                                         class="commentTextArea ${inputClass}"/> 
                                   <form:errors path="settings[${setting.extra}].comments" cssClass="errorMessage" element="div"/>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </c:forEach>
        <div class="pageActionArea stickyPanel" style="min-height: 24px;">
            <div>
                <cti:button nameKey="save" name="save" type="submit"/>&nbsp;
                <cti:button nameKey="cancel" name="cancel" href="view?ecId=${ecId}"/>
            </div>
        </div>
    </form:form>
</div>
<script>
jQuery(function() {
    jQuery(".f_addCommentBtn").click(function() {
        jQuery(this).hide();
        jQuery(this).siblings(".commentTextArea").show(200).focus();
    });
});

</script>
        
</cti:standardPage>