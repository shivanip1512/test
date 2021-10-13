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
                
                <c:set var="classes" value="${exportField.value ? '' : 'dn'}" />
				<tags:nameValue2 rowId="reading-pattern" nameKey=".readingPattern" rowClass="${classes}">
					<input type="hidden" id="customPatternValue"
						value="${exportField.pattern}">
					<c:if test="${customSelected}">
						<input type="hidden" id="isCustomSelected" value="true">
					</c:if>
					<tags:selectWithItems path="pattern" items="${readingPatterns}"
						itemValue="pattern" inputClass="reading-pattern-select" hideErrors= "${customSelected}"/>
					<tags:input id="reading-pattern-input" size="10" maxlength="30"
						inputClass="dn" path="pattern" />
					<c:if test="${not empty readingPatternError}">
					 <input type="hidden" id="readingPatternErrors" value="true">
					</c:if>
					<cti:icon icon="icon-help" data-popup="#value-help" classes="cp fn" />
				</tags:nameValue2>

                <c:set var="classes" value="${exportField.timestamp ? '' : 'dn'}" />
                <tags:nameValue2 rowId="timestamp-pattern" nameKey=".timestampPattern" rowClass="${classes}">
					<tags:selectWithItems path="pattern" items="${timestampPatterns}"
						itemValue="pattern" inputClass="timestamp-pattern-select" hideErrors= "${customSelected}" />
					<tags:input id="timestamp-pattern-input" size="10" maxlength="30"
						inputClass="dn" path="pattern" />
					<c:if test="${not empty timestampPatternError}">
					  <input type="hidden" id="timestampPatternErrors" value="true">
					</c:if>
					<cti:icon icon="icon-help" data-popup="#value-help" classes="cp fn" />
				</tags:nameValue2>
				<c:set var="classes" value="${exportField.field.attributeName ? '' : 'dn'}"/>
                <tags:nameValue2 rowId="field-value" nameKey=".fieldValue" rowClass="${classes}">
                    <form:select id="field-value-pattern-select" path="pattern">
                        <c:forEach var="fieldValue" items="${fieldValues}">
                            <form:option value="${fieldValue}">
                            <cti:msg2 key="${fieldValue}"/></form:option>
                        </c:forEach>
                    </form:select>&nbsp;
                </tags:nameValue2>
                
                <c:set var="classes" value="${exportField.field.plainTextType ? 'dn' : ''}"/>
                <tags:nameValue2 rowId="field-size" nameKey=".fieldSize" rowClass="${classes}">
                    <cti:msg2 var="fieldSizeMsg" key=".fieldSizeMax"/>
                    <tags:input id="max-length" path="maxLength" size="5" maxlength="5" units="${fieldSizeMsg}"/>
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
                <tags:nameValue2 rowId="plain-text" nameKey=".plainTextInput" rowClass="${classes}">
                <tags:input id="plain-text-input" size="25" maxlength="50" path="pattern" />
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            
        </tags:sectionContainer2>
        
        <c:set var="classes" value=""/>
        <c:if test="${exportField.field.plainTextType}"><c:set var="classes" value="dn"/></c:if>
        <c:if test="${exportField.field.deviceType}"><c:set var="classes" value="dn"/></c:if>
        <c:if test="${exportField.field.runtimeType}"><c:set var="classes" value="dn"/></c:if>
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