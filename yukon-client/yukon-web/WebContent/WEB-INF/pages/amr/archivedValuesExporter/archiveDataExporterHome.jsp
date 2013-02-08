<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage page="archivedValueExporter" module="amr">
    <cti:includeScript link="/JavaScript/yukonGeneral.js" />

    <script type="text/javascript">
        function submitForm(index, action) {
            var exporterForm = jQuery('#exporterForm');
            exporterForm.attr('action', action);
            exporterForm[0].submit();
        };

        function addFormatIdToLink(linkHref) {
        	var formatIdLink = jQuery('a[href='+linkHref+']');
        	var formatIdFragment = "?selectedFormatId="+jQuery('#formatId').val();
        	formatIdLink.attr('href', linkHref + formatIdFragment);
        };
        
        function addFormatIdToLinks() {
            addFormatIdToLink('copy');
            addFormatIdToLink('edit');
        };
        
        jQuery(document).ready(function() {
        	addFormatIdToLinks();
        	
            jQuery('.selectDevices, #selectDevicesBtn1, #selectDevicesBtn2').click(function(event) {
                submitForm(-1, 'selectDevices');
            });
            jQuery('#formatId').change(function(event) {
            	addFormatIdToLinks();
            	submitForm(-1, 'view');
            });
            jQuery('#generateReportBtn').click(function(event) {
                submitForm(-1, 'generateReport');
            });
        });
    </script>

    <form:form id="exporterForm" commandName="archivedValuesExporter" action="${action}">
        <cti:deviceCollection deviceCollection="${archivedValuesExporter.deviceCollection}" />

        <div class="smallBoldLabel notesSection stacked">
            <c:choose>
                <c:when test="${empty archivedValuesExporter.deviceCollection}">
                    <i:inline key=".noSelectedDevice" />
                </c:when>
                <c:otherwise>
                    <div class="fl">
                        <tags:selectedDevices id="deviceColletion" deviceCollection="${archivedValuesExporter.deviceCollection}" />
                    </div>
                    <a href="javascript:void();" class="icon icon_folder_edit selectDevices fl" title="<i:inline key=".iconFolderEditDeviceGroup"/>"><i:inline key=".iconFolderEditDeviceGroup"/></a>
                </c:otherwise>
            </c:choose>
            &nbsp &nbsp
            <c:if test="${empty deviceCollection}">
                <cti:button id="selectDevicesBtn1" nameKey="selectDevices" />
            </c:if>
        </div>
        <br>

        <tags:boxContainer2 nameKey="generateReport" styleClass="stacked">
            <tags:nameValueContainer2 id="formatContainer" tableClass="stacked clear">
                <tags:nameValue2 nameKey=".format">
                    <c:if test="${not empty allFormats}">
                        <form:select path="formatId" cssClass="fl" cssStyle="margin-right:5px;">
                            <c:forEach var="format" items="${allFormats}">
                                <form:option value="${format.formatId}" title="${format.formatId}">${format.formatName}</form:option>
                            </c:forEach>
                        </form:select>
                    </c:if>
                    <div class="fl">
                        <a href="create" class="icon icon_create create" title="<i:inline key=".iconCreateFormat" arguments="new format" />"><i:inline key=".iconCreateFormat" arguments="new format" /></a>
                        <c:if test="${not empty allFormats}">
                            <a href="edit" class="icon icon_folder_edit edit" title="<i:inline key=".iconFolderEditFormat" arguments="${format.formatName}" />"><i:inline key=".iconFolderEditFormat" arguments="${format.formatName}" /></a>
                            <a href="copy" class="icon icon_copy copy" title="<i:inline key=".iconCopyFormat" arguments="${format.formatName}" />"><i:inline key=".iconCopyFormat" arguments="${format.formatName}" /></a>
                        </c:if>
                    </div>
                </tags:nameValue2>

                <c:if test="${not empty allFormats}">
                    <tags:nameValue2 nameKey=".endDate">
                        <dt:date path="endDate" />
                        <c:choose>
                            <c:when test="${empty deviceCollection}">
                                <cti:button id="selectDevicesBtn2" nameKey="selectDevices" />
                            </c:when>
                            <c:otherwise>
                                <cti:button id="generateReportBtn" nameKey="generateReport" />
                            </c:otherwise>
                        </c:choose>
                    </tags:nameValue2>
                </c:if>
            </tags:nameValueContainer2>
        </tags:boxContainer2>
    </form:form>

    <c:if test="${not empty allFormats}">
        <tags:boxContainer2 nameKey="preview" styleClass="stacked">
            <div class="code">
            <!-- Please do not format this code -->
