<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="operator" page="inventoryActions">

    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>

    <div class="containerHeader stacked clearfix">
        <div>
            <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
        </div>
        <div>
            <span class="smallBoldLabel dib fl"><i:inline key=".instructionsLabel"/></span>
            <span class="dib fl" style="margin-left: 5px;"><i:inline key=".instructions" /></span>
        </div>
    </div>

    <tags:sectionContainer2 nameKey="actionsContainer">
        <div class="column_12_12 clear">
            <div class="column one stacked">
                <cti:checkRolesAndProperties value="DEVICE_RECONFIG">
                    <cti:url value="inventoryConfiguration" var="url">
                        <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                            <cti:param name="${parm.key}" value="${fn:escapeXml(parm.value)}"/>
                        </c:forEach>
                    </cti:url>
                    <a href="${url}" class="described"><cti:msg2 key=".deviceReconfig.label"/></a>
			        <i:inline key=".deviceReconfigDescription"/>
                </cti:checkRolesAndProperties>
            </div>
            
            <cti:checkRolesAndProperties value="SN_DELETE_RANGE">
                <div class="column two nogutter stacked">
                     <cti:url value="deleteInventory/view" var="url">
                        <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                            <cti:param name="${parm.key}" value="${fn:escapeXml(parm.value)}"/>
                        </c:forEach>
                    </cti:url>
                    <a href="${url}" class="described"><cti:msg2 key=".deleteInventory.label"/></a>
                     <i:inline key=".deleteInventoryDescription"/>
                </div>
            </cti:checkRolesAndProperties>
            
        </div>
        
        <div  class="column_12_12 clear">
            <div class="column one stacked">
                <cti:url value="changeType/view" var="url">
                    <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                        <cti:param name="${parm.key}" value="${fn:escapeXml(parm.value)}"/>
                    </c:forEach>
                </cti:url>
                <a href="${url}" class="described"><cti:msg2 key=".changeType.label"/></a>
                <i:inline key=".changeTypeDescription"/></td>
            </div>
            
            <div class="column two nogutter stacked">
	            <cti:url value="changeStatus/view" var="url">
                    <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                        <cti:param name="${parm.key}" value="${fn:escapeXml(parm.value)}"/>
                    </c:forEach>
                </cti:url>
                <a href="${url}" class="described"><cti:msg2 key=".changeStatus.label"/></a>
	            <i:inline key=".changeStatusDescription"/>
            </div>
        </div>
            
        <div  class="column_12_12 clear">
            <div class="column one stacked">
	            <cti:url value="changeServiceCompany/view" var="url">
                    <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                        <cti:param name="${parm.key}" value="${fn:escapeXml(parm.value)}"/>
                    </c:forEach>
                </cti:url>
                <a href="${url}" class="described"><cti:msg2 key=".changeServiceCompany.label"/></a>
	            <i:inline key=".changeServiceCompanyDescription"/>
            </div>
            
            <div class="column two nogutter stacked">
	            <cti:url value="changeWarehouse/view" var="url">
                    <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                        <cti:param name="${parm.key}" value="${fn:escapeXml(parm.value)}"/>
                    </c:forEach>
                </cti:url>
                <a href="${url}" class="described"><cti:msg2 key=".changeWarehouse.label"/></a>
                <i:inline key=".changeWarehouseDescription"/>
            </div>
        </div>
        
        <div  class="column_12_12 clear">
            <div class="column one stacked">
	            <cti:url value="controlAudit/view" var="url">
                    <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                        <cti:param name="${parm.key}" value="${fn:escapeXml(parm.value)}"/>
                    </c:forEach>
                </cti:url>
                <a href="${url}" class="described"><cti:msg2 key=".controlAudit.label"/></a>
	            <i:inline key=".controlAuditDescription"/>
            </div>
            
            <c:if test="${showSaveToFile}">
                <div class="column two nogutter stacked">
	                <cti:url value="saveToBatch/setup" var="url">
	                    <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
	                        <cti:param name="${parm.key}" value="${fn:escapeXml(parm.value)}"/>
	                    </c:forEach>
	                </cti:url>
	                <a href="${url}" class="described"><cti:msg2 key=".saveToFile.label"/></a>
	                <i:inline key=".saveToFile"/>
                </div>
            </c:if>
        </div>
     
     </tags:sectionContainer2>

</cti:standardPage>