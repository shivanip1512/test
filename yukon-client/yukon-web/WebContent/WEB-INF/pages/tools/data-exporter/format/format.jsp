<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.archivedValueExporter.${mode}">
    
    <tags:setFormEditMode mode="${mode}"/>
    
    <%-- Template rows --%>
    <div id="attribute-template" class="dn">
        <table>
            <tr>
                <td><input type="hidden" name="attributes[?]"></td>
                <td></td>
                <td></td>
                <td>
                    <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove fr M0"/>
                    <cti:button icon="icon-pencil" renderMode="buttonImage" classes="js-edit fr"/>
                </td>
            </tr>
        </table>
    </div>
    <div id="field-template" class="dn">
        <table>
            <tr>
                <td><input type="hidden" name="fields[?]"></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td>
                    <div class="button-group fr wsnw oh">
                        <cti:button icon="icon-cross" classes="js-remove fn M0" renderMode="buttonImage" />
                        <cti:button icon="icon-pencil" classes="js-edit fn" renderMode="buttonImage" />
                        <cti:button icon="icon-bullet-go-up" classes="js-up right fn M0" renderMode="buttonImage" />
                        <cti:button icon="icon-bullet-go-down" classes="js-down left fn M0" renderMode="buttonImage" />
                    </div>
                </td>
            </tr>
        </table>
    </div>

    <cti:toJson id="module-config" object="${jsConfig}"/>
    
    <cti:url var="action" value="/tools/data-exporter/format"/>
    <form:form id="format-form" modelAttribute="format" action="${action}">
        <cti:csrfToken/>
        <form:hidden id="format-id" path="formatId"/>
        <form:hidden id="format-type" path="formatType"/>

        <tags:sectionContainer2 nameKey="formatSettings" styleClass="stacked">
        
            <tags:nameValueContainer2 tableClass="with-form-controls">
            
                <tags:inputNameValue inputClass="js-focus" nameKey=".nameOfFormat" path="formatName" size="50" maxlength="100"/>
                
                <tags:nameValue2 nameKey=".delimiter">
                    <select name="delimiterSelect" id="delimiters">
                        <c:forEach var="delimiter" items="${delimiters}">
                            <c:choose>
                                <c:when test="${format.delimiterType == delimiter}">
                                    <c:set var="selected">selected="selected"</c:set>
                                 </c:when>
                                <c:otherwise><c:set var="selected" value=""/></c:otherwise>
                            </c:choose>
                            <option value="${delimiter.value}" type="${delimiter}" ${selected}>
                                <cti:msg2 key="${delimiter}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <tags:input id="delimiter" path="delimiter" size="2" maxlength="1"/>
                </tags:nameValue2>
                
                <tags:inputNameValue nameKey=".header" path="header" size="100" maxlength="255" inputClass="js-header"/>
                <tags:inputNameValue nameKey=".footer" path="footer" size="100" maxlength="255" inputClass="js-footer"/>
                

                <tags:nameValue2 nameKey=".timeZoneFormat">
                    <form:select id="date-timezone-format" path="dateTimeZoneFormat">
                        <c:forEach var="dateTZFormat" items="${dateTimeZoneFormats}">
                            <form:option value="${dateTZFormat}">
                                <cti:msg2 key="${dateTZFormat}"/>
                            </form:option>
                        </c:forEach>
                    </form:select>
                </tags:nameValue2>
                <tags:nameValue2 excludeColon="true">
                    <tags:checkbox descriptionNameKey=".excludeAbnormal" path="excludeAbnormal"/>
                    <tags:helpInfoPopup nameKey=".excludeAbnormal.help"/>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            
        </tags:sectionContainer2>

        <c:if test="${showAttributeSection}">
            <tags:sectionContainer2 nameKey="attributeSetup" id="attributes" styleClass="stacked attributeSetupContainer">
            
                <table id="attributes-table" class="compact-results-table dashed with-form-controls">
                    <thead>
                        <tr>
                            <th><i:inline key=".attribute"/></th>
                            <th><i:inline key=".dataSelection"/></th>
                            <th><i:inline key=".daysPrevious"/></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="attribute" items="${format.attributes}" varStatus="row">
                            <tr>
                                <td>
                                    <tags:hidden path="attributes[${row.index}]"/>
                                    <span><i:inline key="${attribute.attribute}"/></span>
                                </td>
                                <td><span><cti:msg2 key="${attribute.dataSelection}"/></span></td>
                                <td><span>${fn:escapeXml(attribute.daysPrevious)}</span></td>
                                <td>
                                    <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove fr M0"/>
                                    <cti:button icon="icon-pencil" renderMode="buttonImage" classes="js-edit fr"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="action-area">
                    <cti:button id="b-add-attribute" nameKey="add" icon="icon-add"/>
                </div>
            </tags:sectionContainer2>
        </c:if>

        <tags:sectionContainer2 nameKey="fieldSetup" styleClass="stacked">
            
            <table id="fields-table" class="compact-results-table dashed with-form-controls">
                <thead>
                    <tr>
                        <th><i:inline key=".field"/></th>
                        <th><i:inline key=".dataType"/></th>
                        <th><i:inline key=".dataSelection"/></th>
                        <th><i:inline key=".daysPrevious"/></th>
                        <th><i:inline key=".missingValue"/></th>
                        <th><i:inline key=".rounding"/></th>
                        <th><i:inline key=".pattern"/></th>
                        <th><i:inline key=".fieldSize"/></th>
                        <th><i:inline key=".padding"/></th>
                        <th style="width: 155px;"></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="exportField" items="${format.fields}" varStatus="row">
                        <tr>
                            <td>
                                <tags:hidden path="fields[${row.index}]"/>
                                <span><i:inline key="${exportField}" htmlEscape="true"/></span>
                            </td>
                            <td>
                                <span><i:inline key="${exportField.attributeField}" htmlEscape="true"/></span>
                            </td>
                            <td>
                                <span><i:inline key="${exportField.field.attribute.dataSelection}" htmlEscape="true"/></span>
                            </td>
                            <td>
                                <span><c:if test="${not empty exportField.attributeField}">${fn:escapeXml(exportField.field.attribute.daysPrevious)}</c:if></span>
                            </td>
                            <td>
                                <span><i:inline key="${exportField.missingAttribute}"/>&nbsp;&nbsp;${fn:escapeXml(exportField.missingAttributeValue)}</span>
                            </td>
                            <td>
                                <span>
                                    <c:if test="${exportField.value}">
                                        <i:inline key="${exportField.roundingMode}" htmlEscape="true"/>
                                    </c:if>
                                </span>
                            </td>
                            <td>
                                <span style="max-width: 200px;" class="db wrbw wsn">${fn:escapeXml(exportField.pattern)}</span>
                            </td>
                            <td>
                                <span>
                                    <c:choose>
                                        <c:when test="${exportField.maxLength == 0}"><i:inline key=".noMax"/></c:when>
                                        <c:otherwise>${exportField.maxLength}</c:otherwise>
                                    </c:choose>
                                </span>
                            </td>
                            <td>
                                <span><cti:msg2 key="${exportField.padSide}"/>&nbsp;&nbsp;${exportField.padChar}</span>
                            </td>
                            <td>
                                <div class="button-group fr wsnw oh">
                                    <cti:button icon="icon-cross" classes="js-remove fn M0" renderMode="buttonImage" />
                                    <cti:button icon="icon-pencil" classes="js-edit fn" renderMode="buttonImage" />
                                    <c:set var="disableUp" value="${row.first}" />
                                    <c:set var="disableDown" value="${row.last}" />
                                    <cti:button icon="icon-bullet-go-up" classes="js-up right fn M0"
                                        renderMode="buttonImage" disabled="${disableUp}" />
                                    <cti:button icon="icon-bullet-go-down" classes="js-down left fn M0"
                                        renderMode="buttonImage" disabled="${disableDown}" />
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div class="action-area">
                <cti:button id="b-add-field" nameKey="add" icon="icon-add"/>
            </div>
        </tags:sectionContainer2>
        
        <h3><i:inline key=".preview.title"/></h3>
        <pre><div><i:inline key=".preview.rulerNumbers"/></div><div><i:inline key=".preview.rulerMarks"/></div><div preview-header>${fn:escapeXml(preview.header)}</div><div preview-body><c:forEach items="${preview.body}" var="line">${fn:escapeXml(line)}<br></c:forEach></div><div preview-footer>${fn:escapeXml(preview.footer)}</div></pre>
            
        <div class="page-action-area">
            <cti:button type="submit" nameKey="save" classes="primary action" busy="true"/>
            <cti:displayForPageEditModes modes="EDIT">
                <cti:button id="deleteBtn" nameKey="delete" classes="delete" data-form="#delete-form"/>
                <d:confirm on="#deleteBtn" nameKey="confirmDelete" argument="${format.formatName}"/>
            </cti:displayForPageEditModes>
            <cti:url value="/tools/data-exporter/view" var="cancelUrl"/>
            <cti:button nameKey="cancel" href="${cancelUrl}"/>
        </div>
    </form:form>
    
    <cti:url var="deleteUrl" value="/tools/data-exporter/format/${format.formatId}"/>
    <form id="delete-form" action="${deleteUrl}" method="POST"><cti:csrfToken/><input type="hidden" name="_method" value="DELETE"></form>
    
    <!-- Helper Popups -->
    <div id="rounding-help" title="<cti:msg2 key=".helper.rounding.title"/>" class="dn" data-width="700">
        <jsp:include page="/WEB-INF/pages/amr/dynamicBilling/roundingHelper.jsp"/>
    </div>
    <div id="value-help" title="<cti:msg2 key=".helper.value.title"/>" class="dn" data-width="700">
        <jsp:include page="/WEB-INF/pages/amr/dynamicBilling/valueHelper.jsp"/>
    </div>
    <div id="timestamp-help" title="<cti:msg2 key=".helper.timestamp.title"/>" class="dn" data-width="600">
        <jsp:include page="/WEB-INF/pages/amr/dynamicBilling/timestampHelper.jsp"/>
    </div>

    <div id="format-popup"></div>
    
    <cti:includeScript link="/resources/js/pages/yukon.tools.data.exporter.format.js"/>
    
</cti:standardPage>