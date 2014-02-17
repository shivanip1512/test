<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug"%>

<cti:standardPage module="support" page="uiDemos">

<script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js"></script>
<cti:includeScript link="JQUERY_COOKIE"/>

<script type="text/javascript">
jQuery(function() {
    jQuery('#tabs').tabs({'class': 'section'});
});
</script>

<div id="tabs" class="section">
    <ul>
        <li><a href="#containerTab">Containers</a></li>
        <li><a href="#buttonsTab">Buttons</a></li>
        <li><a href="#pickersTab">Pickers</a></li>
        <li><a href="#i18nTab">I18N</a></li>
        <li><a href="#i18nScopesTab">I18N Scopes</a></li>
        <li><a href="#dialogsTab">Dialogs</a></li>
        <li><a href="#jsTestingTab">JS Testing</a></li>
        <li><a href="#moreTab">More</a></li>
    </ul>

    <div id="containerTab" class="clearfix">
        <%@ include file="containers.jspf" %>
    </div>

    <div id="buttonsTab" class="clearfix">
        <%@ include file="buttons.jspf" %>
    </div>

    <div id="pickersTab" class="clearfix">
        <%@ include file="pickers.jspf" %>
    </div>

    <div id="i18nTab" class="clearfix">
        <%@ include file="i18n.jspf" %>
    </div>

    <div id="i18nScopesTab" class="clearfix">
        <%@ include file="i18n_scopes.jspf" %>
    </div>

    <div id="dialogsTab" class="clearfix">
        <%@ include file="dialogs.jspf" %>
    </div>

    <div id="jsTestingTab" class="clearfix">
        <%@ include file="jsTesting.jspf" %>
    </div>

    <div id="moreTab" class="clearfix">
        <%@ include file="more.jspf" %>
    </div>
</div>

</cti:standardPage>