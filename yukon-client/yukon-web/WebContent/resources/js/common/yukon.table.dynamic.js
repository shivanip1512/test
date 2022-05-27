/**
 * Find the correct instance of DynamicTable for a given element and call the given method on it.
 * @param {Object} event - jquery event object.
 * @param {string} method - calling method.
 */
function callOnDynamicTable (event, method) {
    var wrapperDiv = $(event.target).closest('.dynamicTableWrapper')[0],
        divId = wrapperDiv.id,
        dynamicTableInstance = window[divId.substring(0, divId.length - 8)];
    dynamicTableInstance[method].apply(dynamicTableInstance, [event]);
}

$(function () {
    $(document).on('click', '.dynamicTableWrapper .moveUpBtn',
            function(event) {
                callOnDynamicTable(event, 'moveItemUp');
            });
    $(document).on('click', '.dynamicTableWrapper .moveDownBtn',
            function(event) {
                callOnDynamicTable(event, 'moveItemDown');
            });
    $(document).on('click', '.dynamicTableWrapper .js-remove-btn',
            function(event) {
                callOnDynamicTable(event, 'removeItem');
            });
    $(document).on('click', '.dynamicTableWrapper .js-undo-remove-btn',
            function(event) {
                callOnDynamicTable(event, 'undoRemoveItem');
            });
    $(document).on('click', '.dynamicTableWrapper .addItem',
            function(event) {
                callOnDynamicTable(event, 'addItem');
            });
});

