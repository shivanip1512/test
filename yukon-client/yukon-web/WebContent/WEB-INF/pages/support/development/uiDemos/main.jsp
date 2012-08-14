<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="debug" tagdir="/WEB-INF/tags/debug"%>

<cti:standardPage module="support" page="uiDemos">

<cti:includeScript link="JQUERY_COOKIE"/>

<script type="text/javascript">
jQuery(function() {
    jQuery('#tabs').tabs({'cookie' : {}});
});
</script>

<div id="tabs">
    <ul>
        <li><a href="#pickersTab">Pickers</a></li>
        <li><a href="#i18nTab">I18N</a></li>
        <li><a href="#i18nScopesTab">I18N Scopes</a></li>
        <li><a href="#dialogsTab">Dialogs</a></li>
        <li><a href="#moreTab">More</a></li>
    </ul>

    <div id="pickersTab">
        <%@ include file="pickers.jspf" %>
    </div>

    <div id="i18nTab">
        <%@ include file="i18n.jspf" %>
    </div>

    <div id="i18nScopesTab">
        <%@ include file="i18n_scopes.jspf" %>
    </div>

    <div id="dialogsTab">
        <%@ include file="dialogs.jspf" %>
    </div>

    <div id="moreTab">
        <%@ include file="more.jspf" %>
    </div>
</div>

</cti:standardPage>
