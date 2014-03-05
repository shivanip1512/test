<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="config.category">

<script>
jQuery(function() {
    jQuery(".f-addCommentBtn").click(function() {
        jQuery(this).hide().siblings(".f-commentsShowHide").show(200).focus();
    });
});
</script>

<div class="dashboard">
    <cti:url var="updateUrl" value="/adminSetup/config/update"/>
    <form:form action="${updateUrl}" id="settingsForm" method="post">
        <form:hidden path="category"/>
        
        <div class="clearfix box">
	        <div class="category fl">
	            <cti:url value="edit" var="category_url"><cti:param name="category" value="${category}"/></cti:url>
	            <a href="${category_url}" class="icon icon-32 fl ${category_icon}"></a>
	            <div class="box fl meta">
	                <div><a class="title" href="javascript:void(0);"><i:inline key="yukon.common.setting.subcategory.${category}"/></a></div>
	                <div class="detail"><i:inline key="yukon.common.setting.subcategory.${category}.description"/></div>
	            </div>
	        </div>
	        
	        <div class="legend"><cti:icon icon="icon-asterisk-orange"/><i:inline key=".legend"/></div>
        </div>
        
        <div class="lite-container">
        
            <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="setting" varStatus="loopStatus">
                
                <div class="setting">
                    <div class="column-6-12-6 clearfix">
                        <div class="column one">
                            <i:inline key="${setting.extra.type}"/>
                        </div>
                        <div class="column two">
                            <div class="default-indicator">
                                <span class="highlight">
                                    <c:if test="${setting.extra.nonDefault}">
                                        <cti:icon icon="icon-asterisk-orange"/>
                                    </c:if>
                                </span>
                            </div>
                            <div class="value-description">
                                <div class="value">
                                    <tags:simpleInputType id="${setting.extra.type}" input="${setting.valueType}" path="${setting.path}"/>
                                    <span class="detail updated">
                                        <c:if test="${not empty setting.extra.lastChanged}">
                                            <i:inline key=".updated"/>&nbsp;<cti:formatDate type="DATEHM" value="${setting.extra.lastChanged}"/>
                                        </c:if>
                                    </span>
                                </div>
                                <div class="description detail">
                                    <p>
                                        <i:inline key="yukon.common.setting.${setting.extra.type}.description"/>
                                        <c:if test="${setting.extra.nonDefault}">
                                            <span class="default"><i:inline key=".default"/>(${fn:escapeXml(setting.extra.type.defaultValue)})</span>
                                        </c:if>
                                        <form:errors path="${setting.path}" cssClass="error" element="div"/>
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="column three nogutter comments">
                            <c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
                            <c:set var="style" value="${empty setting.extra.comments ? 'display:none' : ''}"/>
                            <c:if test="${empty setting.extra.comments}">
                                <cti:button classes="f-addCommentBtn" nameKey="energyCompanySettings.addComment" name="add" value="${setting.extra}" renderMode="labeledImage" icon="icon-pencil"/>
                            </c:if>
                            <form:textarea rows="3" 
                                    placeholder="comments"
                                    id="${setting.extra.type}_comments"
                                    path="comments[${setting.extra.type}]"
                                    style="${style}"
                                    class="f-commentsShowHide ${inputClass}"/>
                            <form:errors path="comments[${setting.extra.type}]" cssClass="error" element="div"/>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="page-action-area">
            <cti:button nameKey="save" name="save" type="submit" classes="primary action"/>
            <cti:button nameKey="cancel" name="cancel" href="view"/>
        </div>
    </form:form>
</div>

</cti:standardPage>