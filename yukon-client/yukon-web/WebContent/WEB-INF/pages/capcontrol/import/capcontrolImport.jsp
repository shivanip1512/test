<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="import">    
    <script>
    jQuery(document).ready(function(){
        jQuery("#importTypeSelector").change(function(){
            //get the import type value, remove spaces, lowercase
            var importType = jQuery('#importTypeSelector').val().replace(/_/g, '')
            importType = importType.toLowerCase();
            jQuery("#importForm").attr("action", "/spring/capcontrol/import/" + importType + "File");
        });
    });
    </script>
    
    <div style="width:50%;">
    <tags:boxContainer2 nameKey="importContainer">
        <i:inline key=".importTypeSelect"/>
        <select id="importTypeSelector">
            <c:forEach var="importType" items="${importTypes}">
                <option value="${importType}">
                    <i:inline key="${importType}"/>
                </option>
            </c:forEach>
        </select>
        <br><br>
        <form id="importForm" method="post" action="/spring/capcontrol/import/cbcFile" enctype="multipart/form-data">
            <cti:url var="folderImg" value="/WebConfig/yukon/Icons/folder_edit.gif"/>
            <img src="${folderImg}">&nbsp;<input type="file" name="dataFile"><br>
            <div style="float:right">
                <cti:button type="submit" nameKey="importSubmitButton" id="importSubmitButton" styleClass="f_blocker"/>
            </div>
        </form>
	</tags:boxContainer2>
    </div>
	
    <br>
    <c:if test="${!empty results}">
    <tags:boxContainer2 nameKey="resultContainer">
        <ol style="list-style-type:decimal; padding-left: 35px">
            <c:forEach var="result" items="${results}">
                <c:choose>
                    <c:when test="${result.success}">
                        <c:set var="fontColor" value="successMessage"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="fontColor" value="errorMessage"/>
                    </c:otherwise>
                </c:choose>
                <li>
                     <span class="${fontColor}"><i:inline key="${result.message}"/></span>
                </li>
            </c:forEach>
        </ol>
    </tags:boxContainer2>
    </c:if>
</cti:standardPage>