<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="analysisHome">
    
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        <%-- archive data analysis --%>
        <cti:crumbLink><cti:msg2 key="yukon.web.modules.amr.analysisHome.pageName"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/CalendarTagFuncs.js"/>
    
    <script type="text/javascript">
         function prepareDateTimeFields() {
            combineDateAndTimeFields('startDate');
            combineDateAndTimeFields('stopDate');
        }
    </script>

    <tags:bulkActionContainer key="yukon.web.modules.amr.analysisHome" deviceCollection="${deviceCollection}">
        <form:form id="analyzeIntervalDataForm" commandName="backingBean" action="/spring/bulk/archiveDataAnalysis/analyze" onsubmit="prepareDateTimeFields();">
            <cti:deviceCollection deviceCollection="${deviceCollection}" />            
            <c:set var="selectInputWidth" value="200px" />
            
            <div style="padding: 0px 0px 15px 0px">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".startDateLabel">
                        <tags:dateTimeInput path="startDate" inline="true" fieldValue="${startDateInitialValue}"/>
                        <cti:msg2 key="yukon.web.modules.amr.analysisHome.exclusive"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".stopDateLabel">
                        <tags:dateTimeInput path="stopDate" inline="true" fieldValue="${stopDateInitialValue}"/>
                        <cti:msg2 key="yukon.web.modules.amr.analysisHome.inclusive"/>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".intervalLabel">
                        <form:select path="selectedInterval" style="width:${selectInputWidth};">
                            <c:forEach var="intervalDuration" items="${intervalDurations}">
                                <cti:formatPeriod value="${intervalDuration}" type="DHMS_REDUCED" var="formattedDuration"/>
                                <form:option value="${intervalDuration.millis}">${formattedDuration}</form:option>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".attributeLabel">
                        <form:select path="selectedAttribute" style="width:${selectInputWidth};">
                            <c:forEach var="attribute" items="${attributes}">
                                 <form:option value="${attribute}" title="${attribute.description}">
                                    ${attribute.description}
                                 </form:option>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".empty" excludeColon="true">
                        <cti:msg var="excludeBadQualitiesOption" key="yukon.web.modules.amr.analysisHome.excludeBadQualitiesLabel"/>
                        <form:checkbox path="excludeBadQualities" checked="true" /> ${excludeBadQualitiesOption}
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            
            <cti:button type="submit" key="submitButton"/>
        </form:form>
    </tags:bulkActionContainer>
</cti:standardPage>