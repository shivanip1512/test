function doCalcSelectedLoad () {
    doCalcLoadReduction();
    var custTableCells = jQuery('#customerTableDiv tbody > tr'),
        loadReduct = 0,
        i,
        checkedCells,
        loadReductA,
        tableCellsLength = custTableCells.length,
        summary,
        loadReductSummaryA;

    for (i = 0; i < tableCellsLength; i += 1) {
        checkedCells = jQuery(custTableCells[i]).find('input[type=checkbox]');
        if (jQuery(checkedCells[0]).is(':checked')) {
            loadReductA = custTableCells.find('.loadReduct');
            loadReduct += parseFloat(jQuery(loadReductA[0]).html());
        }
    }

    summary = jQuery('#customerTableDiv tfoot > tr');
    if (summary.length > 0) {
        loadReductSummaryA = jQuery(summary).find('.loadReductFoot');
        jQuery(loadReductSummaryA[0]).html(commaFormat(loadReduct));
    }
    doFormatLoadValues();
}

function doCalcLoadReduction() {
    var custTableCells = jQuery('#customerTableDiv tbody > tr'),
        i,
        curLoadA,
        fslA,
        loadReductCellA,
        curLoadAval,
        fslAval,
        loadReduction;

    for (i = 0; i < custTableCells.length; i += 1) {
        curLoadA = jQuery(custTableCells[i]).find('.curLoad span');
        fslA = jQuery(custTableCells[i]).find('.fsl span');
        loadReductCellA = jQuery(custTableCells[i]).find('.loadReduct');
        if (curLoadA.length > 0 && fslA.length > 0) {
            curLoadAval = parseInt(jQuery(curLoadA[0]).html().replace(/,/g,""), 10);
            fslAval = parseInt(jQuery(fslA[0]).html().replace(/,/g,""), 10);
            loadReduction = curLoadAval - fslAval;
            if (loadReduction !== loadReduction) {
                // meaning loadReduction is NaN, which happens when either curLoadA[0] or fslA[0] are set to ellipses
                loadReduction = 'n/a';
            }
            jQuery(loadReductCellA[0]).html(loadReduction);
        } else {
            jQuery(loadReductCellA[0]).html('n/a');
        }
    }

}

function doFormatLoadValues () {
    var custTableCells = jQuery('#customerTableDiv tbody > tr'),
        i,
        loadReductCellA;

    for (i = 0; i < custTableCells.length; i += 1) {
        loadReductCellA = jQuery(custTableCells[i]).find('.loadReduct');
        jQuery(loadReductCellA[0]).html(commaFormat(jQuery(loadReductCellA[0]).html()));
    }
}

function commaFormat (amount) {
    var delimiter = ",",
        minus,
        i,
        n,
        a,
        nn;

    if (isNaN(amount)) {
        // just return the string if it's not a number
        return amount;
    }

    i = parseInt(amount, 10);
    minus = '';
    if (i < 0) { 
        //record the negative sign
        minus = '-';
    }

    i = parseInt(amount, 10);
    i = Math.abs(i);    //remove negative sign
    n = i.toString();
    a = [];
    while (n.length > 3) {
        nn = n.substr(n.length-3);
        a.unshift(nn);
        n = n.substr(0,n.length-3);
    }
    if (n.length > 0) { a.unshift(n); }
    n = a.join(delimiter);
    amount = n;
    amount = minus + amount;
    return amount;
}