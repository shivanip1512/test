function doCalcSelectedLoad() {
    doCalcLoadReduction();
    var custTableCells = $$('#customerTableDiv tbody > tr');
    var loadReduct = 0;
    
    for (i = 0; i < custTableCells.length; i++) {
        var checkedCells = custTableCells[i].getElementsBySelector('input[type=checkbox]');
        if( checkedCells[0].checked == true) {
            var loadReductA = custTableCells[i].getElementsBySelector('.loadReduct');
            loadReduct += parseFloat(loadReductA[0].innerHTML);
        }
    }

    var summary = $$('#customerTableDiv tfoot > tr');
    if( summary.length > 0) {
        var loadReductSummaryA = summary[0].getElementsBySelector('.loadReductFoot');
        loadReductSummaryA[0].innerHTML = commaFormat(loadReduct);
    }
    doFormatLoadValues();
}

function doCalcLoadReduction() {
    var custTableCells = $$('#customerTableDiv tbody > tr');

    for (i = 0; i < custTableCells.length; i++) {
        var curLoadA = custTableCells[i].getElementsBySelector('.curLoad span');
        var fslA = custTableCells[i].getElementsBySelector('.fsl span');
        var loadReductCellA = custTableCells[i].getElementsBySelector('.loadReduct');
        
        if (curLoadA.length > 0 && fslA.length > 0) {
            loadReductCellA[0].innerHTML = curLoadA[0].innerHTML.replace(/,/g,"") - fslA[0].innerHTML.replace(/,/g,"");
        } else {
            loadReductCellA[0].innerHTML = "n/a"; 
        }
    }

}

function doFormatLoadValues() {
    var custTableCells = $$('#customerTableDiv tbody > tr');
    for (i = 0; i < custTableCells.length; i++) {
        var curLoadA = custTableCells[i].getElementsBySelector('.curLoad span');
        var fslA = custTableCells[i].getElementsBySelector('.fsl span');
        var loadReductCellA = custTableCells[i].getElementsBySelector('.loadReduct');

        loadReductCellA[0].innerHTML = commaFormat(loadReductCellA[0].innerHTML);
    }
}

function commaFormat(amount)
{
    var delimiter = ",";
    if(isNaN(amount)) {
        // just return the string if it's not a number
        return amount;
    }

    var minus = '';
    if(i < 0) { 
        //record the negative sign
        minus = '-';
    }
    
    var i = parseInt(amount);
    i = Math.abs(i);    //remove negative sign
    var n = new String(i);
    var a = [];
    while(n.length > 3) {
        var nn = n.substr(n.length-3);
        a.unshift(nn);
        n = n.substr(0,n.length-3);
    }
    if(n.length > 0) { a.unshift(n); }
    n = a.join(delimiter);
    amount = n;
    amount = minus + amount;
    return amount;
}