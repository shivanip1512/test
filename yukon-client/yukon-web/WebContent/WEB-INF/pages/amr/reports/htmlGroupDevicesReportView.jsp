<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="${reportTitle}" module="amr">

    <cti:standardMenu menuSelection="devicegroups|home" />

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/group/editor/home" title="Groups Home" />
        <cti:crumbLink url="/spring/group/editor/home" title="${deviceGroup.fullName}">
           <cti:param name="groupName" value="${deviceGroup.fullName}"/>
        </cti:crumbLink>
        <cti:crumbLink>${reportTitle}</cti:crumbLink>
    </cti:breadCrumbs>

    <cti:simpleReportUrlFromNameTag var="bodyUrl"
                                    htmlOutput="true"
                                    viewType="htmlView"
                                    viewJsp="BODY"
                                    definitionName="deviceGroupDefinition"
                                    deviceGroup="${deviceGroup}" />
                                    
    <jsp:include page="${bodyUrl}" />

</cti:standardPage>