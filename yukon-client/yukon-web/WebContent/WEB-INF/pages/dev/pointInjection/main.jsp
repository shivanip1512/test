<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="pointInjection">

<script type="text/javascript">

$(function() {
    var tempArray;
    if (localStorage.pointInjectionIds) {
        tempArray = JSON.parse(localStorage.pointInjectionIds);
        $(tempArray).each( function (index, rowEntry) {
            addRow(rowEntry);
        });
    }
});

function addRowHandler(selectedPointInfo) {
    var pointId = selectedPointInfo[0].pointId,
        newRowData = {pointId: pointId, forceArchive: 'false'},
        tempArray;
    addRow(newRowData);
    if (!localStorage.pointInjectionIds) {
        localStorage.pointInjectionIds = JSON.stringify(new Array);
    }
    tempArray = JSON.parse(localStorage.pointInjectionIds);
    tempArray.push(newRowData);
    localStorage.pointInjectionIds = JSON.stringify(tempArray);
    return true;
}

function addRow(newRowData) {
    var newRow = $('#dummyRow')[0].cloneNode(true);
    $('#pointTableBody')[0].appendChild(newRow);
    $.ajax({
        type: 'POST',
        url: 'addRow',
        data: {'pointId': newRowData.pointId, 'forceArchive': newRowData.forceArchive}
    }).done(function (data, textStatus, jqXHR) {
        var dummyHolder = document.createElement('div'),
            replacementRow;
        dummyHolder.innerHTML = data;
        replacementRow = $($(dummyHolder)[0]).find('tr')[0];
        $('#pointTableBody')[0].replaceChild(replacementRow, newRow);
   });
}

function forceArchiveChecked(box, pointId) {
    var tempArray = JSON.parse(localStorage.pointInjectionIds),
        index;
    for (index = 0; index < tempArray.length; index += 1) {
        if (tempArray[index].pointId == pointId) {
            tempArray[index].forceArchive = box.checked;
            break;
        }
    }
    localStorage.pointInjectionIds = JSON.stringify(tempArray);
}

$(document).on('click', '.removeRow', function(event) {
    var theRow = $(event.target).closest('tr'),
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

$(document).on('click', '.sendData', function(event) {
    var theRow = $(event.target).closest('tr').find(':input'),
        parameters = theRow.serializeArray(),
        newparams = {};
    if (0 === parameters.length) {
        return;
    }
    $(parameters).each( function (key, val) {
        newparams[val.name] = val.value;
    });
    $.ajax({
        type: "POST",
        url: "sendData",
        data: newparams,
    }).fail(function(jqXHR, textStatus) {
        alert( "Request failed: " + textStatus );
    });
});

$(document).on('click', '#clearAllRows', function() {
    var rows = $('#pointTableBody > tr').remove();
    localStorage.removeItem('pointInjectionIds');
});
$(document).on('click', '.valuePlus', function(event) {
    var theCell = $(event.target).closest('td'),
        input = theCell.find('input'),
        value = input.val();
    value++;
    input.val(value.toFixed(4));
});
$(document).on('click', '.valueMinus', function(event) {
    var theCell = $(event.target).closest('td'),
        input = theCell.find('input'),
        value = input.val();
    value--;
    input.val(value.toFixed(4));
});
$(document).on('click', '.dateTimeClear', function(event) {
    $(event.currentTarget).prev().val('');
});
</script>

<table id="pointTable" class="compact-results-table no-stripes">
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
<td colspan="9" style="text-align: center"><img src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>"></td>
</tr>
</table>

</cti:standardPage>