<pre><c:forEach var="previewEntry" items="${preview}">${previewEntry}
</c:forEach></pre>
            </div>
        </tags:boxContainer2>
    </c:if>

    <c:if test="${not empty allFormats}">
        <tags:boxContainer2 nameKey="fieldSetup" id="selectedFields" styleClass="stacked">
            <table class="compactResultsTable">
                <tr>
                    <th class="nonwrapping"><i:inline key=".field" /></th>
                    <th class="nonwrapping"><i:inline key=".dataType" /></th>
                    <th class="nonwrapping"><i:inline key=".dataSelection" /></th>
                    <th class="nonwrapping"><i:inline key=".daysPrevious" /></th>
                    <th class="nonwrapping"><i:inline key=".missingValue" /></th>
                    <th class="nonwrapping"><i:inline key=".rounding" /></th>
                    <th class="nonwrapping"><i:inline key=".pattern" /></th>
                    <th class="nonwrapping"><i:inline key=".fieldSize" /></th>
                    <th class="nonwrapping"><i:inline key=".padding" /></th>
                    <th class="nonwrapping"></th>
                </tr>
                <c:forEach var="field" items="${format.fields}" varStatus="row">
                    <tr data-row-index="${row.index}" class="<tags:alternateRow odd="" even="altRow"/>">
                        <td class="nonwrapping"><spring:escapeBody htmlEscape="true"><i:inline key="${field}" /></spring:escapeBody></td>
                        <td class="nonwrapping"><spring:escapeBody htmlEscape="true"><i:inline key="${field.attributeField}" /></spring:escapeBody></td>
                        <td class="nonwrapping"><spring:escapeBody htmlEscape="true"><i:inline key="${field.attribute.dataSelection}" /></spring:escapeBody></td>
                        <td class="nonwrapping"><spring:escapeBody htmlEscape="true"><c:if test="${not empty field.attributeField}">${field.attribute.daysPrevious}</c:if></spring:escapeBody></td>
                        <td class="nonwrapping"><i:inline key="${field.missingAttribute}" />&nbsp&nbsp<spring:escapeBody htmlEscape="true">${field.missingAttributeValue}</spring:escapeBody></td>
                        <td class="nonwrapping"><spring:escapeBody htmlEscape="true"><i:inline key="${field.roundingMode}" /></spring:escapeBody></td>
                        <td class="nonwrapping"><spring:escapeBody htmlEscape="true">${field.pattern}</spring:escapeBody></td>
                        <td class="nonwrapping">
                            <c:choose>
                                <c:when test="${field.maxLength == 0}">
                                    <i:inline key=".noMax" />
                                </c:when>
                                <c:otherwise>
                                    <spring:escapeBody htmlEscape="true">${field.maxLength}</spring:escapeBody>
                                </c:otherwise>
                            </c:choose></td>
                        <td class="nonwrapping"><cti:msg2 key="${field.padSide}" />&nbsp&nbsp${field.padChar}</td>
                        <tags:hidden path="format.fields[${row.index}].fieldId" />
                        <tags:hidden path="format.fields[${row.index}].attribute.attributeId" />
                        <tags:hidden path="format.fields[${row.index}].attribute.formatId" />
                        <tags:hidden path="format.fields[${row.index}].attribute.attribute" />
                        <tags:hidden path="format.fields[${row.index}].attribute.dataSelection" />
                        <tags:hidden path="format.fields[${row.index}].attribute.daysPrevious" />
                        <tags:hidden path="format.fields[${row.index}].fieldType" />
                        <tags:hidden path="format.fields[${row.index}].attributeField" />
                        <tags:hidden path="format.fields[${row.index}].pattern" />
                        <tags:hidden path="format.fields[${row.index}].maxLength" />
                        <tags:hidden path="format.fields[${row.index}].padChar" />
                        <tags:hidden path="format.fields[${row.index}].padSide" />
                        <tags:hidden path="format.fields[${row.index}].roundingMode" />
                        <tags:hidden path="format.fields[${row.index}].missingAttribute" />
                        <tags:hidden path="format.fields[${row.index}].missingAttributeValue" />
                    </tr>
                </c:forEach>
            </table>
        </tags:boxContainer2>
    </c:if>
</cti:standardPage>