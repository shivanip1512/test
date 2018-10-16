<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="tools" page="bulk.analysis.home">
    
    <cti:includeScript link="/resources/js/pages/yukon.data.analysis.js"/>
    
    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.analysis.home" deviceCollection="${deviceCollection}">
    
        <cti:url var="analyzeUrl" value="/bulk/archiveDataAnalysis/home/analyze"/>
        <form:form modelAttribute="backingBean" action="${analyzeUrl}">
        
            <cti:csrfToken/>
            <cti:deviceCollection deviceCollection="${deviceCollection}"/>
            
            <tags:nameValueContainer2 tableClass="with-form-controls">
                <tags:nameValue2 nameKey=".startDateLabel">
                    <dt:dateTime id="startDate" path="startDate" value="${startDateInitialValue}"/>
                    <i:inline key=".exclusive"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".stopDateLabel">
                    <dt:dateTime id="stopDate" path="stopDate" value="${stopDateInitialValue}"/>
                    <i:inline key=".inclusive"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".interval">
                    <form:select path="selectedInterval" class="dateTimeInputWidth">
                        <c:forEach var="intervalPeriod" items="${intervalPeriods}">
                            <form:option value="${intervalPeriod}">
                                <cti:formatPeriod value="${intervalPeriod}" type="DHMS_REDUCED"/>
                            </form:option>
                        </c:forEach>
                    </form:select>
                </tags:nameValue2>
                
                <tags:selectNameValue id="attribute-select" nameKey=".attributes" path="selectedAttribute" 
                        items="${groupedAttributes}" itemValue="key" groupItems="true"/>
                
                <tags:checkboxNameValue id="" path="excludeBadQualities" nameKey=".empty" excludeColon="true" 
                        checkBoxDescriptionNameKey=".normalQualitiesOnly"/>
            </tags:nameValueContainer2>
            
            <cti:button type="submit" nameKey="submitButton" classes="primary action"/>
        </form:form>
        
    </tags:bulkActionContainer>
    
</cti:standardPage>