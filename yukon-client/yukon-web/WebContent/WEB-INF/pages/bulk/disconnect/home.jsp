<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="tools" page="bulk.disconnect">
    <cti:url value="/bulk/disconnect/action" var="connectUrl">
       <cti:mapParam value="${deviceCollection.collectionParameters}"/>
       <cti:param name="command" value="CONNECT"/>
    </cti:url>
    <cti:url value="/bulk/disconnect/action" var="disconnectUrl">
        <cti:mapParam value="${deviceCollection.collectionParameters}"/>
        <cti:param name="command" value="DISCONNECT"/>
    </cti:url>
    <cti:url value="/bulk/disconnect/action" var="armUrl">
        <cti:mapParam value="${deviceCollection.collectionParameters}"/>
        <cti:param name="command" value="ARM"/>
    </cti:url>

    <tags:bulkActionContainer key="yukon.common.device.bulk.disconnect" deviceCollection="${deviceCollection}">
        <table>
            <tr>
                <td><a href="${connectUrl}"><i:inline key="yukon.web.modules.tools.bulk.disconnect.command.CONNECT"/></a></td>
                <td><i:inline key="yukon.web.modules.tools.bulk.disconnect.connect.description" /></td>
            </tr>
            <tr>
                <td><a href="${disconnectUrl}"><i:inline key="yukon.web.modules.tools.bulk.disconnect.command.DISCONNECT"/></a></td>
                <td><i:inline key="yukon.web.modules.tools.bulk.disconnect.disconnect.description" /></td>
            </tr>
            <c:if test="${displayArmLink}">
                <tr>
                    <td><a href="${armUrl}"><i:inline key="yukon.web.modules.tools.bulk.disconnect.command.ARM"/></a></td>
                    <td><i:inline key="yukon.web.modules.tools.bulk.disconnect.arm.description" /></td>
                </tr>
            </c:if>
        </table>
    </tags:bulkActionContainer>
    
</cti:standardPage>