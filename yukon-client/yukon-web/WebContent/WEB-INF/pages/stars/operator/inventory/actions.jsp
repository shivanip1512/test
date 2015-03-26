<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.actions">
    
<div class="stacked-md"><tags:selectedInventory inventoryCollection="${inventoryCollection}"/></div>
<div class="stacked-md">
    <span class="label label-info"><i:inline key="yukon.common.note"/></span>&nbsp;
    <i:inline key=".instructions"/>
</div>

<tags:sectionContainer2 nameKey="actionsContainer">
    
    <div class="column-12-12 clear">
        <div class="column one stacked">
            <table class="link-table">
                
                <cti:checkRolesAndProperties value="DEVICE_RECONFIG">
                    <tr>
                        <td>
                            <cti:url value="inventoryConfiguration" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}"><cti:msg2 key=".deviceReconfig.label"/></a>
                        </td>
                        <td><i:inline key=".deviceReconfigDescription"/></td>
                    </tr>
                </cti:checkRolesAndProperties>
                
                <tr>
                    <td>
                        <cti:url value="changeType/view" var="url">
                            <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                <cti:param name="${parm.key}" value="${parm.value}"/>
                            </c:forEach>
                        </cti:url>
                        <a href="${url}"><cti:msg2 key=".changeType.label"/></a>
                    </td>
                    <td><i:inline key=".changeTypeDescription"/></td></td>
                </tr>
                
                <tr>
                    <td>
                        <cti:url value="changeServiceCompany/view" var="url">
                            <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                <cti:param name="${parm.key}" value="${parm.value}"/>
                            </c:forEach>
                        </cti:url>
                        <a href="${url}"><cti:msg2 key=".changeServiceCompany.label"/></a>
                    </td>
                    <td><i:inline key=".changeServiceCompanyDescription"/></td>
                </tr>
                
                <tr>
                    <td>
                        <cti:url value="controlAudit/view" var="url">
                            <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                <cti:param name="${parm.key}" value="${parm.value}"/>
                            </c:forEach>
                        </cti:url>
                        <a href="${url}"><cti:msg2 key=".controlAudit.label"/></a>
                    </td>
                    <td><i:inline key=".controlAuditDescription"/></td>
                </tr>
                
            </table>
        </div>
        
        <div class="column two nogutter stacked">
            <table class="link-table">
            
                <tr>
                    <td>
                         <cti:url value="deleteInventory/view" var="url">
                            <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                <cti:param name="${parm.key}" value="${parm.value}"/>
                            </c:forEach>
                        </cti:url>
                        <a href="${url}"><cti:msg2 key=".deleteInventory.label"/></a>
                    </td>
                    <td><i:inline key=".deleteInventoryDescription"/></td>
                </tr>
                
                <tr>
                    <td>
                        <cti:url value="changeStatus/view" var="url">
                            <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                <cti:param name="${parm.key}" value="${parm.value}"/>
                            </c:forEach>
                        </cti:url>
                        <a href="${url}"><cti:msg2 key=".changeStatus.label"/></a>
                    </td>
                    <td><i:inline key=".changeStatusDescription"/></td>
                </tr>
                
                <tr>
                    <td>
                        <cti:url value="changeWarehouse/view" var="url">
                            <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                <cti:param name="${parm.key}" value="${parm.value}"/>
                            </c:forEach>
                        </cti:url>
                        <a href="${url}"><cti:msg2 key=".changeWarehouse.label"/></a>
                    </td>
                    <td><i:inline key=".changeWarehouseDescription"/></td>
                </tr>
                
                <c:if test="${showSaveToFile}">
                    <tr>
                        <td>
                            <cti:url value="saveToBatch/setup" var="url">
                                <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                    <cti:param name="${parm.key}" value="${parm.value}"/>
                                </c:forEach>
                            </cti:url>
                            <a href="${url}"><cti:msg2 key=".saveToFile.label"/></a>
                        </td>
                        <td><i:inline key=".saveToFile"/></td>
                    </tr>
                </c:if>
                
                <tr>
                    <td>
                        <cti:url value="report" var="url">
                            <c:forEach items="${inventoryCollection.collectionParameters}" var="parm">
                                <cti:param name="${parm.key}" value="${parm.value}"/>
                            </c:forEach>
                        </cti:url>
                        <a href="${url}"><i:inline key=".report.label"/></a>
                    </td>
                    <td><i:inline key=".report.description"/></td>
                </tr>
                
            </table>
        </div>
    </div>
    
</tags:sectionContainer2>

</cti:standardPage>