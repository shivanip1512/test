<%@ attribute name="columnInfo" required="true" type="java.util.ArrayList"%>
<%@ attribute name="dataUrl" required="true" type="java.lang.String"%>
<%@ attribute name="height" required="true" type="java.lang.Integer"%>
<%@ attribute name="width" required="true" type="java.lang.Integer"%>

<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="csvUrl" required="true" type="java.lang.String"%>
<%@ attribute name="pdfUrl" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:includeCss link="/JavaScript/extjs/resources/css/grid.css"/>

<cti:includeScript link="/JavaScript/extjs/ext-base.js"/>
<cti:includeScript link="/JavaScript/extjs/ext-all.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/extGridHelper.js"/>
<cti:includeScript link="/JavaScript/extjs_cannon/extGridMaker.js"/>

<cti:uniqueIdentifier var="gridDiv" prefix="gridId_"/>

<script type="text/javascript">
    
    Ext.onReady(function(){
    
        var gridMaker = new ExtGridMaker();
        var grid = gridMaker.getReportGrid('${title}', ${height}, ${width}, ${cti:jsonString(columnInfo)}, '${cti:escapeJavaScript(dataUrl)}', '${cti:escapeJavaScript(csvUrl)}', '${cti:escapeJavaScript(pdfUrl)}');
    
        grid.render('${gridDiv}');
    });
</script>

<div id="${gridDiv}"></div>