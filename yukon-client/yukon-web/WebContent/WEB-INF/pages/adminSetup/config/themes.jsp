<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="adminSetup" page="config.themes">

<cti:includeScript link="JQUERY_UI_WIDGET"/>
<cti:includeScript link="JQUERY_IFRAME_TRANSPORT"/>
<cti:includeScript link="JQUERY_FILE_UPLOAD"/>

<tags:setFormEditMode mode="${mode}"/>

<cti:msg2 key="yukon.common.choose" var="chooseText"/>
<cti:msg2 key="yukon.common.okButton" var="okText"/>
<cti:msg2 key="yukon.common.cancel" var="cancelText"/>

<cti:includeScript link="/JavaScript/yukon.themes.js"/>
<script type="text/javascript">
jQuery(function() {
    Yukon.Themes.init({chooseText: '${chooseText}',
                       okText: '${okText}',
                       cancelText: '${cancelText}'});
});
</script>

<style>
.side-tabs {
    list-style: none;
    padding-left: 0;
    text-shadow: 1px 1px 0 white;
}
.side-tabs li {
    padding: 5px;
}
.side-tabs li.selected {
}
.side-tabs li:not(:last-child) {
    border-bottom: 1px dashed #ccc;
}
.side-tabs li.selected a {
    color: #555;
    cursor: default;
}
.side-tabs li:not(.selected):hover {
    background-color: #fff;
    border-right: none;
}
.cp-spectrum.sp-container, .cp-spectrum.sp-replacer {border: 1px solid #bbb;border-radius: 2px;}
.cp-spectrum .sp-palette {width: 180px;}
.cp-spectrum .sp-picker-container {width: 200px;}
.cp-spectrum .sp-picker-container .sp-input-container {width: 100%;}
.cp-spectrum .sp-slider {height: 5px;}
.cp-spectrum .sp-button-container {width:100%;} 
.cp-spectrum .sp-button-container .sp-cancel {display: none;}
.cp-spectrum .sp-button-container .sp-choose {
    background: #F3F3F3;
    border: solid 1px #bbb;
    border-radius: 2px;
    color: #444;
    display: inline-block;
    float: right;
    font: bold 12px Helvetica, Arial, sans-serif;
    font-smoothing: antialiased;
    height: 26px;
    line-height: 16px;
    margin: 3px 5px;
    padding: 4px 8px;
    text-decoration: none;
    text-shadow: 0 1px 0 #fff;
    transition: border-color 0.2s;
}

.image-picker .image.selected, .image-picker .image:hover {box-shadow: 0 0 8px #06C;cursor: pointer;}
</style>

<form id="file-upload" class="dn" action="/common/images" method="post">
    <input type="file" name="file" multiple>
</form>

    <div class="clearfix">
        <div class="category fl">
            <a href="theme" class="icon icon-32 fl icon-32-brush"></a>
            <div class="fl meta">
                <div><a class="title" href="/adminSetup/config/theme"><i:inline key="yukon.common.setting.subcategory.THEMES"/></a></div>
                <div class="detail"><i:inline key="yukon.common.setting.subcategory.THEMES.description"/></div>
            </div>
        </div>
    </div>
    
    <div class="column_6_18">
        
        <div class="column one">
            <tags:sectionContainer title="Themes">
                <ul class="side-tabs">
                    <c:forEach var="theme" items="${themes}">
                        <li class="<c:if test="${theme.currentTheme}">current</c:if> <c:if test="${command.themeId == theme.themeId}">selected</c:if>">
                            <div><a href="/adminSetup/config/themes/${theme.themeId}">${fn:escapeXml(theme.name)}</a></div>
                            <c:if test="${theme.currentTheme}">
                                <div class="detail">(current theme)</div>
                            </c:if>
                        </li>
                    </c:forEach>
                    <li>
                        <div class="pageActionArea">
                            <cti:button nameKey="create" icon="icon-plus-green" href="/adminSetup/config/themes/create"/>
                        </div>
                    </li>
                </ul>
            </tags:sectionContainer>
        </div>
    
        <div class="column two nogutter">
            <tags:sectionContainer title="Settings">
                
                <c:if test="${mode == 'EDIT'}">
                    <c:set var="action" value="/adminSetup/config/themes/${command.themeId}"/>
                    <c:set var="method" value="PUT"/>
                </c:if>
                <c:if test="${mode == 'CREATE'}">
                    <c:set var="action" value="/adminSetup/config/themes/"/>
                    <c:set var="method" value="POST"/>
                </c:if>
                
                <form:form id="theme-form" action="${action}" method="${method}">
                    <tags:hidden path="themeId"/>
                    <tags:hidden path="editable"/>
                    <tags:hidden path="currentTheme"/>
                    
                    <div class="separated-sections">
                        <div class="section">
                            <div class="column_8_16 clearfix">
                                <div class="column one">
                                    Theme Name:
                                </div>
                                <div class="column two nogutter">
                                    <tags:input path="name"/>
                                </div>
                            </div>
                        </div>
                        
                        <c:forEach items="${mappedPropertiesHelper.mappableProperties}" var="prop">
                            <div class="section">
                                <div class="column_8_16 clearfix">
                                    <div class="column one">
                                        <i:inline key="${prop.extra}"/>:
                                    </div>
                                    <div class="column two nogutter">
                                        <tags:simpleInputType id="${prop.extra}" input="${prop.valueType}" path="${prop.path}" pageEditMode="${mode}"/>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    
                    <div class="pageActionArea">
                        <c:if test="${mode == 'VIEW' and !command.currentTheme}">
                                <cti:url value="/adminSetup/config/themes/${command.themeId}/use" var="useUrl"/>
                                <cti:button nameKey="use" icon="icon-tick" href="${useUrl}"/>
                        </c:if>
                        
                        <c:if test="${command.editable and mode == 'VIEW'}">
                            <cti:url value="/adminSetup/config/themes/${command.themeId}/edit" var="editUrl"/>
                            <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
                        </c:if>
                        
                        <c:if test="${command.editable and (mode == 'EDIT' or mode == 'CREATE')}">
                            <cti:button nameKey="save" classes="primary action" type="submit"/>
                            
                            <c:if test="${mode == 'EDIT' and !command.currentTheme}">
                                <cti:button id="b-delete" nameKey="delete" classes="delete"/>
                            </c:if>
                            
                            <cti:url value="/adminSetup/config/themes/${cancelId}" var="cancelUrl"/>
                            <cti:button nameKey="cancel" href="${cancelUrl}"/>
                        </c:if>
                        
                        <c:if test="${mode == 'VIEW'}">
                            <cti:url value="/adminSetup/config/themes/${command.themeId}/copy" var="copyUrl"/>
                            <cti:button nameKey="copy" icon="icon-page-copy" href="${copyUrl}"/>
                        </c:if>
                    </div>
                    
                </form:form>
                
            </tags:sectionContainer>
        </div>
    
    </div>
        

</cti:standardPage>