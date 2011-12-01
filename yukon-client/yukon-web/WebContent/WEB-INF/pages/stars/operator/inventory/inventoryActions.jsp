<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="operator" page="inventoryActions">

    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>

    <tags:boxContainer2 nameKey="actionsContainer" hideEnabled="false">
    
        <div class="containerHeader">
            <table>
                <tr>
                    <td valign="top" colspan="2">
                        <tags:selectedInventory inventoryCollection="${inventoryCollection}" id="inventoryCollection"/>
                    </td>
                </tr>
        
                <tr>
                    <td class="smallBoldLabel"><i:inline key=".instructionsLabel"/></td>
                    <td><i:inline key=".instructions"/></td>
                </tr>
            </table>
        </div>
    
        <br>
    
        <cti:dataGrid cols="2" tableClasses="twoColumnLayout split">
            <cti:dataGridCell>
                <cti:checkRolesAndProperties value="DEVICE_RECONFIG">
                    <table>
                        <tr>
                            <td class="actionCell">
                                <form action="deviceReconfig/setup" method="get">
                                    <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                                    <cti:button nameKey="deviceReconfig" type="submit" styleClass="buttonGroup"/>
                                </form>
                            </td>
                            <td class="actionCell"><i:inline key=".deviceReconfigDescription"/></td>
                        </tr>
                    </table>
                </cti:checkRolesAndProperties>
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                <table>
                    <tr>
                        <td class="actionCell">
                            <form action="deleteInventory/view" method="get">
                                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                                <cti:button nameKey="deleteInventory" type="submit" styleClass="buttonGroup"/>
                            </form>
                        </td>
                        <td class="actionCell"><i:inline key=".deleteInventoryDescription"/></td>
                    </tr>
                </table>
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                <table>
                    <tr>
                        <td class="actionCell">
                            <form action="changeType/view" method="get">
                                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                                <cti:button nameKey="changeType" type="submit" styleClass="buttonGroup"/>
                            </form>
                        </td>
                        <td class="actionCell"><i:inline key=".changeTypeDescription"/></td>
                    </tr>
                </table>
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                <table>
                    <tr>
                        <td class="actionCell">
                            <form action="changeStatus/view" method="get">
                                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                                <cti:button nameKey="changeStatus" type="submit" styleClass="buttonGroup"/>
                            </form>
                        </td>
                        <td class="actionCell"><i:inline key=".changeStatusDescription"/></td>
                    </tr>
                </table>
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                <table>
                    <tr>
                        <td class="actionCell">
                            <form action="changeServiceCompany/view" method="get">
                                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                                <cti:button nameKey="changeServiceCompany" type="submit" styleClass="buttonGroup"/>
                            </form>
                        </td>
                        <td class="actionCell"><i:inline key=".changeServiceCompanyDescription"/></td>
                    </tr>
                </table>
            </cti:dataGridCell>
            
            <cti:dataGridCell>
                <table>
                    <tr>
                        <td class="actionCell">
                            <form action="changeWarehouse/view" method="get">
                                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                                <cti:button nameKey="changeWarehouse" type="submit" styleClass="buttonGroup"/>
                            </form>
                        </td>
                        <td class="actionCell"><i:inline key=".changeWarehouseDescription"/></td>
                    </tr>
                </table>
            </cti:dataGridCell>
        </cti:dataGrid>
     
     </tags:boxContainer2>

</cti:standardPage>