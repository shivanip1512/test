<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="warehouse.HOME">

    <cti:includeCss link="/WebConfig/yukon/styles/admin/energyCompany.css"/>

        <cti:dataGrid cols="1" tableClasses="energyCompanyOperationsLayout">
        
            <%-- RIGHT SIDE COLUMN --%>
            <cti:dataGridCell>
                <tags:boxContainer title="Warehouses">
                    
                    <table class="compactResultsTable rowHighlighting">
                        <c:forEach items="${warehouses}" var="warehouse">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <td style="width:30%">
                                <cti:url var="warehouseViewUrl" value="${viewUrl}">
                                    <cti:param name="ecId" value="${ecId}"/>
                                    <cti:param name="warehouseId" value="${warehouse.warehouse.warehouseID}"/>
                                </cti:url>
                                <b><a href="${warehouseViewUrl}">${warehouse.warehouse.warehouseName}</a></b>
                                <br/>
                                <span class="meta">
                                    <tags:notNullDataLine value="${warehouse.address.locationAddress1}"/>
                                    <tags:notNullDataLine value="${warehouse.address.locationAddress2}"/>
                                    <tags:notNullDataLine value="${warehouse.address.cityName}" inLine="true"/>,
                                    <tags:notNullDataLine value="${warehouse.address.stateCode}" inLine="true"/>
                                    <tags:notNullDataLine value="${warehouse.address.zipCode}" inLine="true"/>
                                </span>
                            </td>
                            <td>
                                ${warehouse.warehouse.notes}
                            </td>
                        </tr>
                        </c:forEach>
                    </table>
                    
                </tags:boxContainer>
                
                <cti:checkRolesAndProperties value="ADMIN_MULTI_WAREHOUSE">
                    <div class="actionArea">
                        <cti:url var="createWarehouseUrl" value="${newUrl}">
                            <cti:param name="ecId" value="${ecId}"/>
                        </cti:url>
                        <cti:button key="add" onclick="javascript:window.location='${createWarehouseUrl}'"/>
                    </div>
                </cti:checkRolesAndProperties>
                
            </cti:dataGridCell>
        </cti:dataGrid>
    
</cti:standardPage>