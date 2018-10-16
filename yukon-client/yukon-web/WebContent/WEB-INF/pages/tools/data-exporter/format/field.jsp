<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.bulk.archivedValueExporter">

    <form:form id="field-form" modelAttribute="exportField">
        <cti:csrfToken/>
        <div class="notes stacked tac"><i:inline key=".note"/></div>
        
        <tags:sectionContainer2 nameKey="selectField">
        
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".field">
                    <form:select id="field-select" path="field">
                        <c:forEach items="${fields}" var="field">
                            <form:option value="${field}">
                                <c:if test="${!field.attributeType}"><cti:msg2 key="${field}"/></c:if>
                                <c:if test="${field.attributeType}">
                                    <cti:list var="arguments">
                                        <cti:item><cti:msg2 key="${field}"/></cti:item>
                                        <cti:item><cti:msg2 key="${field.attribute.dataSelection}"/></cti:item>
                                        <cti:item value="${field.attribute.daysPrevious}"/>
                                    </cti:list>
                                    <cti:msg2 key=".attributeFieldOption" arguments="${arguments}"/>
                                </c:if>
                            </form:option>
                        </c:forEach>
                    </form:select>&nbsp;
                    <c:set var="classes" value="${exportField.field.attributeType ? '' : 'dn'}"/>
                    <form:select id="attribute-field" path="attributeField" cssClass="${classes}">
                        <c:forEach var="attributeField" items="${attributeFields}">
                            <form:option value="${attributeField}"><cti:msg2 key="${attributeField}"/></form:option>
                        </c:forEach>
                    </form:select>
                </tags:nameValue2>
            </tags:nameValueContainer2>
            
        </tags:sectionContainer2>
        
        <tags:sectionContainer2 nameKey="defineLayout">
            
            <tags:hidden id="pattern" path="pattern"/>
            
            <tags:nameValueContainer2 tableClass="with-form-controls">
                <c:set var="classes" value="${exportField.value ? '' : 'dn'}"/>
                <tags:nameValue2 rowId="rounding-mode" nameKey=".roundingMode" rowClass="${classes}">
                    <form:select id="rouding-mode-select" path="roundingMode">
                        <c:forEach var="roundingMode" items="${roundingModes}">
                            <form:option value="${roundingMode}"><cti:msg2 key="${roundingMode}"/></form:option>
                        </c:forEach>
                    </form:select>
                    <cti:icon icon="icon-help" data-popup="#rounding-help" classes="cp fn"/>
                </tags:nameValue2>
                
                <c:set var="classes" value="${exportField.value ? '' : 'dn'}"/>
                <tags:nameValue2 rowId="reading-pattern" nameKey=".readingPattern" rowClass="${classes}">
                    <form:select id="reading-pattern-select" path="readingPattern">
                        <c:forEach items="${readingPatterns}" var="readingPattern">
                            <form:option value="${readingPattern}" data-pattern="${readingPattern.pattern}">
                                <cti:msg2 key="${readingPattern}"/>
                            </form:option>
                        </c:forEach>
                    </form:select>&nbsp;
                    <c:set var="classes" value="${exportField.readingPattern.custom ? '' : 'dn'}"/>
                    <c:if test="${not empty readingPatternError}"><c:set var="classes">${classes} error</c:set></c:if>
                    <c:set var="patternValue" value="${exportField.readingPattern.custom ? exportField.pattern : ''}"/>
                    <input id="reading-pattern-input" type="text" size="10" maxlength="30" class="${classes}" value="${patternValue}">
                    <c:if test="${not empty readingPatternError}"><span class="error">${fn:escapeXml(readingPatternError)}</span></c:if>
                    <cti:icon icon="icon-help" data-popup="#value-help" classes="cp fn"/>
                </tags:nameValue2>
                
                <c:set var="classes" value="${exportField.timestamp ? '' : 'dn'}"/>
                <tags:nameValue2 rowId="timestamp-pattern" nameKey=".timestampPattern" rowClass="${classes}">
                    <form:select id="timestamp-pattern-select" path="timestampPattern">
                        <c:forEach items="${timestampPatterns}" var="timestampPattern">
                            <form:option value="${timestampPattern}" data-pattern="${timestampPattern.pattern}">
                                <cti:msg2 key="${timestampPattern}"/>
                            </form:option>
                        </c:forEach>
                    </form:select>&nbsp;
                    <c:set var="classes" value="${exportField.timestampPattern.custom ? '' : 'dn'}"/>
                    <c:if test="${not empty timestampPatternError}"><c:set var="classes">${classes} error</c:set></c:if>
                    <c:set var="patternValue" value="${exportField.timestampPattern.custom ? exportField.pattern : ''}"/>
                    <input id="timestamp-pattern-input" type="text" size="10" maxlength="30" class="${classes}" value="${patternValue}">
                    <c:if test="${not empty timestampPatternError}"><span class="error">${fn:escapeXml(timestampPatternError)}</span></c:if>
                    <cti:icon icon="icon-help" data-popup="#timestamp-help" classes="cp fn"/>
                </tags:nameValue2>
                
                <tags:nameValue2 rowId="field-size" nameKey=".fieldSize">
                    <tags:input id="max-length" path="maxLength" size="5" maxlength="5"/>
                    <i:inline key=".fieldSizeMax"/>
                </tags:nameValue2>
                
                <c:set var="classes" value="${exportField.field.plainTextType ? 'dn' : ''}"/>
                <tags:nameValue2 rowId="padding" nameKey=".padding" rowClass="${classes}">
                    <form:select id="pad-side-select" path="padSide">
                        <c:forEach var="padSide" items="${padSides}">
                            <form:option value="${padSide}"><cti:msg2 key="${padSide}"/></form:option>
                        </c:forEach>
                    </form:select>&nbsp;
                    
                    <c:set var="hasPadding" value="${exportField.padSide != 'NONE'}"/>
                    <c:set var="classes" value="${hasPadding ? '' : 'dn'}"/>
                    <span id="pad-char-fields" class="dib ${classes}">
                        <c:set var="customPadChar" value="${hasPadding && exportField.padChar != ' ' && exportField.padChar != '0'}"/>
                        <select id="pad-char-select">
                            <option value=" " <c:if test="${exportField.padChar == ' '}">selected="selected"</c:if>><cti:msg2 key=".space"/></option>
                            <option value="0" <c:if test="${exportField.padChar == '0'}">selected="selected"</c:if>><cti:msg2 key=".zero"/></option>
                            <option value="CUSTOM" <c:if test="${customPadChar}">selected="selected"</c:if>><cti:msg2 key=".CUSTOM"/></option>
                        </select>&nbsp;
                        <c:set var="classes" value="${customPadChar ? '' : 'dn'}"/>
                        <tags:input id="pad-char-input" path="padChar" size="2" maxlength="1" inputClass="${classes}"/>
                    </span>
                </tags:nameValue2>
                
                <c:set var="classes" value="${exportField.field.plainTextType ? '' : 'dn'}"/>
                <c:set var="patternValue" value="${exportField.field.plainTextType ? exportField.pattern : ''}"/>
                <tags:nameValue2 rowId="plain-text" nameKey=".plainTextInput" rowClass="${classes}">
                    <input id="plain-text-input" type="text" size="25" maxlength="50" value="${patternValue}">
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            
        </tags:sectionContainer2>
        
        <c:set var="classes" value="${exportField.field.plainTextType ? 'dn' : ''}"/>
        <c:set var="classes" value="${exportField.field.deviceType ? 'dn' : ''}"/>
        <tags:sectionContainer2 id="other-options" nameKey="otherOptions" styleClass="${classes}">
        
            <tags:nameValueContainer2 tableClass="with-form-controls">
            
                <tags:nameValue2 nameKey=".unsupportedField">
                    <form:select id="unsupported-field-select" path="missingAttribute">
                        <c:forEach var="missingAttribute" items="${missingAttributes}">
                            <form:option value="${missingAttribute}"><cti:msg2 key="${missingAttribute}"/></form:option>
                        </c:forEach>
                    </form:select>
                    <c:set var="classes" value="${exportField.missingAttribute == 'FIXED_VALUE' ? '' : 'dn'}"/>
                    <tags:input id="fixed-value" path="missingAttributeValue" size="10" maxlength="20" inputClass="${classes}"/>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            
        </tags:sectionContainer2>
        
    </form:form>

</cti:msgScope>