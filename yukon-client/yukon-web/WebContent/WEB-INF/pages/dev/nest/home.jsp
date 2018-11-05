<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="nest">
<div class="column-12-12 clearfix">
    <div class="column one">
        <b><i:inline key=".currentNestVersion.label" arguments="${currentNestVersion}"/></b>
        <br>
        <h3>Pages</h3>
        <ul class="simple-list stacked">
            <li><a href="viewNestVersion"><i:inline key=".nestVersion.label"/></a></li>
            <li><a href="viewNestFileSetting"><i:inline key=".nestFileSettings.label"/></a></li>
            <li><a href="viewNestSync"><i:inline key=".nestSync.label"/></a></li>
            <li><a href="viewControlEvents"><i:inline key=".controlEvents.label"/></a></li>
    </div>
</div>
</cti:standardPage>