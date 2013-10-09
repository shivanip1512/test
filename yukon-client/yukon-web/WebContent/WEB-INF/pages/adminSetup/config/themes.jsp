<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="adminSetup" page="config.themes">
<tags:setFormEditMode mode="${mode}"/>

<cti:msg2 key="yukon.common.choose" var="chooseText"/>

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

<script type="text/javascript">

function initColorPickers() {
    jQuery('.f-color-input').each(function(idx, item) {
        var item = jQuery(item);
        var color = item.val();
        item.spectrum({
            color: color,
            showInput: true,
            className: "cp-spectrum",
            chooseText: "${chooseText}",
            showInitial: true,
            showAlpha: true,
            showPalette: true,
            showSelectionPalette: true,
            maxPaletteSize: 10,
            preferredFormat: "hex",
            localStorageKey: "yukon.spectrum",
            change: function() {
                
            },
            palette: [
                ["rgb(0, 0, 0)", "rgb(67, 67, 67)", "rgb(102, 102, 102)",
                "rgb(204, 204, 204)", "rgb(217, 217, 217)","rgb(255, 255, 255)"],
                ["rgb(152, 0, 0)", "rgb(255, 0, 0)", "rgb(255, 153, 0)", "rgb(255, 255, 0)", "rgb(0, 255, 0)",
                "rgb(0, 255, 255)", "rgb(74, 134, 232)", "rgb(0, 0, 255)", "rgb(153, 0, 255)", "rgb(255, 0, 255)"], 
                ["rgb(230, 184, 175)", "rgb(244, 204, 204)", "rgb(252, 229, 205)", "rgb(255, 242, 204)", "rgb(217, 234, 211)", 
                "rgb(208, 224, 227)", "rgb(201, 218, 248)", "rgb(207, 226, 243)", "rgb(217, 210, 233)", "rgb(234, 209, 220)", 
                "rgb(221, 126, 107)", "rgb(234, 153, 153)", "rgb(249, 203, 156)", "rgb(255, 229, 153)", "rgb(182, 215, 168)", 
                "rgb(162, 196, 201)", "rgb(164, 194, 244)", "rgb(159, 197, 232)", "rgb(180, 167, 214)", "rgb(213, 166, 189)", 
                "rgb(204, 65, 37)", "rgb(224, 102, 102)", "rgb(246, 178, 107)", "rgb(255, 217, 102)", "rgb(147, 196, 125)", 
                "rgb(118, 165, 175)", "rgb(109, 158, 235)", "rgb(111, 168, 220)", "rgb(142, 124, 195)", "rgb(194, 123, 160)",
                "rgb(166, 28, 0)", "rgb(204, 0, 0)", "rgb(230, 145, 56)", "rgb(241, 194, 50)", "rgb(106, 168, 79)",
                "rgb(69, 129, 142)", "rgb(60, 120, 216)", "rgb(61, 133, 198)", "rgb(103, 78, 167)", "rgb(166, 77, 121)",
                "rgb(91, 15, 0)", "rgb(102, 0, 0)", "rgb(120, 63, 4)", "rgb(127, 96, 0)", "rgb(39, 78, 19)", 
                "rgb(12, 52, 61)", "rgb(28, 69, 135)", "rgb(7, 55, 99)", "rgb(32, 18, 77)", "rgb(76, 17, 48)"]
            ]
        });
    });
}

jQuery(function() {
    initColorPickers();
    jQuery('#b-delete').click(function(e){
        jQuery('input[name=_method]').val('DELETE');
        jQuery('#theme-form').submit();
    });
    
    jQuery('a[data-image-picker]').click(function(e) {
        
        var link = jQuery(e.currentTarget),
            href = link.attr('href'),
            popup = jQuery('div[data-image-picker=' + link.attr('data-image-picker') + ']');
        
        popup.load(href, function() {
            var buttons = [],
                okButton = {'text' : '<cti:msg2 key="yukon.common.okButton"/>', 'click': function() { popup.trigger('yukon.image.selected'); }, 'class': 'primary'},
                cancelButton = {'text' : '<cti:msg2 key="yukon.common.cancel"/>', 'click' : function() { jQuery(this).dialog('close'); }};
            
            buttons.push(cancelButton);
            buttons.push(okButton);
            popup.dialog({ autoOpen: false,
                           height: 400, 
                           width:600,
                           modal : true,
                           buttons : buttons });
            popup.dialog('open');
        });
        
        return false;
    });
});

//if i don't listen for this event it doesn't work, god knows why
jQuery(document).on('show.spectrum', 'input', function(e) {
    e.preventDefault();
});
jQuery(document).on('yukon.image.selected', '[data-image-picker]', function(e) {
    var imgPicker = jQuery(e.currentTarget),
        selected = imgPicker.find('.image.selected').data('imageId'),
        input = jQuery('#' + imgPicker.data('imagePicker')),
        link = input.next();
        
    imgPicker.dialog('close');
    input.val(selected);
    link.attr('href', '/adminSetup/config/themes/imagePicker?category=logos&selected=' + selected);
    link.find('img').attr('alt', selected).attr('src', '/common/images/' + selected);
});
</script>

<div class="box clear dashboard">

    <div class="clearfix box">
        <div class="category fl">
            <a href="theme" class="icon icon-32 fl icon-32-brush"></a>
            <div class="box fl meta">
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
        
</div>

</cti:standardPage>