yukon.protoDynamicTable = function (tableId, nextRowId, addItemParameters) {
    var _initialize = function(tableId, nextRowId, addItemParameters) {
        this.tableId = tableId;
        this.nextRowId = nextRowId;
        this.addItemParameters = addItemParameters;
    },
    
    /**
     * Private method to get all visible rows.  Does not include undo rows.
    */
    getVisibleRows = function () {
        var retVal = [];
        everyRow.call(this, function (row, undoRow) {
            if ('none' !== $(row).css('display')) {
                retVal.push(row);
            }
        });
        return retVal;
    },

    /**
     * Method to force height of undo row and matching regular row to be the same height.
     * @param {Object} row - row element.
     * @param {Object} undoRow - undoRow element
     */
    matchRowAndUndoRowHeights = function (row, undoRow) {
        var rowHeight = $(row).height(),
            undoRowHeight = $(undoRow).height();
        if (undoRowHeight < rowHeight) {
            undoRow.style.height = rowHeight + "px";
        } else if (rowHeight > undoRowHeight) {
            row.style.height = undoRowHeight + "px";
        }
    },

    /**
     * Call action for every row/undo row combination. The "action" function will be passed
     * both the row and the undo rows as parameters.
     * @param {string} action - function name that will be invoked.
     */
    everyRow = function(action) {
        var rows = this.table.rows,
            index,
            undoRow,
            row;
        if (rows.length < 2) {
            return;
        }

        index = 0;
        while (!$(rows[index]).hasClass('undo-row')) {
            index += 1;
        }
        for (; index < rows.length; index += 2) {
            undoRow = rows[index];
            row = $(undoRow).prev()[0];
            action(row, undoRow);
        }
    },

    /**
     * Method to disable move up buttons.
     * @param {Object} tableRow - tableRow instance
     */
    disableMoveUp = function (tableRow) {
        var disabledMoveUpBtn = $(tableRow).find('.disabledMoveUpBtn');
        if (disabledMoveUpBtn[0]) {
            disabledMoveUpBtn.show();
            $(tableRow).find('.moveUpBtn').hide();
        }
    },

    /**
     * Method to enable move up buttons.
     * @param {Object} tableRow- tableRow instance.
     */
    enableMoveUp = function (tableRow) {
        var disabledMoveUpBtn = $(tableRow).find('.disabledMoveUpBtn');
        if (disabledMoveUpBtn[0]) {
            disabledMoveUpBtn.hide();
            $(tableRow).find('.moveUpBtn').show();
        }
    },

    /**
     * Method to disable move down buttons.
     * @param {Object} tableRow- tableRow instance.
     */
    disableMoveDown = function (tableRow) {
        var disabledMoveDownBtn = $(tableRow).find('.disabledMoveDownBtn');
        if (disabledMoveDownBtn[0]) {
            disabledMoveDownBtn.show();
            $(tableRow).find('.moveDownBtn').hide();
        }
    },
    
    /**
     * Method to enable move down buttons.
     * @param {Object} tableRow- tableRow parameter
     */
    enableMoveDown = function (tableRow) {
        var disabledMoveDownBtn = $(tableRow).find('.disabledMoveDownBtn');
        if ('undefined' !== typeof disabledMoveDownBtn && disabledMoveDownBtn[0]) {
            disabledMoveDownBtn.hide();
            $(tableRow).find('.moveDownBtn').show();
        }
    },
    
    /**
     * Method is calling another method to undo row and matching regular row to be the same height and 
     * also hide removed item (row)
     * @param {Object} row - row element.
     * @param {Object} undoRow - undoRow element.
     */
    initRow = function(row, undoRow) {
        matchRowAndUndoRowHeights(row, undoRow);
        if ($(row).find('.isDeletionField').val() == 'true') {
            this.hideRemovedItem(row);
        }
    },
    dynamicProto = yukon.protoDynamicTable.prototype;

    /**
     * Initialization that needs to be called after the page has loaded.
     */
    dynamicProto.init = function() {
        var visibleRows,
            numVisible;
        this.wrapper = $('#' + this.tableId + '_wrapper')[0];
        this.table = $(this.wrapper).find('table')[0];
        this.tempRequestDiv = $(this.wrapper).find('div.tempRequest')[0];
        everyRow.call(this, initRow.bind(this));
        visibleRows = getVisibleRows.call(this);
        numVisible = visibleRows.length;
        $(visibleRows).each(function (index, row) {
            if (index === 0) {
                disableMoveUp(row);
            } else {
                enableMoveUp(row);
            }
            if (index === numVisible - 1) {
                disableMoveDown(row);
            } else {
                enableMoveDown(row);
            }
        });
    };

    /**
     * The tag calls this method to add a new blank row to the table.
     * @param {Object} event - jquery event object.
     * @param {Object} extraArgs - request parameters
     */
    dynamicProto.addItem = function(event, extraArgs) {
        var parameters,
            url,
            thisContext,
            onComplete;
        if (!this.addItemParameters) {
            this.addItemParameters = {};
        }
        this.addItemParameters.itemIndex = this.nextRowId++;
        parameters = $.extend({}, this.addItemParameters);
        url = 'addItem';
        thisContext = this;
        onComplete = this.addItemSuccess.bind(this);
        if (extraArgs) {
            if (extraArgs.extraParameters) {
                $.extend(parameters, extraArgs.extraParameters);
            }
            if (extraArgs.url) {
                url = extraArgs.url;
            }
        }
        $.ajax({
            type: "GET",
            url: url,
            data: parameters
        }).done(function (data, status, xhrobj) {
            try {
                $(thisContext.tempRequestDiv).html(data);
            } catch (replaceEx) {
                alert('DynamicTable:addItem: exception=' + replaceEx);
            }
            onComplete(xhrobj);
        });
    };
    
    /**
     * This method is intended to be called directly, to add multiple rows to the table.
     * The single parameter is an arguments object with the following properties:
     * -url (optional, used if no url set on an individual request)
     * -requests (array of objects to create rows from, each with the following properties)
     *   -extraParameters
     *   -url (optional)
     */
    dynamicProto.addItems = function (args) {
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
            parameters = $.extend({}, that.addItemParameters);
            if (request.extraParameters) {
                $.extend(parameters, request.extraParameters);
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

            $.ajax({
                type: "GET",
                url: url,
                data: parameters
            }).done(function (data, status, xhrobj) {
                $(that.tempRequestDiv).html(data);
                onComplete();
            });
        };

        doRequest(0);
    };

    /**
     * Method to add item message
     * @param {boolean} doUnblock - unblock flag.
     */
    dynamicProto.addItemSuccess = function (doUnblock) {
        var wrapperDiv = document.getElementById(this.tableId + '_wrapper'),
            tempRequestDiv = wrapperDiv.getElementsByClassName('tempRequest'),
            newRow = $(tempRequestDiv[0]).find('tr')[0],
            newUndoRow = $(newRow).next()[0],
            tempRow = $(this.table).find('tbody')[0].insertRow(-1),
            parentNode = tempRow.parentNode,
            noItemsMessageDiv,
            tbody;

        // IE puts the new rows in the TFOOT element, which breaks moveItemUp,
        // which expects all tr's to be in tbody
        if ('TFOOT' === parentNode.nodeName) {
            tbody = $(this.table).find('tbody')[0];
            tbody.appendChild(tempRow);
            tbody.replaceChild(newRow, tempRow);
            tbody.appendChild(newUndoRow);
        } else {
            parentNode.replaceChild(newRow, tempRow);
            parentNode.appendChild(newUndoRow);
        }
        this.updateMoveButtonVisibility.call(this, newRow);
        matchRowAndUndoRowHeights.call(this, newRow, newUndoRow);
        if ($(newRow).find("input:first[type='text']")) {
            $(newRow).find("input:first[type='text']").focus();
        }
        noItemsMessageDiv = $(this.wrapper).find('.js-no-items-message')[0];
        if (noItemsMessageDiv) {
            noItemsMessageDiv.parentNode.removeChild(noItemsMessageDiv);
        }
        if (doUnblock) {
            yukon.ui.unblockPage();
        }
    };


    /**
     * Method to move up the item (row)
     * @param {Object} event - jquery event object.
     */
    dynamicProto.moveItemUp = function (event) {
        var thisRow = $(event.target).closest('tr'),
            previousRow = thisRow.prev().prev();

        // keep going back until we find a visible one
        while (previousRow[0].rowIndex >= 2 && !previousRow.is(':visible')) {
            previousRow = previousRow.prev().prev();
        }
        this.swapRows(previousRow[0], thisRow[0]);
    };

    /**
     * Method to move down the item (row)
     * @param {Object} event - jquery event object.
     */
    dynamicProto.moveItemDown = function(event) {
        var thisRow = $(event.target).closest('tr'),
            nextRow = thisRow.next().next();
        while (nextRow.next() && !nextRow.is(':visible')) {
            nextRow = nextRow.next().next();
        }
        this.swapRows(thisRow[0], nextRow[0]);
    };


    /**
     * Method to remove the item (row)
     * @param {Object} event - jquery event object.
     */
    dynamicProto.removeItem = function(event) {
        var row = $(event.target).closest('tr'),
            isDeletionInput;
        this.hideRemovedItem(row[0]);
        isDeletionInput = row.find('.isDeletionField');
        isDeletionInput.val(true);
    };

    /**
     * Private method used to hide a row removed either by clicking "delete" or when coming back
     * to the page as a result of validation errors.
     * @param {Object} row- row instance.
     */
    dynamicProto.hideRemovedItem = function (row) {
        var undoRow = $(row).next(),
            visibleRows = getVisibleRows.call(this);
        undoRow.show();
        $(row).hide();
        if (visibleRows.length < 2) {
            return;
        }
        if (row == visibleRows[0]) {
            disableMoveUp(visibleRows[1]);
        }
        if (row == $(visibleRows).last()[0]) {
            disableMoveDown(visibleRows[visibleRows.length - 2]);
        }
    };

    dynamicProto.undoRemoveItem = function (event) {
        var undoRow = $(event.target).closest('tr'),
            row = undoRow.prev(),
            isDeletionInput;
        undoRow.hide();
        row.show();
        this.updateMoveButtonVisibility(row[0]);
        isDeletionInput = row.find('.isDeletionField');
        isDeletionInput.val(false);
    };

    /**
     * Private method used by other methods to help keep correct move buttons visible.  (For
     * example, ensure that on the first row the move up button is disabled.)  If the row being
     * updated turns out to be the first or last row, the adjacent row will also be updated.
     * @param {Object} aroundRow - row instance.
     */
    dynamicProto.updateMoveButtonVisibility = function (aroundRow) {
        var visibleRows = getVisibleRows.call(this);
        if (visibleRows.length === 1) {
            disableMoveUp(aroundRow);
            disableMoveDown(aroundRow);
            return;
        }
        if (aroundRow === visibleRows[0]) {
            disableMoveUp(aroundRow);
            enableMoveUp(visibleRows[1]);
        } else {
            enableMoveUp(aroundRow);
        }

        if (aroundRow === $(visibleRows).last()[0]) {
            disableMoveDown(aroundRow);
            enableMoveDown(visibleRows[visibleRows.length - 2]);
        } else {
            enableMoveDown(aroundRow);
        }
    };

    /**
     * Private method to swap the given rows in the table, moving undo rows along with the rows
     * themselves.
     * @param {Object} firstRow - first row instance.
     * @param {Object} secondRow - second row instance.
     */
    dynamicProto.swapRows = function (firstRow, secondRow) {
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
        firstUndoRow = $(firstRow).next()[0];
        firstRowOrderInput = $(firstRow).find('.orderField')[0];
        secondUndoRow = $(secondRow).next()[0];
        secondRowOrderInput = $(secondRow).find('.orderField')[0];

        visibleRows = getVisibleRows.call(this);
        if (firstRow === $(visibleRows).first()[0]) {
            // this is the first row; update "move up" icons
            disableMoveUp(secondRow);
            enableMoveUp(firstRow);
        }
        if (secondRow === $(visibleRows).last()[0]) {
            // swapping with last row; update "move down" icons
            disableMoveDown(firstRow);
            enableMoveDown(secondRow);
        }

        temp = $(firstRowOrderInput).val();
        $(firstRowOrderInput).val($(secondRowOrderInput).val());
        $(secondRowOrderInput).val(temp);

        parentNode = firstRow.parentNode;
        firstGoesBefore = $(secondUndoRow).next()[0];
        parentNode.insertBefore(secondRow, firstRow);
        parentNode.insertBefore(secondUndoRow, firstRow);
        if ($(firstUndoRow).next()[0] !== secondRow) {
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
    };

    _initialize.call(this, tableId, nextRowId, addItemParameters);
};

function DynamicTable (tableId, nextRowId, addItemParameters) {
    yukon.protoDynamicTable.call(this, tableId, nextRowId, addItemParameters);
}

yukon.inheritPrototype(DynamicTable, yukon.protoDynamicTable);