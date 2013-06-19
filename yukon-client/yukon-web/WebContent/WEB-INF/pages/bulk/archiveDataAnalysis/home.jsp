<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="bulk.analysis.home">
    
<cti:includeScript link="/JavaScript/CalendarTagFuncs.js"/>

<script type="text/javascript">
function prepareDateTimeFields() {
  combineDateAndTimeFields('startDate');
  combineDateAndTimeFields('stopDate');
}
</script>

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.analysis.home" deviceCollection="${deviceCollection}">
        <form:form id="analyzeIntervalDataForm" commandName="backingBean" action="/bulk/archiveDataAnalysis/home/analyze" onsubmit="prepareDateTimeFields();">
            <cti:deviceCollection deviceCollection="${deviceCollection}" />            
            
            <div class="stacked">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".startDateLabel">
                        <tags:dateTimeInput path="startDate" inline="true" fieldValue="${startDateInitialValue}"/>
                        <i:inline key="yukon.web.modules.tools.bulk.analysis.home.exclusive"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".stopDateLabel">
                        <tags:dateTimeInput path="stopDate" inline="true" fieldValue="${stopDateInitialValue}"/>
                        <i:inline key="yukon.web.modules.tools.bulk.analysis.home.inclusive"/>
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
                    
                    <tags:selectNameValue nameKey=".attributes" path="selectedAttribute" items="${groupedAttributes}" itemValue="key" groupItems="true"/>
                    
                    <tags:checkboxNameValue id="" path="excludeBadQualities" nameKey=".empty" excludeColon="true" checkBoxDescriptionNameKey=".normalQualitiesOnly"/>
                </tags:nameValueContainer2>
            </div>
            
            <cti:button type="submit" nameKey="submitButton"/>
        </form:form>
    </tags:bulkActionContainer>
</cti:standardPage>