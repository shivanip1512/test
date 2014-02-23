<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="operator" page="inventoryActions">
<style type="text/css">
td.left {
vertical-align: top;padding-right: 10px;padding-bottom: 10px;
}
td.right {
vertical-align: top;padding-right: 1px;padding-bottom: 10px;
}
</style>
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>

    <div class="note stacked clearfix">
        <div>
            <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
        </div>
        <div>
            <span class="strong-label-small dib fl"><i:inline key=".instructionsLabel"/></span>
            <span class="dib fl" style="margin-left: 5px;"><i:inline key=".instructions" /></span>
        </div>
    </div>

    <tags:sectionContainer2 nameKey="actionsContainer">
        <div class="column-12-12 clear">
            <div class="column one stacked">
                <table>
                <cti:checkRolesAndProperties value="DEVICE_RECONFIG">
                    <tr>
                        <td class="left">
                            <cti:url value="inventoryConfiguration" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}" class="described"><cti:msg2 key=".deviceReconfig.label"/></a>
                        </td>
                        <td class="right">
                            <i:inline key=".deviceReconfigDescription"/>
                        </td>
                    </tr>
                </cti:checkRolesAndProperties>
                    <tr>
                        <td class="left">
                            <cti:url value="changeType/view" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}" class="described"><cti:msg2 key=".changeType.label"/></a>
                        </td>
                        <td class="right">
                            <i:inline key=".changeTypeDescription"/></td>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">
                            <cti:url value="changeServiceCompany/view" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}" class="described"><cti:msg2 key=".changeServiceCompany.label"/></a>
                        </td>
                        <td class="right">
                            <i:inline key=".changeServiceCompanyDescription"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">
                            <cti:url value="controlAudit/view" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}" class="described"><cti:msg2 key=".controlAudit.label"/></a>
                        </td>
                        <td class="right">
                            <i:inline key=".controlAuditDescription"/>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="column two nogutter stacked">
                <table>
                    <tr>
                        <td class="left">
                             <cti:url value="deleteInventory/view" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}" class="described"><cti:msg2 key=".deleteInventory.label"/></a>
                        </td>
                        <td class="right">
                            <i:inline key=".deleteInventoryDescription"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">
                            <cti:url value="changeStatus/view" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}" class="described"><cti:msg2 key=".changeStatus.label"/></a>
                        </td>
                        <td class="right">
                            <i:inline key=".changeStatusDescription"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="left">
                            <cti:url value="changeWarehouse/view" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}" class="described"><cti:msg2 key=".changeWarehouse.label"/></a>
                        </td>
                        <td class="right">
                            <i:inline key=".changeWarehouseDescription"/>
                        </td>
                    </tr>
                <c:if test="${showSaveToFile}">
                    <tr>
                        <td class="left">
                            <cti:url value="saveToBatch/setup" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}" class="described"><cti:msg2 key=".saveToFile.label"/></a>
                        </td>
                        <td class="right">
                            <i:inline key=".saveToFile"/>
                        </td>
                    </tr>
                </c:if>
                </table>
            </div>
        </div>
    </tags:sectionContainer2>

</cti:standardPage>