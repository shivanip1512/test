<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="format" required="true" type="com.cannontech.common.csvImport.ImportFileFormat" %>
<%@ attribute name="formatName" required="true" %>
<%@ attribute name="id" %>
<%@ attribute name="classes" %>

<cti:uniqueIdentifier prefix="import-format-display-" var="thisId"/>
<cti:default var="id" value="${thisId}"/>

<div id="${id}" class="${classes}">
    <c:set var="requiredColumns" value="${format.requiredColumns}"/>
    <c:if test="${not empty requiredColumns}">
        <table class="results-table" style="font-size:11px;">
            <tr>
                <th colspan="3" style="font-weight:bold;">
                    <i:inline key="yukon.web.import.display.requiredColumnsName"/>
                    <cti:button renderMode="image" icon="icon-information" classes="fn" nameKey="info"
                        data-popup="#${id}-required" data-popup-toggle="true"/>
                    <div id="${id}-required" class="dn" data-width="400"
                        data-title="<cti:msg2 key='import.display.requiredColumnsName'/>">

                        <p><i:inline key="yukon.web.import.display.requiredColumnsIntro"/></p>
                    </div>
                </th>
            </tr>
            <tr>
                <th><i:inline key="yukon.web.import.display.column"/></th>
                <th><i:inline key="yukon.web.import.display.description"/></th>
                <th><i:inline key="yukon.web.import.display.permittedValues"/></th>
            </tr>
            <c:forEach var="requiredColumn" items="${requiredColumns}">
                <tr>
                    <td>${requiredColumn.name}</td>
                    <td><i:inline key="${requiredColumn.descriptionKey}"/></td>
                    <td style="max-width: 300px;">
                        <c:choose>
                            <c:when test="${not empty requiredColumn.validValuesKey}">
                                <cti:msg2 var="validValues" key="${requiredColumn.validValuesKey}"/>
                                <tags:expandableString name="${formatName}_${requiredColumn.name}" value="${validValues}"/>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${not empty requiredColumn.validValuesList}">
                                    <tags:expandableString name="${formatName}_${requiredColumn.name}" value="${requiredColumn.validValuesString}"/>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <br><br>
    </c:if>
    
    <c:set var="optionalColumns" value="${format.optionalColumns}"/>
    <c:if test="${not empty optionalColumns}">
        <table class="results-table" style="font-size:11px;">
            <tr>
                <th colspan="3" style="font-weight:bold;">
                    <i:inline key="yukon.web.import.display.optionalColumnsName" />

                    <cti:button renderMode="image" icon="icon-information" classes="fn" nameKey="info"
                        data-popup="#${id}-optional" data-popup-toggle="true"/>
                    <div id="${id}-optional" class="dn" data-width="400"
                        data-title="<cti:msg2 key='import.display.optionalColumnsName'/>">

                        <p><i:inline key="yukon.web.import.display.optionalColumnsIntro"/></p>
                    </div>
                </th>
            </tr>
            <tr>
                <th><i:inline key="yukon.web.import.display.column"/></th>
                <th><i:inline key="yukon.web.import.display.description"/></th>
                <th><i:inline key="yukon.web.import.display.permittedValues"/></th>
            </tr>
            <c:forEach var="optionalColumn" items="${optionalColumns}">
                <tr>
                    <td>${optionalColumn.name}</td>
                    <td><i:inline key="${optionalColumn.descriptionKey}"/></td>
                    <td style="max-width: 300px;">
                        <c:choose>
                            <c:when test="${not empty optionalColumn.validValuesKey}">
                                <cti:msg2 var="validValues" key="${optionalColumn.validValuesKey}"/>
                                <tags:expandableString name="${formatName}_${optionalColumn.name}" value="${validValues}"/>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${not empty optionalColumn.validValuesList}">
                                    <tags:expandableString name="${formatName}_${optionalColumn.name}" value="${optionalColumn.validValuesString}"/>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <br><br>
    </c:if>
    
    <c:set var="groupedColumnsMap" value="${format.groupedColumnsAsMap}"/>
    <c:if test="${not empty groupedColumnsMap}">
        <table class="results-table" style="font-size:11px;">
            <tr>
                <th colspan="3" style="font-weight:bold;">
                    <i:inline key="yukon.web.import.display.groupedColumnsName" />

                    <cti:button renderMode="image" icon="icon-information" classes="fn" nameKey="info"
                        data-popup="#${id}-grouped" data-popup-toggle="true"/>
                    <div id="${id}-grouped" class="dn" data-width="400"
                        data-title="<cti:msg2 key='import.display.groupedColumnsName'/>">

                        <p><i:inline key="yukon.web.import.display.groupedColumnsIntro"/></p>
                    </div>
                </th>
            </tr>
            <c:forEach var="groupedColumns" items="${groupedColumnsMap}">
                <tr>
                    <th colspan="3">${groupedColumns.key}</th>
                </tr>
                <tr>
                    <th><i:inline key="yukon.web.import.display.column"/></th>
                    <th><i:inline key="yukon.web.import.display.description"/></th>
                    <th><i:inline key="yukon.web.import.display.permittedValues"/></th>
                </tr>
                <c:forEach var="groupedColumn" items="${groupedColumns.value}">
                    <tr>
                        <td>${groupedColumn.name}</td>
                        <td><i:inline key="${groupedColumn.descriptionKey}"/></td>
                        <td style="max-width: 300px;">
                            <c:choose>
                                <c:when test="${not empty groupedColumn.validValuesKey}">
                                    <cti:msg2 var="validValues" key="${groupedColumn.validValuesKey}"/>
                                <tags:expandableString name="${formatName}_${groupedColumn.name}" value="${validValues}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${not empty groupedColumn.validValuesList}">
                                        <tags:expandableString name="${formatName}_${groupedColumn.name}" value="${groupedColumn.validValuesString}"/>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </c:forEach>
        </table>
        <br><br>
    </c:if>
    
    <c:set var="valueDepColumns" value="${format.valueDependentColumnsAsMap}"/>
    <c:if test="${not empty valueDepColumns}">
        <table class="results-table" style="font-size:11px;">
            <tr>
                <th colspan="5" style="font-weight:bold;">
                    <i:inline key="yukon.web.import.display.valueDependentColumnsName" />

                    <cti:button renderMode="image" icon="icon-information" classes="fn" nameKey="info"
                        data-popup="#${id}-value-dependent" data-popup-toggle="true"/>
                    <div id="${id}-value-dependent" class="dn" data-width="400"
                        data-title="<cti:msg2 key='import.display.valueDependentColumnsName'/>">

                        <p><i:inline key="yukon.web.import.display.valueDependentColumnsIntro"/></p>
                    </div>
                </th>
            </tr>
            <tr>
                <th><i:inline key="yukon.web.import.display.column"/></th>
                <th><i:inline key="yukon.web.import.display.description"/></th>
                <th><i:inline key="yukon.web.import.display.permittedValues"/></th>
                <th><i:inline key="yukon.web.import.display.dependedUponColumn"/></th>
                <th><i:inline key="yukon.web.import.display.dependedUponValues"/></th>
            </tr>
            <c:forEach var="valueDepColumnsEntry" items="${valueDepColumns}">
                <c:set var="dependedUponColumn" value="${valueDepColumnsEntry.key}"/>
                <c:forEach var="valueDepColumn" items="${valueDepColumnsEntry.value}">
                    <tr>
                        <td>${valueDepColumn.name}</td>
                        <td><i:inline key="${valueDepColumn.descriptionKey}"/></td>
                        <td style="max-width: 300px;">
                            <c:choose>
                                <c:when test="${not empty valueDepColumn.validValuesKey}">
                                    <cti:msg2 var="validValues" key="${valueDepColumn.validValuesKey}"/>
                                <tags:expandableString name="${formatName}_${valueDepColumn.name}" value="${validValues}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${not empty valueDepColumn.validValuesList}">
                                        <tags:expandableString name="${formatName}_${valueDepColumn.name}" value="${valueDepColumn.validValuesString}"/>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${dependedUponColumn.name}</td>
                        <td>${valueDepColumn.dependedUponValueString}</td>
                    </tr>
                </c:forEach>
            </c:forEach>
        </table>
    </c:if>
    
</div>