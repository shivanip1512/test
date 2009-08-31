<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.controlAreaDetail.pageTitle" argument="${controlArea.name}"/>
<cti:standardPage module="dr" page="controlAreaDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|controlareas"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.controlAreaDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/controlArea/list">
        	<cti:msg key="yukon.web.modules.dr.controlAreaDetail.breadcrumb.controlAreas"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.breadcrumb.controlArea"
                htmlEscape="true" argument="${controlArea.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.controlAreaDetail.controlArea"
        htmlEscape="true" argument="${controlArea.name}"/></h2>

    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
            		<cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.start"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.stop"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.disable"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.enable"/><br>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
        <%@ include file="../program/programList.jspf" %>
    </tags:abstractContainer>

</cti:standardPage>
