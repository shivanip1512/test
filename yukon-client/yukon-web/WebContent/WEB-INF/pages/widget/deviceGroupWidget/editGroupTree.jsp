<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>


<dialog:ajaxPage nameKey="editGroupTree" id="editGroupTreeDialog" module="amr" page="meterDetail" okEvent="dialogSubmit">      
<cti:msgScope paths="yukon.web.widgets.deviceGroupWidget,">
    <jsTree:multiNodeValueSelectingInlineTree fieldId="groupIds"
                                          fieldName="groupIds"
                                          nodeValueName="groupId"
                                          id="deviceGroupWidgetTree"
                                          treeParameters="{checkbox: true}"  
                                          dataJson="${allGroupsDataJson}"
                                          title=".groupTree.label"
                                          width="432"
                                          height="600"
                                          noSelectionAlert="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />  
</cti:msgScope>
</dialog:ajaxPage>
