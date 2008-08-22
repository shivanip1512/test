<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="deviceCollection" required="true" type="com.cannontech.common.bulk.collection.DeviceCollection"%>

<tags:crumbLinkByMap url="/spring/bulk/collectionActions"
                     parameterMap="${deviceCollection.collectionParameters}" 
                     titleKey="yukon.common.device.bulk.collectionActions.pageTitle" />