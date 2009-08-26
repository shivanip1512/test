<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:simpleReportUrlFromNameTag var="reportUrl"
                                htmlOutput="true"
                                viewType="htmlView"
                                viewJsp="BODY"
                                definitionName="commandRequestExecutionFailureStatsDefinition"
                                commandRequestExecutionId="${commandRequestExecutionId}"
                                showLoadMask="false"
                                refreshRate="0" />
                                
<jsp:include page="${reportUrl}" />