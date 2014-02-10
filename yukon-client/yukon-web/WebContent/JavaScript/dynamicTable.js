DynamicTable = Class.create();

DynamicTable.prototype = {
    initialize: function(tableId, nextRowId, addItemParameters) {
        this.tableId = tableId;
        this.nextRowId = nextRowId;
        this.addItemParameters = addItemParameters;
    },

    /**
     * Initialization that needs to be called after the page has loaded.
     */
    init : function() {
        var visibleRows,
            numVisible,
            that;
        this.wrapper = jQuery('#' + this.tableId + '_wrapper')[0];
        this.table = jQuery(this.wrapper).find('table')[0];
        this.tempRequestDiv = jQuery(this.wrapper).find('div.tempRequest')[0];
        this.everyRow(this.initRow.bind(this));
        visibleRows = this.getVisibleRows();
        numVisible = visibleRows.length;
        that = this;
        jQuery(visibleRows).each(function (index, row) {
            if (index === 0) {
                that.disableMoveUp(row);
            } else {
                that.enableMoveUp(row);
            }
            if (index === numVisible - 1) {
                that.disableMoveDown(row);
            } else {
                that.enableMoveDown(row);
            }
        });
    },

    initRow: function(row, undoRow) {
        this.matchRowAndUndoRowHeights(row, undoRow);
        if (jQuery(row).find('.isDeletionField').val() == 'true') {
            this.hideRemovedItem(row);
        }
    },

    /**
     * The tag calls this method to add a new blank row to the table.
     */
    addItem: function (event, extraArgs) {
        var parameters,
            url,
            thisContext,
            onComplete;
        if (!this.addItemParameters) {
            this.addItemParameters = {};
        }
        this.addItemParameters.itemIndex = this.nextRowId++;
        parameters = jQuery.extend({}, this.addItemParameters);
        url = 'addItem';
        thisContext = this;
        onComplete = yukon.doBind(this.addItemSuccess, this);
        if (extraArgs) {
            if (extraArgs.extraParameters) {
                jQuery.extend(parameters, extraArgs.extraParameters);
            }
            if (extraArgs.url) {
                url = extraArgs.url;
            }
        }
        jQuery.ajax({
            type: "GET",
            url: url,
            data: parameters
        }).done(function (data, status, xhrobj) {
            try {
                jQuery(thisContext.tempRequestDiv).html(data);
            } catch (replaceEx) {
                alert('DynamicTable:addItem: exception=' + replaceEx);
            }
            onComplete(xhrobj);
        });
    },
    
    /**
     * This method is intended to be called directly, to add multiple rows to the table.
     * The single parameter is an arguments object with the following properties:
     * -url (optional, used if no url set on an individual request)
     * -requests (array of objects to create rows from, each with the following properties)
     *   -extraParameters
     *   -url (optional)
     */
    addItems: function (args) {
        yukon.ui.blockPage();

        var that = this,
            doRequest;
        doRequest = function (index) {
            var request = args.requests[index],
                parameters,
                url;

            if (!that.addItemParameters) {
                that.addItemParameters = {};
            }
            that.addItemParameters.itemIndex = that.nextRowId++;
            parameters = jQuery.extend({}, that.addItemParameters);
            if (request.extraParameters) {
                jQuery.extend(parameters, request.extraParameters);
            }

            url = 'addItem';
            if (request.url) {
                url = request.url;
            } else if(args.url) {
                url = args.url;
            }

            function onComplete () {
                that.addItemSuccess(false);
                index += 1;
                if (index < args.requests.length) {
                    doRequest(index);
                } else {
                    yukon.ui.unblockPage();
                }
            }

            jQuery.ajax({
                type: "GET",
                url: url,
                data: parameters
            }).done(function (data, status, xhrobj) {
                jQuery(that.tempRequestDiv).html(data);
                onComplete();
            });
        };

        doRequest(0);
    },

    addItemSuccess : function (doUnblock) {
        var wrapperDiv = document.getElementById(this.tableId + '_wrapper'),
            tempRequestDiv = wrapperDiv.getElementsByClassName('tempRequest'),
            newRow = jQuery(tempRequestDiv).find('tr')[0],
            newUndoRow = jQuery(newRow).next()[0],
            tempRow = this.table.insertRow(-1),
            parentNode = tempRow.parentNode,
            noItemsMessageDiv;
        parentNode.replaceChild(newRow, tempRow);
        parentNode.appendChild(newUndoRow);
        this.updateMoveButtonVisibility(newRow);
        this.matchRowAndUndoRowHeights(newRow, newUndoRow);
        if (jQuery(newRow).find("input:first[type='text']")) {
            jQuery(newRow).find("input:first[type='text']").focus();
        }
        noItemsMessageDiv = jQuery(this.wrapper).find('.noItemsMessage')[0];
        if (noItemsMessageDiv) {
            noItemsMessageDiv.parentNode.removeChild(noItemsMessageDiv);
        }
        if (doUnblock) {
            yukon.ui.unblockPage();
        }
    },

    moveItemUp: function (event) {
        var thisRow = jQuery(event.target).closest('tr'),
            previousRow = thisRow.prev().prev();
        // keep going back until we find a visible one
        while (previousRow[0].rowIndex >= 2 && !previousRow[0].visible()) {
            previousRow = previousRow.prev().prev();
        }
        this.swapRows(previousRow[0], thisRow[0]);
    },

    moveItemDown: function(event) {
        var thisRow = jQuery(event.target).closest('tr'),
            nextRow = thisRow.next().next();
        while (nextRow.next() && !nextRow[0].visible()) {
            nextRow = nextRow.next().next();
        }
        this.swapRows(thisRow[0], nextRow[0]);
    },

    removeItem: function(event) {
        var row = jQuery(event.target).closest('tr'),
            isDeletionInput;
        this.hideRemovedItem(row);
        isDeletionInput = row.find('.isDeletionField');
        isDeletionInput.val(true);
    },

    /**
     * Private method used to hide a row removed either by clicking "delete" or when coming back
     * to the page as a result of validation errors.
     */
    hideRemovedItem: function (row) {
        var undoRow = jQuery(row).next(),
            visibleRows = this.getVisibleRows();
        undoRow.show();
        jQuery(row).hide();
        if (visibleRows.length < 2) {
            return;
        }
        if (row == visibleRows[0]) {
            this.disableMoveUp(visibleRows[1]);
        }
        if (row == visibleRows.last()) {
            this.disableMoveDown(visibleRows[visibleRows.length - 2]);
        }
    },

    undoRemoveItem: function (event) {
        var undoRow = jQuery(event.target).closest('tr'),
            row = undoRow.prev(),
            isDeletionInput;
        undoRow.hide();
        row.show();
        this.updateMoveButtonVisibility(row[0]);
        isDeletionInput = row.find('.isDeletionField');
        isDeletionInput.val(false);
    },

    /**
     * Private method used by other methods to help keep correct move buttons visible.  (For
     * example, ensure that on the first row the move up button is disabled.)  If the row being
     * updated turns out to be the first or last row, the adjacent row will also be updated.
     */
    updateMoveButtonVisibility: function (aroundRow) {
        var visibleRows = this.getVisibleRows();
        if (visibleRows.length === 1) {
            this.disableMoveUp(aroundRow);
            this.disableMoveDown(aroundRow);
            return;
        }
        if (aroundRow == visibleRows[0]) {
            this.disableMoveUp(aroundRow);
            this.enableMoveUp(visibleRows[1]);
        } else {
            this.enableMoveUp(aroundRow);
        }

        if (aroundRow == visibleRows.last()) {
            this.disableMoveDown(aroundRow);
            this.enableMoveDown(visibleRows[visibleRows.length - 2]);
        } else {
            this.enableMoveDown(aroundRow);
        }
    },

    /**
     * Private method to swap the given rows in the table, moving undo rows along with the rows
     * themselves.
     */
    swapRows: function (firstRow, secondRow) {
        var temp,
            firstUndoRow,
            firstRowOrderInput,
            secondUndoRow,
            secondRowOrderInput,
            visibleRows,
            parentNode,
            firstGoesBefore;
        if (firstRow.rowIndex > secondRow.rowIndex) {
            temp = firstRow;
            firstRow = secondRow;
            secondRow = temp;
        }
        firstUndoRow = jQuery(firstRow).next()[0];
        firstRowOrderInput = jQuery(firstRow).find('.orderField')[0];
        secondUndoRow = jQuery(secondRow).next()[0];
        secondRowOrderInput = jQuery(secondRow).find('.orderField')[0];

        visibleRows = this.getVisibleRows();
        if (firstRow == jQuery(visibleRows).first()[0]) {
            // this is the first row; update "move up" icons
            this.disableMoveUp(secondRow);
            this.enableMoveUp(firstRow);
        }
        if (secondRow == jQuery(visibleRows).last()[0]) {
            // swapping with last row; update "move down" icons
            this.disableMoveDown(firstRow);
            this.enableMoveDown(secondRow);
        }

        temp = jQuery(firstRowOrderInput).val();
        jQuery(firstRowOrderInput).val(jQuery(secondRowOrderInput).val());
        jQuery(secondRowOrderInput).val(temp);

        parentNode = firstRow.parentNode;
        firstGoesBefore = jQuery(secondUndoRow).next()[0];
        parentNode.insertBefore(secondRow, firstRow);
        parentNode.insertBefore(secondUndoRow, firstRow);
        if (jQuery(firstUndoRow).next()[0] != secondRow) {
            // they weren't adjacent, need to move first row too
            if (firstGoesBefore) {
                // second one is NOT the last one
                parentNode.insertBefore(firstRow, firstGoesBefore);
                parentNode.insertBefore(firstUndoRow, firstGoesBefore);
            } else {
                // second one is the last one
                parentNode.appendChild(firstRow);
                parentNode.appendChild(firstUndoRow);
            }
        }
    },

    /**
     * Private method to get all visible rows.  Does not include undo rows.
     */
    getVisibleRows : function () {
        var retVal = [];
        this.everyRow(function (row, undoRow) {
            if ('none' !== jQuery(row).css('display')) {
                retVal.push(row);
            }
        });
        return retVal;
    },

    /**
     * Private method to force height of undo row and matching regular row to be the same height.
     */
    matchRowAndUndoRowHeights: function (row, undoRow) {
        var rowHeight = jQuery(row).height(),
            undoRowHeight = jQuery(undoRow).height();
        if (undoRowHeight < rowHeight) {
            undoRow.style.height = rowHeight + "px";
        } else if (rowHeight > undoRowHeight) {
            row.style.height = undoRowHeight + "px";
        }
    },

    /**
     * Call action for every row/undo row combination.  The "action" function will be passed
     * both the row and the undo rows as parameters.
     */
    everyRow : function(action) {
        var rows = this.table.rows,
            index,
            undoRow,
            row;
        if (rows.length < 2) {
            return;
        }

        index = 0;
        while (!jQuery(rows[index]).hasClass('undo-row')) {
            index += 1;
        }
        for (; index < rows.length; index += 2) {
            undoRow = rows[index];
            row = jQuery(undoRow).prev()[0];
            action(row, undoRow);
        }
    },

    // Private methods to enable/disable move up and move down buttons.
    disableMoveUp: function (tableRow) {
        var disabledMoveUpBtn = jQuery(tableRow).find('.disabledMoveUpBtn');
        if (disabledMoveUpBtn[0]) {
            disabledMoveUpBtn.show();
            jQuery(tableRow).find('.moveUpBtn').hide();
        }
    },

    enableMoveUp: function (tableRow) {
        var disabledMoveUpBtn = jQuery(tableRow).find('.disabledMoveUpBtn');
        if (disabledMoveUpBtn[0]) {
            disabledMoveUpBtn.hide();
            jQuery(tableRow).find('.moveUpBtn').show();
        }
    },

    disableMoveDown: function (tableRow) {
        var disabledMoveDownBtn = jQuery(tableRow).find('.disabledMoveDownBtn');
        if (disabledMoveDownBtn[0]) {
            disabledMoveDownBtn.show();
            jQuery(tableRow).find('.moveDownBtn').hide();
        }
    },

    enableMoveDown: function (tableRow) {
        var disabledMoveDownBtn = jQuery(tableRow).find('.disabledMoveDownBtn');
        if ('undefined' !== typeof disabledMoveDownBtn && disabledMoveDownBtn[0]) {
            disabledMoveDownBtn.hide();
            jQuery(tableRow).find('.moveDownBtn').show();
        }
    }
};

