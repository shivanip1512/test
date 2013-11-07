<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support" page="pointInjection">

<script type="text/javascript">

jQuery(function() {
    var tempArray;
    if (localStorage.pointInjectionIds) {
        tempArray = localStorage.pointInjectionIds.evalJSON();
        tempArray.each(addRow);
    }
});

function addRowHandler(selectedPointInfo) {
    var pointId = selectedPointInfo[0].pointId,
        newRowData = {pointId: pointId, forceArchive: 'false'},
        tempArray;
    addRow(newRowData);
    if (!localStorage.pointInjectionIds) {
        localStorage.pointInjectionIds = Object.toJSON(new Array());
    }
    tempArray = localStorage.pointInjectionIds.evalJSON();
    tempArray.push(newRowData);
    localStorage.pointInjectionIds = Object.toJSON(tempArray);
    return true;
}

function addRow(newRowData) {
    var newRow = $("dummyRow").cloneNode(true);
    $('pointTableBody').appendChild(newRow);
    new Ajax.Request("addRow",{
        parameters: {'pointId': newRowData.pointId, 'forceArchive': newRowData.forceArchive},
        onSuccess: function(transport) {
            var dummyHolder = document.createElement('div');
            dummyHolder.innerHTML = transport.responseText;
            var replacementRow = $(dummyHolder).getElementsBySelector('tr')[0];
            $('pointTableBody').replaceChild(replacementRow, newRow);
        }
    });
}

function forceArchiveChecked(box, pointId) {
	var tempArray = localStorage.pointInjectionIds.evalJSON();
	for (var index = 0; index < tempArray.length; index++) {
		if (tempArray[index].pointId == pointId) {
			tempArray[index].forceArchive = box.checked;
			break;
		}
	}
    localStorage.pointInjectionIds = Object.toJSON(tempArray);
}

jQuery(document).on('click', '.removeRow', function(event) {
    var theRow = jQuery(event.target).closest('tr'),
        pointId,
        forceArchive,
        rowToRemove,
        idx = -1,
        removedEl,
        tempArray = [],
        i;
    pointId = theRow.find('input[name=pointId]').val();
    forceArchive = theRow.find('input[name=forceArchive]').val();
    rowToRemove = {forceArchive: forceArchive, pointId: pointId};
    theRow.remove();
    if (localStorage.pointInjectionIds) {
        tempArray = JSON.parse(localStorage.pointInjectionIds);
    }
    for (i = 0; i < tempArray.length; i += 1) {
        // convert to number, so we compare tambourines to tambourines
        if (parseInt(rowToRemove.pointId, 10) === tempArray[i].pointId) {
            idx = i;
            break;
        }
    }
    if (-1 !== idx) {
        removedEl = tempArray.splice(idx, 1);
    }
    localStorage.pointInjectionIds = JSON.stringify(tempArray);
});

jQuery(document).on('click', '.sendData', function(event) {
    var theRow = jQuery(event.target).closest('tr').find(':input'),
        parameters = theRow.serializeArray(),
        newparams = {};
    if (0 === parameters.length) {
        return;
    }
    jQuery(parameters).each( function (key, val) {
        newparams[val.name] = val.value;
    });
    jQuery.ajax({
        type: "POST",
        url: "sendData",
        data: newparams,
    }).done(function(data) {
    }).fail(function(jqXHR, textStatus) {
        alert( "Request failed: " + textStatus );
    });
});

jQuery(document).on('click', '#clearAllRows', function() {
    var rows = jQuery('#pointTableBody > tr').remove();
    localStorage.removeItem('pointInjectionIds');
});
jQuery(document).on('click', '.valuePlus', function(event) {
    var theCell = jQuery(event.target).closest('td'),
        input = theCell.find('input'),
        value = input.val();
    value++;
    input.val(value.toFixed(4));
});
jQuery(document).on('click', '.valueMinus', function(event) {
    var theCell = jQuery(event.target).closest('td'),
        input = theCell.find('input'),
        value = input.val();
    value--;
    input.val(value.toFixed(4));
});
jQuery(document).on('click', '.dateTimeClear', function(event) {
    jQuery(event.target).prev().val('');
});
</script>

<table id="pointTable" class="compact-results-table">
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
<th>Archive</th>
<th>Actions</th>
</tr>
</thead>
<tbody id="pointTableBody"></tbody>
</table>
<tags:pickerDialog 
    id="thePointPicker" 
    type="pointPicker" 
    linkType="button"
    nameKey="addRow" 
    immediateSelectMode="true"
    endAction="addRowHandler"/>
<button id="clearAllRows">Clear</button>

<table style="display:none">
<tr id="dummyRow">
<td colspan="9" style="text-align: center"><img src="/WebConfig/yukon/Icons/spinner.gif"></td>
</tr>
</table>

</cti:standardPage>
