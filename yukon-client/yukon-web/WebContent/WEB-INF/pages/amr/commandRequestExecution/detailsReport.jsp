<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:simpleReportUrlFromNameTag var="reportUrl"
                                htmlOutput="true"
                                viewType="htmlView"
                                viewJsp="BODY"
                                definitionName="commandRequestExecutionResultsDefinition"
                                commandRequestExecutionId="${commandRequestExecutionId}"
                                resultsFilterType="${resultsFilterType}"
                                showLoadMask="false"
                                refreshRate="0" />
                                
<jsp:include page="${reportUrl}" />