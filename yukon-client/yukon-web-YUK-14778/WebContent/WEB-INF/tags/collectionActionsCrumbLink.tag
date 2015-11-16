<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="deviceCollection" required="true" type="com.cannontech.common.bulk.collection.device.model.DeviceCollection"%>

<tags:crumbLinkByMap url="/bulk/collectionActions"
                     parameterMap="${deviceCollection.collectionParameters}" 
                     titleKey="yukon.common.device.bulk.collectionActions.pageTitle" />