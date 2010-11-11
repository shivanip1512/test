<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support" page="pointInjection">

<script type="text/javascript">

Event.observe(window, 'load', function() {
    if (localStorage.pointInjectionIds) {
        var tempArray = localStorage.pointInjectionIds.evalJSON();
        tempArray.each(addRow);
    }
});

function addRowHandler(selectedPointInfo) {
    var pointId = selectedPointInfo[0].pointId;
    addRow(pointId);
    if (!localStorage.pointInjectionIds) {
        localStorage.pointInjectionIds = new Array().toJSON();
    }
    var tempArray = localStorage.pointInjectionIds.evalJSON();
    tempArray.push(pointId);
    localStorage.pointInjectionIds = tempArray.toJSON();
    return true;
}

function addRow(pointId) {
    var newRow = $("dummyRow").cloneNode(true);
    $('pointTableBody').appendChild(newRow);
    new Ajax.Request("addRow",{
        parameters: {'pointId': pointId},
        onSuccess: function(transport) {
            var dummyHolder = document.createElement('div');
            dummyHolder.innerHTML = transport.responseText;
            var replacementRow = $(dummyHolder).getElementsBySelector('tr')[0];
            $('pointTableBody').replaceChild(replacementRow, newRow);
        }
    });
}

YEvent.observeSelectorClick('.removeRow', function(event) {
    var theRow = Event.findElement(event, 'tr');
    var pointId = $F(theRow.down('input[name=pointId]'));
    theRow.remove();
    var tempArray = localStorage.pointInjectionIds.evalJSON();
    tempArray = tempArray.without(pointId);
    localStorage.pointInjectionIds = tempArray.toJSON();
});

YEvent.observeSelectorClick('.sendData', function(event) {
    YEvent.markBusy(event);
    var theRow = Event.findElement(event, 'tr');
    var parameters = Form.serializeElements(theRow.getElementsBySelector('input,select'), true);
    new Ajax.Request("sendData",{
        parameters: parameters,
        onComplete: function(transport) {
            YEvent.unmarkBusy(event);
        }
    });
});

YEvent.observeSelectorClick('#clearAllRows', function() {
    var rows = $$('#pointTableBody > tr');
    rows.invoke('remove');
    localStorage.removeItem('pointInjectionIds');
});
YEvent.observeSelectorClick('.valuePlus', function(event) {
    var theCell = Event.findElement(event, 'td');
    var input = theCell.down('input');
    var value = input.value;
    value++;
    input.value = value.toFixed(4);
});
YEvent.observeSelectorClick('.valueMinus', function(event) {
    var theCell = Event.findElement(event, 'td');
    var input = theCell.down('input');
    var value = input.value;
    value--;
    input.value = value.toFixed(4);
});
YEvent.observeSelectorClick('.dateTimeClear', function(event) {
    var theCell = Event.element(event).previous().value = "";
});
</script>

<table id="pointTable" class="compactResultsTable">
<thead>
<tr>
<th>PaoIdentifier</th>
<th>Point Identifier</th>
<th>Point Name</th>
<th>Point ID</th>
<th>Date</th>
<th>Time</th>
<th>Quality</th>
<th>Value</th>
<th>Actions</th>
</tr>
</thead>
<tbody id="pointTableBody"></tbody>
</table>
<tags:pickerDialog 
    id="thePointPicker" 
    type="pointPicker" 
    linkType="none" 
    immediateSelectMode="true"
    endAction="addRowHandler"/>
    <input type="button" onclick="javascript:thePointPicker.show();" value="Add Row">
<button id="clearAllRows">Clear</button>

<table style="display:none">
<tr id="dummyRow">
<td colspan="9" style="text-align: center"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></td>
</tr>
</table>

</cti:standardPage>
