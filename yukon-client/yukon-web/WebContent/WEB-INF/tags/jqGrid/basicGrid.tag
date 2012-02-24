<%@ attribute name="columnInfo" required="true" type="java.util.ArrayList"%>
<%@ attribute name="dataUrl" required="true" type="java.lang.String"%>
<%@ attribute name="height" required="false" type="java.lang.Integer"%>
<%@ attribute name="width" required="false" type="java.lang.Integer"%>
<%@ attribute name="showLoadMask" required="false" type="java.lang.String" description="Possible values: 'enable', 'disable', 'block'"%>
<%@ attribute name="refreshRate" required="false" type="java.lang.Integer" description="ms between refreshes.  0 Represents NO REFRESH." %>

<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="csvUrl" required="false" type="java.lang.String"%>
<%@ attribute name="pdfUrl" required="false" type="java.lang.String"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:uniqueIdentifier var="gridId" prefix="gridId_"/>

<%--jqGrid --%>
<cti:includeScript link="JQUERY"/>
<cti:includeScript link="JQUERY_GRID"/>
<cti:includeScript link="JQUERY_GRID_HELPER"/>
<link rel="stylesheet" type="text/css" media="screen" href="/JavaScript/lib/jQuery/plugins/jqGrid/4.2.0/css/ui.jqgrid.css" />


<c:if test="${empty pageScope.showLoadMask}">
    <c:set var="showLoadMask" value="block"/>
</c:if>

<c:if test="${empty pageScope.refreshRate}">
    <c:set var="refreshRate" value="0"/>
</c:if>

<script type="text/javascript">
    jQuery(document).ready(function(){
        jqGridHelper.createGrid({
            id: "${gridId}",
            colModel:  ${cti:jsonString(columnInfo)},
            
            <c:if test="${not empty pageScope.height}">
                height: ${pageScope.height},
            </c:if>
            <c:if test="${not empty pageScope.width}">
                width: ${pageScope.width},
            </c:if>
                
            loadui: '${showLoadMask}',
            refreshRate: ${pageScope.refreshRate},
            title: '${title}',
            toolbar: [true, "top"],
            url: "${cti:escapeJavaScript(dataUrl)}"
        });
    });
</script>

<table id="${gridId}"><tr><td/></tr></table>

<div class="dn">
    <div id="${gridId}_toolbar_buttons">
        <div class="fl">
            <a href="#" class="refresh" data-grid-id="${gridId}"><i:inline key="components.refresh" /></a>
        </div>
        <c:if test="${not empty csvUrl or not empty pdfUrl}">
            <div class="fr">
                <i:inline key="components.export"/>
                <a class="excel" href="${cti:escapeJavaScript(csvUrl)}"><i:inline key="components.csv"/></a>
                <a class="pdf" href="${cti:escapeJavaScript(pdfUrl)}"><i:inline key="components.pdf"/></a>
            </div>
        </c:if>
    </div>
</div>