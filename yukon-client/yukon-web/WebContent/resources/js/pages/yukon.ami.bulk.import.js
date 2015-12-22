function show (layerName) {
    $('#' + layerName)[0].style.display = '';
}
function hide (layerName) {
    $('#' + layerName)[0].style.display = 'none';
}
function showStyle (tablink) {
    $('#' + tablink)[0].style.fontWeight = 'bold;';
}
function hideStyle (tablink) {
    $('#' + tablink)[0].style.fontWeight = 'normal;';
}

var old_tab = "failed";
var selected_tab = "failed";

function show_next (new_tab) {
    old_tab = selected_tab;

    // toggle results pane
    hide(old_tab+'_data');
    show(new_tab+'_data');

    // toggle report links
    hide(old_tab+'_reports');
    show(new_tab+'_reports');

    // toggle report title
    hide(old_tab+'_title');
    show(new_tab+'_title');

    selected_tab = new_tab;
}

function removeDataFromTable (table) {
    var rowIdx;
    for(rowIdx = table.rows.length - 1; rowIdx >= 1; rowIdx -= 1) {
        table.deleteRow(rowIdx);
    }
}
    
function addRowsToTable(table, items, itemFieldNames) {
    var addColumnToRow = function (row, idx, val) {
        var cell = row.insertCell(idx),
            valItems = [].slice.call(val.split('<br>'), 0);
        valItems.forEach(function (valItem, index, arr) {
            var textNode = document.createTextNode(valItem),
                br = document.createElement('br');
            cell.appendChild(textNode);
            cell.appendChild(br);
       	});
    };

    items.forEach(function (item, index, arr) {
        var row = table.insertRow(-1),
            itemFieldCount = 0;
        itemFieldNames.forEach(function (itemFieldName, index, arr) {
            addColumnToRow(row, itemFieldCount, eval('item.' + itemFieldName));
            itemFieldCount += 1;
        });
    });
}

function setupRefreshStuff(url, refreshPeriod) {

    var onRefreshFailure = function (transport, json) {
        alert("Unable to refresh results:\n" + transport.responseText);
    },
    updateResults = function () {
    	
        var csrfToken = $('#ajax-csrf-token');
        var csrfVal = csrfToken.val();
    	var data={"com.cannontech.yukon.request.csrf.token":csrfVal};
        $.ajax({
            url: url,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            dataType: 'json'
        	
        }).done(function (data, textStatus, jqXHR) {
            onRefreshComplete(jqXHR);
        }).fail(function (jqXHR, textStatus, errorThrown) {
            onRefreshFailure(jqXHR);
        });
    },
    onRefreshComplete = function (transport) {
        var content = transport.responseText,
            json = JSON.parse(content),
            failed_table,
            pendingComm_table,
            failedComm_table;

        // SET COUNTS
        $('#importDataCount').html(json.importDataCount);
        $('#failureCount').html(json.failureCount);
        $('#pendingCommsCount').html(json.pendingCommsCount);
        $('#failedCommsCount').html(json.failedCommsCount);

        // SET IMPORT TIMES
        $('#lastImportAttempt').html(json.lastImportAttempt);
        $('#nextImportAttempt').html(json.nextImportAttempt);

        // GET TABLE OBJS AND COUNT ROWS
        failed_table = $('#failed_table')[0];
        pendingComm_table = $('#pendingComm_table')[0];
        failedComm_table = $('#failedComm_table')[0];

        // FANCY HIGHLIGHTING
        if (json.importDataCount !== parseInt($('#prevImportDataCount').val()), 10) {
            $('#importDataCount').flash();
        }
        $('#prevImportDataCount').val(json.importDataCount);
        
        if (json.failureCount > failed_table.rows.length - 1) {
            $('#failureCount').flash();
        }
        if (json.pendingCommsCount > pendingComm_table.rows.length - 1) {
            $('#pendingCommsCount').flash();
        }
        if (json.failedCommsCount > failedComm_table.rows.length - 1) {
            $('#failedCommsCount').flash();
        }

        // rebuild failures table
        removeDataFromTable(failed_table);
        addRowsToTable(failed_table, json.failures, ['failName','errorString','failTime']);

        // rebuild pending comms table
        removeDataFromTable(pendingComm_table);
        addRowsToTable(pendingComm_table, json.pendingComms, ['pendingName','routeName','substationName']);

        // rebuild failed comms table
        removeDataFromTable(failedComm_table);
        addRowsToTable(failedComm_table, json.failedComms, ['failName','routeName','substationName','errorString','failTime']);

        setTimeout(updateResults, refreshPeriod);
    };

    updateResults();
}
