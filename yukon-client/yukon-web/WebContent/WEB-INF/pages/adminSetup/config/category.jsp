<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="config.category">
<cti:includeScript link="JQUERY_STICKY_PANEL" />
<%--Following script and style are for showing the save/cancel actions when the user has changed something
       without making them scroll all the way down the page. Ran into and issue when the page wasn't tall 
       enough to scroll.  We can revisit this idea when we have time.
<script>
jQuery(function(){
    jQuery("#settingsForm :input").each(function(idx, elem){
                //'Remember' the unmodified value
                jQuery(elem).data({original_value: jQuery(elem).val()});
    });
    jQuery("#settingsForm :input").blur(function(event){
                //check to see if *any* value changed - this is not very efficient, but short of tracking some global var there really isn't a good way to determine if the form has been modified.
                var inputs = jQuery("#settingsForm :input");
                for(i=0; i<inputs.length; i++){
                    var oldValue = jQuery(inputs[i]).data('original_value');
                    if(typeof(oldValue) != "undefined" && (jQuery(inputs[i]).val() != oldValue)){
                        jQuery("#settingsForm .pageActionArea > div button").enable();
                        return;
                    } else {
                        jQuery("#settingsForm .pageActionArea > div button").disable();
                        return;
                    }
                }
                jQuery("#settingsForm .pageActionArea > div").removeClass("fixed");
    });
    
});
</script>

<style>
.pageActionArea .fixed {
    background: white;
    position:fixed;
    bottom: 0px;
    padding: 10px;
    border: solid 1px #ccc;
    border-style: solid solid none;
    -moz-border-radius: 2px 2px 0, 0;
    -webkit-border-radius: 2px 2px 0, 0;
    border-radius: 2px 2px 0, 0;
    -moz-box-shadow: 0 -5px 10px rgba(0,0,0,0.3);
    -webkit-box-shadow: 0 -5px 10px rgba(0,0,0,0.3);
    box-shadow: 0 -5px 10px rgba(0,0,0,0.3);
}
</style>
--%>

<div class="centeredContainer twoThirds box clear dashboard">
    <form:form action="/spring/adminSetup/config/update" id="settingsForm" method="post">
        <form:hidden path="category"/>
        
        <div class="category">
            <cti:url value="edit" var="category_url"><cti:param name="category" value="${category}"/></cti:url>
            <a href="${category_url}" class="medium_icon fl ${category_icon}"></a>
            <div class="box fl meta">
                <a class="title" href="javascript:void(0);"><i:inline key="yukon.common.setting.subcategory.${category}"/></a>
                <br/>
                <span class="detail"><i:inline key="yukon.common.setting.subcategory.${category}.description"/></span>
            </div>
        </div>
        
        <div class="default_indicator_legend secondary_emphasis"><i:inline key=".legend"/></div>
        
        <div class="box liteContainer">
        
            <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="setting" varStatus="loopStatus">
                
                <c:choose>
                    <c:when test="${loopStatus.first and not loopStatus.last}"><c:set var="rowClass" value="first"/></c:when>
                    <c:when test="${loopStatus.last}"><c:set var="rowClass" value="last"/></c:when>
                    <c:otherwise><c:set var="rowClass" value="middle"/></c:otherwise>
                </c:choose>
                
                <div class="setting box ${rowClass}">
                    <div class="setting_name box fl"><i:inline key="${setting.extra.type}"/></div>
                    <div class="setting_default_indicator fl">
                        <span class="secondary_emphasis">
                            <c:if test="${setting.extra.nonDefault}">
                                <i:inline key=".nonDefault"/>
                            </c:if>
                        </span>
                    </div>
                    <div class="setting_details box detail fl">
                        <div>
                            <tags:simpleInputType id="${setting.extra.type}" input="${setting.valueType}" path="${setting.path}"/>
                            <span class="detail updated"><i:inline key=".updated"/>&nbsp;
                                <span>
                                    <c:choose>
                                        <c:when test="${not empty setting.extra.lastChanged}">
                                            <cti:formatDate type="DATEHM" value="${setting.extra.lastChanged}"/>
                                        </c:when>
                                        <c:otherwise><i:inline key=".never"/></c:otherwise>
                                    </c:choose>
                                </span>
                            </span>
                        </div>
                        <div class="description">
                            <span class="detail"><i:inline key="yukon.common.setting.${setting.extra.type}.description"/></span>
                            <span class="detail">
                                <i:inline key=".default"/>
                                <span class="default">(${fn:escapeXml(setting.extra.type.defaultValue)})</span>
                                <form:errors path="${setting.path}" cssClass="errorMessage" element="div"/>
                            </span>
                        </div>
                    </div>
                    <div class="setting_comments box fl">
                        <spring:bind path="comments[${setting.extra.type}]" htmlEscape="true">
                            <c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
                            <form:textarea rows="3" cols="27" 
                                placeholder="comments"
                                id="${setting.extra.type}_comments"
                                path="comments[${setting.extra.type}]"
                                class="${inputClass}"/>
                        </spring:bind>
                    </div>
                </div>
                
            </c:forEach>
        </div>
        <div class="pageActionArea stickyPanel" style="min-height: 24px;">
            <div>
                <cti:button nameKey="save" name="save" type="submit"/>&nbsp;
                <cti:button nameKey="cancel" name="cancel" href="view"/>
            </div>
        </div>
    </form:form>
</div>

</cti:standardPage>