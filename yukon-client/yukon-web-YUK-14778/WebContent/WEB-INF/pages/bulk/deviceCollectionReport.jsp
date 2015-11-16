<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.collectionActions.deviceCollectionReport">

    <cti:msg var="collectionDescription" key="${collectionDescriptionResovlable}"/>
    
    <cti:simpleReportUrlFromNameTag var="reportUrl"
                                htmlOutput="true"
                                viewType="extView"
                                viewJsp="BODY"
                                definitionName="deviceCollectionDefinition"
                                deviceGroup="${tempDeviceGroup}"
                                collectionDescription="${collectionDescription}"
                                showLoadMask="false"
                                refreshRate="0"/>
                                
    <jsp:include page="${reportUrl}"/>

</cti:standardPage>