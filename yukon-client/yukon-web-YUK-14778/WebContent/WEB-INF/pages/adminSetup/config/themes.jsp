<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="adminSetup" page="config.themes">

<cti:includeScript link="JQUERY_UI_WIDGET"/>
<cti:includeScript link="JQUERY_IFRAME_TRANSPORT"/>
<cti:includeScript link="JQUERY_FILE_UPLOAD"/>

<cti:csrfToken var="token"/>

<style>
.theme-palette {
    border: 1px solid #bbb;
    border-collapse: separate;
    border-spacing: 2px;
    background-color: #eee;
    border-radius: 2px;
}
.theme-palette td {
    padding: 0;
    width: 3px;
    height: 3px;
}
</style>

<tags:setFormEditMode mode="${mode}"/>

<cti:msg2 key="yukon.common.choose" var="chooseText"/>
<cti:msg2 key="yukon.common.okButton" var="okText"/>
<cti:msg2 key="yukon.common.cancel" var="cancelText"/>

<cti:includeScript link="/resources/js/pages/yukon.themes.js"/>
    <input id="button-keys" type="hidden"
        data-button-keys='{
            "chooseText" : "${cti:escapeJavaScript(chooseText)}",
            "okText" : "${cti:escapeJavaScript(okText)}",
            "cancelText" : "${cti:escapeJavaScript(cancelText)}"
        }'
    />
    <div class="clearfix">
        <div class="category fl">
            <a href="<cti:url value="/admin/config/themes"/>" class="icon icon-32 fl icon-32-brush"></a>
            <div class="fl meta">
                <div><a class="title" href="<cti:url value="/admin/config/themes"/>"><i:inline key="yukon.common.setting.subcategory.THEMES"/></a></div>
                <div class="detail"><i:inline key="yukon.common.setting.subcategory.THEMES.description"/></div>
            </div>
        </div>
    </div>
    
    <div class="column-6-18">
        
        <div class="column one">
            <tags:sectionContainer2 nameKey="themes">
                <ul class="side-tabs">
                    <c:forEach var="theme" items="${themes}">
                        <li class="<c:if test="${theme.currentTheme}">current</c:if> <c:if test="${command.themeId == theme.themeId}">selected</c:if>">
                            <div class="clearfix">
                                <div class="fl dib" style="margin-right: 10px;">
                                    <table data-theme="${theme.themeId}" class="theme-palette">
                                        <tbody>
                                            <tr>
                                                <td style="background-color: red;"></td>
                                                <td style="background-color: green;"></td>
                                                <td style="background-color: blue;"></td>
                                            </tr>
                                            <tr>
                                                <td style="background-color: blue;"></td>
                                                <td style="background-color: red;"></td>
                                                <td style="background-color: green;"></td>
                                            </tr>
                                            <tr>
                                                <td style="background-color: red;"></td>
                                                <td style="background-color: green;"></td>
                                                <td style="background-color: blue;"></td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <div class="dib fl">
                                    <div>
                                        <a class="dib" href="<cti:url value="/admin/config/themes/${theme.themeId}"/>">${fn:escapeXml(theme.name)}</a>
                                    </div>
                                    <div>
                                        <c:if test="${theme.currentTheme}">
                                            <div class="detail"><i:inline key=".current"/></div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </li>
                    </c:forEach>
                    <li>
                        <div class="page-action-area">
                            <cti:url var="createUrl" value="/admin/config/themes/create"/>
                            <cti:button nameKey="create" icon="icon-plus-green" href="${createUrl}"/>
                        </div>
                    </li>
                </ul>
            </tags:sectionContainer2>
        </div>
    
        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="settings">
                
                <c:if test="${mode == 'EDIT'}">
                    <cti:url var="action" value="/admin/config/themes/${command.themeId}"/>
                    <c:set var="method" value="PUT"/>
                </c:if>
                <c:if test="${mode == 'CREATE'}">
                    <cti:url var="action" value="/admin/config/themes/"/>
                    <c:set var="method" value="POST"/>
                </c:if>
                
                <form:form id="theme-form" action="${action}" method="${method}">
                    <cti:csrfToken/>
                    <tags:hidden path="themeId"/>
                    <tags:hidden path="editable"/>
                    <tags:hidden path="currentTheme"/>
                    <input type="hidden" id="csrf-token" value="${token}">
                    
                    <div class="separated-sections">
                    
                        <div class="section">
                            <div class="column-6-18 clearfix">
                                <div class="column one">
                                    <i:inline key=".name"/>
                                </div>
                                <div class="column two nogutter">
                                    <tags:input path="name"/>
                                </div>
                            </div>
                        </div>

                        <div class="section">
                            <div class="column-6-18 clearfix">
                                <div class="column one"><i:inline key=".primaryColors"/></div>
                                <div class="column two nogutter">
                                    <div class="column-12-12 clearfix">
                                        <div class="column one">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['PRIMARY_COLOR']}"/>
                                            <div><span class="name"><i:inline key=".primary"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                        </div>
                                        <div class="column two nogutter">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['VISITED_COLOR']}"/>
                                            <div><span class="name"><i:inline key=".visited"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="section">
                            <div class="column-6-18 clearfix">
                                <div class="column one"><i:inline key=".buttons"/></div>
                                <div class="column two nogutter">
                                    <div class="column-8-8-8 clearfix">
                                        <div class="column one">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['BUTTON_COLOR']}"/>
                                            <div><span class="name"><i:inline key=".color"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                        </div>
                                        <div class="column two">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['BUTTON_COLOR_BORDER']}"/>
                                            <div><span class="name"><i:inline key=".border"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                        </div>
                                        <div class="column three nogutter">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['BUTTON_COLOR_HOVER']}"/>
                                            <div><span class="name"><i:inline key=".hover"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="section">
                            <div class="column-6-18 clearfix">
                                <div class="column one"><i:inline key=".background"/></div>
                                <div class="column two nogutter">
                                    <div class="column-8-8-8 clearfix">
                                        <div class="column one">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['PAGE_BACKGROUND']}"/>
                                            <div><span class="name"><i:inline key=".color"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                        </div>
                                        <div class="column two">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['PAGE_BACKGROUND_SHADOW']}"/>
                                            <div><span class="name"><i:inline key=".shadow"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                        </div>
                                        <div class="column three nogutter">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['PAGE_BACKGROUND_FONT_COLOR']}"/>
                                            <div><span class="name"><i:inline key=".fontColor"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="section">    
                            <div class="column-6-18 clearfix">
                                <div class="column one"><i:inline key=".login"/></div>
                                <div class="column two nogutter">
                                    <div class="column-10-14 clearfix">
                                        <div class="column one">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['LOGIN_BACKGROUND']}"/>
                                            <div>
                                                <span class="name fl"><i:inline key=".backgroundImage"/>
                                                </span>
                                                <span class="fl">
                                                    <cti:msg2 key=".background.title" var="popupTitle"/>
                                                    <tags:helpInfoPopup title="${popupTitle}"><i:inline key=".background.helpText" /></tags:helpInfoPopup>
                                                </span>
                                            </div>
                                            <div><tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                        </div>
                                        <div class="column two nogutter">
                                            <div class="column-12-12 clearfix stacked">
                                                <div class="column one">
                                                    <c:set var="prop" value="${mappedPropertiesHelper.map['LOGIN_FONT_COLOR']}"/>
                                                    <div><span class="name"><i:inline key=".fontColor"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                                    <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                                </div>
                                                <div class="column two nogutter">
                                                    <c:set var="prop" value="${mappedPropertiesHelper.map['LOGIN_FONT_SHADOW']}"/>
                                                    <div><span class="name"><i:inline key=".fontShadow"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                                    <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                                </div>
                                            </div>
                                            <div class="column-24 clearfix">
                                                <div class="column one nogutter">
                                                    <c:set var="prop" value="${mappedPropertiesHelper.map['LOGIN_TAGLINE_MARGIN']}"/>
                                                    <div><span class="name"><i:inline key=".taglineMargin"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                                    <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div class="section">    
                            <div class="column-6-18 clearfix">
                                <div class="column one"><i:inline key=".logo"/></div>
                                <div class="column two nogutter">
                                    <div class="column-10-14 clearfix">
                                        <div class="column one">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['LOGO']}"/>
                                            <div>
                                                <span class="name fl"><i:inline key=".image"/></span>
                                                <span class="fl">
                                                    <cti:msg2 key=".logo.title" var="popupTitle"/>
                                                    <tags:helpInfoPopup title="${popupTitle}"><i:inline key=".logo.helpText" /></tags:helpInfoPopup>
                                                </span>
                                            <div><tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                        </div>
                                        <div class="column two nogutter">
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['LOGO_WIDTH']}"/>
                                            <div class="stacked"><span class="name"><i:inline key=".width"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['LOGO_LEFT']}"/>
                                            <div class="stacked"><span class="name"><i:inline key=".offsetX"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                            <c:set var="prop" value="${mappedPropertiesHelper.map['LOGO_TOP']}"/>
                                            <div><span class="name"><i:inline key=".offsetY"/></span>&nbsp;<tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/></div>
                                            <form:errors path="${prop.path}" cssClass="error" element="div"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                    </div>
                    
                    <div class="page-action-area">
                        <c:if test="${mode == 'VIEW' and !command.currentTheme}">
                            <cti:url value="/admin/config/themes/${command.themeId}/use" var="useUrl"/>
                            <cti:button nameKey="use" icon="icon-tick" href="${useUrl}"/>
                        </c:if>
                        
                        <c:if test="${command.editable and mode == 'VIEW'}">
                            <cti:url value="/admin/config/themes/${command.themeId}/edit" var="editUrl"/>
                            <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
                        </c:if>
                        
                        <c:if test="${command.editable and (mode == 'EDIT' or mode == 'CREATE')}">
                            <cti:button nameKey="save" classes="primary action" type="submit"/>
                            
                            <c:if test="${mode == 'EDIT' and !command.currentTheme}">
                                <cti:button id="b-delete" nameKey="delete" classes="delete"/>
                            </c:if>
                            
                            <cti:url value="/admin/config/themes/${cancelId}" var="cancelUrl"/>
                            <cti:button nameKey="cancel" href="${cancelUrl}"/>
                        </c:if>
                        
                        <c:if test="${mode == 'VIEW'}">
                            <cti:url value="/admin/config/themes/${command.themeId}/copy" var="copyUrl"/>
                            <cti:button nameKey="copy" icon="icon-page-copy" href="${copyUrl}"/>
                        </c:if>
                    </div>
                    
                </form:form>
                
            </tags:sectionContainer2>
        </div>
    
    </div>
    <input id="palette-map" type="hidden" data-palette-map='${colorMap}' />
</cti:standardPage>