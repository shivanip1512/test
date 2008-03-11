<%@ attribute name="columnInfo" required="true" type="java.util.ArrayList"%>
<%@ attribute name="dataUrl" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:includeCss link="/JavaScript/extjs/resources/css/ext-all.css"/>
<cti:includeCss link="/JavaScript/extjs/resources/css/xtheme-gray.css"/>
<cti:includeCss link="/JavaScript/extjs/resources/css/grid.css"/>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/ctiExtGrid.js"/>

<script type="text/javascript">
	Ext.onReady(function(){
	   initializeGrid('gridDiv', ${cti:jsonString(columnInfo)}, '${cti:escapeJavaScript(dataUrl)}');
	});
</script>

<div id="gridDiv"></div>