<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.scenarioDetail.pageTitle" argument="${scenario.name}"/>
<cti:standardPage module="dr" page="scenarioDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|scenarios"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.scenarioDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/scenario/list">
        	<cti:msg key="yukon.web.modules.dr.scenarioDetail.breadcrumb.scenarios"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.scenarioDetail.breadcrumb.scenario"
                htmlEscape="true" argument="${scenario.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.scenarioDetail.scenario"
        htmlEscape="true" argument="${scenario.name}"/></h2>
    <br>

    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.scenarioDetail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.scenarioDetail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
            		<cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.start"/><br>
            		<cti:msg key="yukon.web.modules.dr.scenarioDetail.actions.stop"/><br>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.scenarioDetail.heading.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
        <c:set var="baseUrl" value="/spring/dr/scenario/detail"/>
        <%@ include file="../program/programList.jspf" %>
    </tags:abstractContainer>

</cti:standardPage>
