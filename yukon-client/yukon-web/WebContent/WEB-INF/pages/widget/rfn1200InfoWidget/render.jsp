<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.common,yukon.web.modules.operator.commChannelInfoWidget">
    <tags:setFormEditMode mode="${mode}" />
    <c:if test="${not empty errorMsg}"><tags:alertBox>${fn:escapeXml(errorMsg)}</tags:alertBox></c:if>
    <cti:url var="saveUrl" value="/widget/rfn1200InfoWidget/save"/>
    <form:form modelAttribute="rfn1200" method="post" action="${saveUrl}" id="rfn1200-info-form">
        <cti:csrfToken />
        <cti:msg2 var="milliseconds" key=".units.ms"/>
        <c:choose>
            <c:when test="${not empty rfn1200}">
                <tags:hidden path="id"/>
                <tags:hidden path="paoType"/>
                <input type="hidden" id="isRfn1200" value="true"/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".name">
                        <tags:input id="js-comm-channel-name" path="name" maxlength="60" inputClass="w300 wrbw dib"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".type">
                        <cti:displayForPageEditModes modes="CREATE">
                            <tags:selectWithItems items="${webSupportedCommChannelTypes}" id="js-comm-channel-type" path="paoType"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW,EDIT">
                            <i:inline key="${rfn1200.paoType}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".serialNumber">
                        <tags:input path="rfnAddress.serialNumber" maxlength="30"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".manufacturer">
                        <tags:input path="rfnAddress.manufacturer" maxlength="60"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".model">
                        <tags:input path="rfnAddress.model" maxlength="60"/>  
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".postCommWait">
                        <tags:input path="postCommWait" units="${milliseconds}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".status">
                        <tags:switchButton path="enabled" offNameKey=".disabled.label" onNameKey=".enabled.label"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </c:when>
            <c:otherwise>
                <span class="empty-list"><i:inline key=".search.noResultsFound"/></span>
            </c:otherwise>
        </c:choose>
    </form:form>
    <cti:displayForPageEditModes modes="VIEW">
        <div class="action-area">
             <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE"> 
                <cti:url var="editUrl" value="/widget/rfn1200InfoWidget/${rfn1200.id}/edit"/>
                <cti:msg2 var="saveText" key="components.button.save.label"/>
                <cti:msg2 var="editPopupTitle" key=".editWithName" argument="${rfn1200.name}"/>
                <c:if test="${not empty rfn1200}">
                    <cti:button icon="icon-pencil"
                                nameKey="edit"
                                data-popup="#js-edit-rfn1200-popup"
                                id="edit-btn"/>
                </c:if>
             </cti:checkRolesAndProperties> 
        </div>
        <div class="dn" 
             id="js-edit-rfn1200-popup" 
             data-title="${editPopupTitle}" 
             data-dialog
             data-ok-text="${saveText}" 
             data-width="555"
             data-height="450"
             data-event="yukon:assets:rfn1200:save" 
             data-url="${editUrl}"/>
        </div>
    </cti:displayForPageEditModes>
    
    <cti:includeScript link="/resources/js/pages/yukon.assets.commChannel.js"/>
</cti:msgScope>
