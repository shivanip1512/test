function show(layerName) {
    $(layerName).style.display = '';
}
function hide(layerName) {
    $(layerName).style.display = 'none';
}
function showStyle(tablink) {
    $(tablink).style.fontWeight = 'bold;';
}
function hideStyle(tablink) {
    $(tablink).style.fontWeight = 'normal;';
}

var old_tab = "failed";
var selected_tab = "failed";

function show_next(new_tab) {

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






function removeDataFromTable(table) {
    for(rowIdx = table.rows.length - 1; rowIdx >= 1; rowIdx--) {
        table.deleteRow(rowIdx)
    }
}
    
function addRowsToTable(table, items, itemFieldNames) {
    
    function addColumnToRow(row, idx, val) {
        var cell = row.insertCell(idx);
        var textNode = document.createTextNode(val);
        cell.appendChild(textNode);
    }

    items.each(function(item) {
        
        var row = table.insertRow(-1);
        
        itemFieldCount = 0;
        itemFieldNames.each(function(itemFieldName) {
            addColumnToRow(row, itemFieldCount, eval('item.' + itemFieldName));
            itemFieldCount++;
        });
    });
}
    
    
function setupRefreshStuff(url, refreshPeriod) {
       
    var onRefreshComplete = function(transport) {
    
        var content = transport.responseText;
        var json = content.evalJSON();
        
        // SET COUNTS
        $('failureCount').innerHTML = json.failureCount;
        $('pendingCommsCount').innerHTML = json.pendingCommsCount;
        $('failedCommsCount').innerHTML = json.failedCommsCount;
        
        // SET IMPORT TIMES
        $('lastImportAttempt').innerHTML = json.lastImportAttempt;
        $('nextImportAttempt').innerHTML = json.nextImportAttempt;
        
        // GET TABLE OBJS AND COUNT ROWS
        var failed_table = $('failed_table');
        var pendingComm_table = $('pendingComm_table');
        var failedComm_table = $('failedComm_table');
        
        // FANCY HIGHLIGHTING
        if (json.failureCount > failed_table.rows.length - 1) {
            new Effect.Highlight($('failureCount'), {'duration': 2, 'startcolor': '#FFE900'});
        }
        if (json.pendingCommsCount > pendingComm_table.rows.length - 1) {
            new Effect.Highlight(document.getElementById('pendingCommsCount'), {'duration': 2, 'startcolor': '#FFE900'});
        }
        if (json.failedCommsCount > failedComm_table.rows.length - 1) {
            new Effect.Highlight($('failedCommsCount'), {'duration': 2, 'startcolor': '#FFE900'});
        }
        
        // rebuild failures table
        removeDataFromTable(failed_table);
        addRowsToTable(failed_table, json.failures, ['failName','errorString','failTime']);
        
        // rebuild pending comms table
        removeDataFromTable(pendingComm_table);
        addRowsToTable(pendingComm_table, json.pendingComms, ['pendingName','routeName','substationName']);
        
        // rebuild failed comms table
        removeDataFromTable(failedComm_table);
        addRowsToTable(failedComm_table, json.failedComms, ['failName','routeName','substationName','errorString','errorString']);
        
        
        setTimeout(updateResults, refreshPeriod);
    }
    
    var onRefreshFailure = function(transport, json) {
        alert("Unable to refresh results:\n" + transport.responseText);
    };
    
    var updateResults = function() {
          new Ajax.Request(url, {'method': 'post', 'onSuccess': onRefreshComplete, 'onFailure': onRefreshFailure, 'parameters': {}});
    }
    
    updateResults();
    
}