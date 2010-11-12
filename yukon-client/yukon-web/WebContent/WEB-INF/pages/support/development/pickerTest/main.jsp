<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support" page="pickerTest">

<script type="text/javascript">

</script>

<table class="compactResultsTable">
<tr>
  <th>Picker</th>
  <th>Input</th>
  <th>type</th>
  <th>linkType</th>
  <th>selectionProperty</th>
  <th>nameKey</th>
  <th>endAction</th>
  <th>allowEmptySelection</th>
  <th>multiSelectMode</th>
  <th>immediateSelectMode</th>
</tr>
<tr>
  <td><tags:pickerDialog 
    id="p1" 
    type="pointPicker" 
    destinationFieldId="p1_input"
    linkType="link" 
    nameKey="pick1" 
    allowEmptySelection="false"
    multiSelectMode="false" 
    immediateSelectMode="true">Pick 1</tags:pickerDialog></td>
  <td><input id="p1_input"></td>
  <td>pointPicker</td>
  <td>link</td>
  <td></td>
  <td>pick1</td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>true</td>
</tr>
<tr>
  <td><tags:pickerDialog 
    id="p2" 
    type="pointPicker" 
    destinationFieldId="p2_input"
    linkType="link" 
    nameKey="pick2" 
    allowEmptySelection="false"
    multiSelectMode="false" 
    immediateSelectMode="false">Pick 2</tags:pickerDialog></td>
  <td><input id="p2_input"></td>
  <td>pointPicker</td>
  <td>link</td>
  <td></td>
  <td>pick2</td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog 
    id="p4" 
    type="pointPicker" 
    destinationFieldId="p4_input"
    linkType="selection" 
    selectionProperty="pointName"
    nameKey="pick4" 
    allowEmptySelection="false"
    multiSelectMode="false" 
    immediateSelectMode="false">Pick 4</tags:pickerDialog></td>
  <td><input id="p4_input"></td>
  <td>pointPicker</td>
  <td>selection</td>
  <td>pointName</td>
  <td>pick4</td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog 
    id="p5" 
    type="pointPicker" 
    destinationFieldId="p5_input"
    linkType="selection" 
    selectionProperty="pointName"
    nameKey="pick5" 
    allowEmptySelection="false"
    multiSelectMode="true" 
    immediateSelectMode="false">Pick 5</tags:pickerDialog></td>
  <td><input id="p5_input"></td>
  <td>pointPicker</td>
  <td>selection</td>
  <td>pointName</td>
  <td>pick5</td>
  <td></td>
  <td>false</td>
  <td>true</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog 
    id="p6" 
    type="pointPicker" 
    destinationFieldId="p6_input"
    linkType="selection" 
    selectionProperty="pointName"
    nameKey="pick6" 
    allowEmptySelection="false"
    multiSelectMode="false" 
    immediateSelectMode="true">Pick 6</tags:pickerDialog></td>
  <td><input id="p6_input"></td>
  <td>pointPicker</td>
  <td>selection</td>
  <td>pointName</td>
  <td>pick6</td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>true</td>
</tr>
<tr>
  <td><tags:pickerDialog 
    id="p7" 
    type="pointPicker" 
    destinationFieldId="p7_input"
    linkType="button" 
    selectionProperty="pointName"
    nameKey="pick7" 
    allowEmptySelection="false"
    multiSelectMode="false" 
    immediateSelectMode="false">Pick 7</tags:pickerDialog></td>
  <td><input id="p7_input"></td>
  <td>pointPicker</td>
  <td>button</td>
  <td>pointName</td>
  <td>pick7</td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog 
    id="p8" 
    type="pointPicker" 
    destinationFieldId="p8_input"
    linkType="button" 
    selectionProperty="pointName"
    nameKey="pick8" 
    allowEmptySelection="false"
    multiSelectMode="true" 
    immediateSelectMode="false">Pick 8</tags:pickerDialog></td>
  <td><input id="p8_input"></td>
  <td>pointPicker</td>
  <td>button</td>
  <td>pointName</td>
  <td>pick8</td>
  <td></td>
  <td>false</td>
  <td>true</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog 
    id="p9" 
    type="pointPicker" 
    destinationFieldId="p9_input"
    linkType="button" 
    selectionProperty="pointName"
    nameKey="pick9" 
    allowEmptySelection="false"
    multiSelectMode="false" 
    immediateSelectMode="true">Pick 9</tags:pickerDialog></td>
  <td><input id="p9_input"></td>
  <td>pointPicker</td>
  <td>button</td>
  <td>pointName</td>
  <td>pick9 (no image)</td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>true</td>
</tr>
</table>

</cti:standardPage>
