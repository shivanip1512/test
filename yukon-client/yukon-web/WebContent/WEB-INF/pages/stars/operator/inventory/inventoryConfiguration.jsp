<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="operator" page="inventoryConfiguration">

	<cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css" />

    <div class="containerHeader stacked clearfix">
        <div>
            <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection" />
        </div>
        <div>
            <span class="strong-label-small dib fl"><i:inline key=".instructionsLabel"/></span>
            <span class="dib fl" style="margin-left: 5px;"><i:inline key=".instructions" /></span>
        </div>
    </div>
	<tags:sectionContainer2 nameKey="actionsContainer">
	    
        <div class="stacked">
            <cti:url value="deviceReconfig/setup" var="url">
                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                    <cti:param name="${parm.key}" value="${parm.value}"/>
                </c:forEach>
            </cti:url>
            <span class="dib" style="min-width: 80px;"><a href="${url}"><cti:msg2 key=".deviceReconfig.label"/></a></span>
            <span><i:inline key=".deviceReconfigDescription" /></span>
        </div>
        <div class="stacked">
            <cti:url value="resendConfig/view" var="url">
                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                    <cti:param name="${parm.key}" value="${parm.value}"/>
                </c:forEach>
            </cti:url>
            <span class="dib" style="min-width: 80px;"><a href="${url}"><cti:msg2 key=".resendConfig.label"/></a></span>
            <span><i:inline key=".resendConfigDescription" /></span>
        </div>
				
	</tags:sectionContainer2>
	
	<div class="page-action-area">
		<cti:url value="/stars/operator/inventory/inventoryActions?" var="cancelUrl" />
		<cti:button href="${cancelUrl}${pageContext.request.queryString}" nameKey="cancel"/>
	</div>

</cti:standardPage>