<%@ attribute name="metaObject" required="true" type="com.cannontech.web.bulk.model.MetaObject"%>
<%@ attribute name="dataUrl" required="true" type="java.lang.String"%>
<%@ attribute name="editUrl" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:includeCss link="/JavaScript/extjs/resources/css/grid.css"/>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/ctiExtEditorGrid.js"/>

<script type="text/javascript">
Event.observe(window, 'load', function() {
       initializeGrid('gridDiv', ${cti:jsonString(metaObject)}, '${cti:escapeJavaScript(dataUrl)}', '${cti:escapeJavaScript(editUrl)}');
    });
</script>

<div id="gridDiv"></div>