/**
 * Find the correct instance of DynamicTable for a given element and call the given method on it.
 */
function callOnDynamicTable (event, method) {
    var wrapperDiv = jQuery(event.target).closest('div.dynamicTableWrapper')[0],
        divId = wrapperDiv.id,
        dynamicTableInstance = window[divId.substring(0, divId.length - 8)];
    dynamicTableInstance[method].apply(dynamicTableInstance, [event]);
}

jQuery(function () {
    jQuery(document).on('click', 'div.dynamicTableWrapper .moveUpBtn',
            function(event) {
                callOnDynamicTable(event, 'moveItemUp');
            });
    jQuery(document).on('click', 'div.dynamicTableWrapper .moveDownBtn',
            function(event) {
                callOnDynamicTable(event, 'moveItemDown');
            });
    jQuery(document).on('click', 'div.dynamicTableWrapper .removeBtn',
            function(event) {
                callOnDynamicTable(event, 'removeItem');
            });
    jQuery(document).on('click', 'div.dynamicTableWrapper .undoRemoveBtn',
            function(event) {
                callOnDynamicTable(event, 'undoRemoveItem');
            });
    jQuery(document).on('click', 'div.dynamicTableWrapper .addItem',
            function(event) {
                callOnDynamicTable(event, 'addItem');
            });
});