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
        this.wrapper = $(this.tableId + '_wrapper');
        this.table = this.wrapper.down('table');
        this.tempRequestDiv = this.wrapper.down('div.tempRequest');

        this.everyRow(this.initRow.bind(this));
        var visibleRows = this.getVisibleRows();
        var numVisible = visibleRows.length;
        var that = this;
        visibleRows.each(function(row, index) {
            if (index == 0) {
                that.disableMoveUp(row);
            } else {
                that.enableMoveUp(row);
            }
            if (index == numVisible - 1) {
                that.disableMoveDown(row);
            } else {
                that.enableMoveDown(row);
            }
        });
    },

    initRow: function(row, undoRow) {
        this.matchRowAndUndoRowHeights(row, undoRow);
        if ($F(row.down('.isDeletionField')) == 'true') {
            this.hideRemovedItem(row);
        }
    },

    /**
     * The tag calls this method to add a new blank row to the table.
     */
    addItem: function(event) {
        if (!this.addItemParameters) {
            this.addItemParameters = {};
        }
        this.addItemParameters.itemIndex = this.nextRowId;
        new Ajax.Updater(this.tempRequestDiv, 'addItem', {
            'parameters': this.addItemParameters,
            'onComplete': this.addItemSuccess.bind(this)
        });
    },

    addItemSuccess : function() {
        this.nextRowId++;
        var newRow = this.tempRequestDiv.down('tr');
        var newUndoRow = newRow.next();
        var tempRow = this.table.insertRow(-1);
        var parentNode = tempRow.parentNode;
        parentNode.replaceChild(newRow, tempRow);
        parentNode.appendChild(newUndoRow);
        this.updateMoveButtonVisibility(newRow);
        this.matchRowAndUndoRowHeights(newRow, newUndoRow);
        if ($(newRow).down("input:first[type='text']")) {
            $(newRow).down("input:first[type='text']").focus();
        }
        var noItemsMessageDiv = this.wrapper.down('.noItemsMessage');
        if (noItemsMessageDiv) {
            noItemsMessageDiv.parentNode.removeChild(noItemsMessageDiv);
        }
        Yukon.ui.unblockPage();
    },

    moveItemUp: function(event) {
        var thisRow = event.target.up('tr');
        var previousRow = thisRow.previous().previous();
        // keep going back until we find a visible one
        while (previousRow.rowIndex >= 2 && !previousRow.visible()) {
            previousRow = previousRow.previous().previous();
        }
        this.swapRows(previousRow, thisRow);
    },

    moveItemDown: function(event) {
        var thisRow = event.target.up('tr');
        var nextRow = thisRow.next().next();
        while (nextRow.next() && !nextRow.visible()) {
            nextRow = nextRow.next().next();
        }
        this.swapRows(thisRow, nextRow);
    },

    removeItem: function(event) {
        var row = event.target.up('tr');
        this.hideRemovedItem(row);
        var isDeletionInput = row.down('.isDeletionField');
        isDeletionInput.value = true;
    },

    /**
     * Private method used to hide a row removed either by clicking "delete" or when coming back
     * to the page as a result of validation errors.
     */
    hideRemovedItem: function(row) {
        var undoRow = row.next();
        var visibleRows = this.getVisibleRows();
        undoRow.show();
        row.hide();
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

    undoRemoveItem: function(event) {
        var undoRow = event.target.up('tr');
        var row = undoRow.previous();
        undoRow.hide();
        row.show();
        this.updateMoveButtonVisibility(row);
        var isDeletionInput = row.down('.isDeletionField');
        isDeletionInput.value = false;
    },

    /**
     * Private method used by other methods to help keep correct move buttons visible.  (For
     * example, ensure that on the first row the move up button is disabled.)  If the row being
     * updated turns out to be the first or last row, the adjacent row will also be updated.
     */
    updateMoveButtonVisibility: function(aroundRow) {
        var visibleRows = this.getVisibleRows();
        if (visibleRows.length == 1) {
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
    swapRows: function(firstRow, secondRow) {
        if (firstRow.rowIndex > secondRow.rowIndex) {
            var temp = firstRow;
            firstRow = secondRow;
            secondRow = temp;
        }
        var firstUndoRow = firstRow.next();
        var firstRowOrderInput = firstRow.down('.orderField');
        var secondUndoRow = secondRow.next();
        var secondRowOrderInput = secondRow.down('.orderField');

        var visibleRows = this.getVisibleRows();
        if (firstRow == visibleRows.first()) {
            // this is the first row; update "move up" icons
            this.disableMoveUp(secondRow);
            this.enableMoveUp(firstRow);
        }
        if (secondRow == visibleRows.last()) {
            // swapping with last row; update "move down" icons
            this.disableMoveDown(firstRow);
            this.enableMoveDown(secondRow);
        }

        var temp = $F(firstRowOrderInput);
        firstRowOrderInput.value = $F(secondRowOrderInput);
        secondRowOrderInput.value = temp;

        var parentNode = firstRow.parentNode;
        var firstGoesBefore = secondUndoRow.next();
        parentNode.insertBefore(secondRow, firstRow);
        parentNode.insertBefore(secondUndoRow, firstRow);
        if (firstUndoRow.next() != secondRow) {
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
    getVisibleRows : function() {
        var retVal = [];
        this.everyRow(function(row, undoRow) {
            if (row.visible()) {
                retVal.push(row);
            }
        });
        return retVal;
    },

    /**
     * Private method to force height of undo row and matching regular row to be the same height.
     */
    matchRowAndUndoRowHeights: function(row, undoRow) {
        var rowHeight = row.getHeight();
        var undoRowHeight = undoRow.getHeight();
        if (undoRowHeight < rowHeight) {
            undoRow.setStyle("height: " + rowHeight + "px;")
        } else if (rowHeight > undoRowHeight) {
            row.setStyle("height: " + undoRowHeight + "px;")
        }
    },

    /**
     * Call action for every row/undo row combination.  The "action" function will be passed
     * both the row and the undo rows as parameters.
     */
    everyRow : function(action) {
        var rows = this.table.rows;
        if (rows.length < 2) {
            return;
        }

        var index = 0;
        while (!rows[index].hasClassName('undoRow')) {
            index++;
        }
        for (; index < rows.length; index += 2) {
            var undoRow = rows[index];
            var row = undoRow.previous();
            action(row, undoRow);
        }
    },

    // Private methods to enable/disable move up and move down buttons.
    disableMoveUp: function(tableRow) {
        tableRow.down('.disabledMoveUpBtn').show();
        tableRow.down('.moveUpBtn').hide();
    },

    enableMoveUp: function(tableRow) {
        tableRow.down('.disabledMoveUpBtn').hide();
        tableRow.down('.moveUpBtn').show();
    },

    disableMoveDown: function(tableRow) {
        tableRow.down('.disabledMoveDownBtn').show();
        tableRow.down('.moveDownBtn').hide();
    },

    enableMoveDown: function(tableRow) {
        tableRow.down('.disabledMoveDownBtn').hide();
        tableRow.down('.moveDownBtn').show();
    }
}

function getDynamicTableInstance(wrapperDiv) {
    var divId = wrapperDiv.id;
    return window[divId.substring(0, divId.length - 8)];
}

Event.observe(window, 'load', function() {
    var divs = $$('div');
    $$('div.dynamicTableWrapper').each(function(wrapperDiv){
        getDynamicTableInstance(wrapperDiv).init();
    });
});

/**
 * Find the correct instance of DynamicTable for a given element and call the given method on it.
 */
function callOnDynamicTable(event, method) {
    var wrapperDiv = event.target.up('div.dynamicTableWrapper');
    var dynamicTableInstance = getDynamicTableInstance(wrapperDiv);
    dynamicTableInstance[method].apply(dynamicTableInstance, [event]);
}

YEvent.observeSelectorClick('div.dynamicTableWrapper .moveUpBtn',
        function(event) { callOnDynamicTable(event, 'moveItemUp') });
YEvent.observeSelectorClick('div.dynamicTableWrapper .moveDownBtn',
        function(event) { callOnDynamicTable(event, 'moveItemDown') });
YEvent.observeSelectorClick('div.dynamicTableWrapper .removeBtn',
        function(event) { callOnDynamicTable(event, 'removeItem') });
YEvent.observeSelectorClick('div.dynamicTableWrapper .undoRemoveBtn',
        function(event) { callOnDynamicTable(event, 'undoRemoveItem') });

YEvent.observeSelectorClick('div.dynamicTableWrapper #addItem',
        function(event) { callOnDynamicTable(event, 'addItem') });
