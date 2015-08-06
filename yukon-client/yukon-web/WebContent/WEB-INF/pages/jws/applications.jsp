<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="jreUrl" value="${jreInstaller != null ? '/static/JRE/'.concat(jreInstaller) : 'http://www.java.com/getjava/'}"/>

<div class="stacked js-close">
    <c:set var="installJreLink">
        <a class="js-close" href="${jreUrl}" target="_blank">
            <i:inline key=".applications.installJava"/>
        </a>
    </c:set>
    <span class="notes">
        <cti:msg2 key=".applications.description" argument="${installJreLink}" htmlEscapeArguments="false"/>
    </span>
</div>

<div  style="display:inline-block; float:left; width:50%;">
<cti:checkRolesAndProperties value="DATABASE_EDITOR">
    <div class="stacked js-close clearfix">
        <a href="<cti:url value="/jws/dbeditor.jnlp"/>">
            <i class="icon icon-32 icon-32-database-editor"></i>Database Editor
        </a>
    </div>
</cti:checkRolesAndProperties>

<cti:checkRolesAndProperties value="TABULAR_DISPLAY_CONSOLE">
    <div class="stacked js-close clearfix">
        <a href="<cti:url value="/jws/tdc.jnlp"/>" target-title="Tabular TDC">
            <i class="icon icon-32 icon-32-tdc"></i>Tabular Data Console
        </a>
    </div>
</cti:checkRolesAndProperties>
</div>

<div style="display:inline-block; float:left; width:50%;">
<cti:checkRolesAndProperties value="TRENDING">
    <div class="stacked js-close clearfix">
        <a href="<cti:url value="/jws/trending.jnlp"/>">
            <i class="icon icon-32 icon-32-trending"></i>Trending
        </a>
    </div>
</cti:checkRolesAndProperties>

<cti:checkRolesAndProperties value="ENABLE_CLIENT_COMMANDER">
    <div class="stacked js-close clearfix">
        <a href="<cti:url value="/jws/commander.jnlp"/>">
            <i class="icon icon-32 icon-32-commander"></i>Commander
        </a>
    </div>
</cti:checkRolesAndProperties>
</div>