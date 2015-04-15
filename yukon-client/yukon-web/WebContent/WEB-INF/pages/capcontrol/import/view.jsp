<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="import">
<cti:includeScript link="/JavaScript/yukon.da.import.js"/>

<div class="stacked-lg">
    <cti:url var="url" value="/capcontrol/import/cbcFile"/>
    <form id="importForm" method="post" action="${url}" enctype="multipart/form-data">
        <cti:csrfToken/>
        <tags:nameValueContainer2 tableClass="with-form-controls">
            <tags:nameValue2 nameKey=".importTypeSelect">
                <select id="import-type">
                    <c:forEach var="importType" items="${importTypes}">
                        <option value="${importType}"><cti:msg2 key="${importType}"/></option>
                    </c:forEach>
                </select>
                <tags:file buttonKey="yukon.common.file"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div class="page-action-area">
            <cti:button type="submit" nameKey="import" classes="js-blocker primary action"/>
        </div>
    </form>
</div>

<c:if test="${not empty results}">
    <tags:sectionContainer2 nameKey="resultContainer">
        <div class="scroll-md">
            <ol style="list-style-type:decimal; padding-left: 35px">
                <c:forEach var="result" items="${results}">
                    <c:set var="clazz" value="${result.success ? 'success' : 'error'}"/>
                    <li>
                         <span class="${clazz}"><i:inline key="${result.importResultMessage}"/></span>
                    </li>
                </c:forEach>
            </ol>
        </div>
    </tags:sectionContainer2>
</c:if>

<c:forEach var="importType" items="${importTypes}">
    <div data-type="${importType}" class="dn">
        <cti:msg2 var="displayImportType" key="${importType}"/>
        <h2 class="dib"><i:inline key=".file.format" arguments="${displayImportType}"/></h2>
        <cti:url var="url" value="/WebConfig/custom/sample_capcontrol_files/Sample ${displayImportType} Import.csv"/>
        <a href="${url}" class="fr">
            <cti:icon icon="icon-page-white-excel"/>
            <i:inline key=".sampleFile"/>
        </a>
        <table class="full-width">
            <c:forEach var="columnType" items="${importType.columnTypes}">
                <c:if test="${not empty columnType.value}">
                    <tr>
                        <th class="wsnw"><i:inline key=".header.${columnType.key}"/></th>
                        <th><i:inline key=".header.description"/></th>
                    </tr>
                    <c:forEach var="column" items="${columnType.value}">
                        <tr>
                            <td class="name wsnw">${column}</td>
                            <td><i:inline key=".description.${importType}.${column}"/></td>
                        </tr>
                    </c:forEach>
                </c:if>
            </c:forEach>
        </table>
    </div>
</c:forEach>

</cti:standardPage>