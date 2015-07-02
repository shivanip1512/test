/**
 * Module to manage the commercial curtailment pages.
 * 
 * @requires JQUERY
 * @requires yukon
 */

yukon.namespace('yukon.dr.curtailment');

yukon.dr.curtailment = (function () {
    var mod,
        _doCalcLoadReduction = function () {
            var custTableCells = $('#customerTableDiv tbody > tr'),
                curLoadA,
                fslA,
                loadReductCellA,
                curLoadAval,
                fslAval,
                loadReduction;
            
            custTableCells.each(function(index, item) {
            	curLoadA = $(item).find('.curLoad span');
            	fslA = $(item).find('.fsl span');
            	loadReductCellA = $(item).find('.loadReduct');
            	if (curLoadA.length > 0 && fslA.length > 0) {
            		curLoadAval = parseInt($(curLoadA[0]).html().replace(/,/g,""), 10);
                    fslAval = parseInt($(fslA[0]).html().replace(/,/g,""), 10);
                    loadReduction = curLoadAval - fslAval;
                    if (loadReduction !== loadReduction) {
                        // meaning loadReduction is NaN, which happens when either curLoadA[0] or fslA[0] are set to ellipses
                        loadReduction = 'n/a';
                    }
                    $(loadReductCellA[0]).html(loadReduction);
            	} else {
            		$(loadReductCellA[0]).html(loadReduction);
            	}
            });
        },

        _doFormatLoadValues = function () {
            var custTableCells = $('#customerTableDiv tbody > tr'),
                loadReductCellA;
            
            custTableCells.each(function(index, item) {
            	loadReductCellA = $(item).find('.loadReduct');
            	$(loadReductCellA[0]).html(_commaFormat($(loadReductCellA[0]).html()));
            });
        },

        _commaFormat = function (amount) {
            var delimiter = ",",
                minus,
                i,
                n,
                a,
                nn;
            
            //check for NaN
            if (amount != amount) {
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
        },
        
        _groupMove = function (opts) {
        	var groupId = $(opts.target).data(opts.data),
            rowToMove = $(opts.target).closest('tr'),
            moveTarget = $(opts.moveTarget),
            unassigning = opts.operation.indexOf('un') === 0,
            icon = rowToMove.find('.icon'),
            input = rowToMove.find('input');
            
        	//move the row
            rowToMove.appendTo(moveTarget);
            //change icon
        	icon.removeClass(unassigning ? 'icon-delete' : 'icon-add');
            icon.addClass(unassigning ? 'icon-add' : 'icon-delete');
            //change input name
            input.attr('name', opts.operation);
            
            debug.log('operation: ' + opts.operation + ' for ' + opts.data + ' groupId ' + groupId + ' successfully processed');
        },
        
        _customerMove = function (opts) {
            var customerId = $(opts.target).data(opts.data),
            rowToMove = $(opts.target).closest('tr'),
            moveTarget = $(opts.moveTarget),
            inputs = rowToMove.find('.js-hidable-input'),
            icon = rowToMove.find('.icon'),
            unassigning = opts.operation.indexOf('un') === 0;
            
            inputs.each(function(index, input) {
            	input = $(input);
            	input.toggle();
            });
            
            if(unassigning) {
                _swapNames(rowToMove, 'assigned', 'available');    
            } else {
	            _swapNames(rowToMove, 'available', 'assigned');
            }
            
            rowToMove.appendTo(moveTarget);
            icon.removeClass(unassigning ? 'icon-delete' : 'icon-add');
            icon.addClass(unassigning ? 'icon-add' : 'icon-delete');
            
            yukon.ui.reindexInputs($('table#assigned-customers'));
            yukon.ui.reindexInputs($('table#unassigned-customers'));
            
            debug.log('operation: ' + opts.operation + ' for customerId ' + customerId + ' successfully processed');
        },
        
        _swapNames = function(row, grepString, newString) {
        	var inputs = row.find('input');
        	inputs.each(function (index, input) {
        		input = $(input);
        		var name;
        		if (input.is('[name]')) {
        			name = $(input).attr('name');
        			input.attr('name', name.replace(grepString, newString));
        		}
        	});
        };

    mod = {
        doCalcSelectedLoad: function () {
            _doCalcLoadReduction();
            var custTableCells = $('#customerTableDiv tbody > tr'),
                loadReduct = 0,
                i,
                checkedCells,
                loadReductA,
                tableCellsLength = custTableCells.length,
                summary,
                loadReductSummaryA;
        
            for (i = 0; i < tableCellsLength; i += 1) {
                checkedCells = $(custTableCells[i]).find('input[type=checkbox]');
                if ($(checkedCells[0]).is(':checked')) {
                    loadReductA = custTableCells.find('.loadReduct');
                    loadReduct += parseFloat($(loadReductA[0]).html());
                }
            }
        
            summary = $('#customerTableDiv tfoot > tr');
            if (summary.length > 0) {
                loadReductSummaryA = $(summary).find('.loadReductFoot');
                $(loadReductSummaryA[0]).html(_commaFormat(loadReduct));
            }
            _doFormatLoadValues();
        },
        
        init: function () {
            $(document).on('click', '#create-program', function (ev) {
                var programTypeId,
                    programName,
                    url;
                programTypeId = $('#program-type :selected').val();
                programName = $('#program-name').val();
                url = $(this).data('url');
                debug.log('programTypeId=' + programTypeId + ' programName=' + programName + ' url=' + url);
                $.ajax({
                    url: url + '/' + programTypeId + '/' + programName,
                    type: 'post'
                }).done(function (data, textStatus, jqXHR) {
                    debug.log('textStatus=' + textStatus);
                    var newDoc = document.open("text/html", "replace");
                    newDoc.write(data);
                    newDoc.close();
                });
            });
            
            $(function () {
                $(document).on('click', '#assigned-groups', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving assigned group to unassigned');
                    _groupMove({target: button, operation: 'unassignedGroup', data: 'group', moveTarget: '#unassigned-groups tbody'});
                });
                $(document).on('click', '#unassigned-groups', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving unassigned group to assigned');
                    _groupMove({target: button, operation: 'assignedGroup', data: 'group', moveTarget: '#assigned-groups tbody'});
                });
                $(document).on('click', '#assigned-notification-groups', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving assigned notification group to unassigned');
                    _groupMove({target: button, operation: 'unassignedNotificationGroup', data: 'notifGroup', moveTarget: '#unassigned-notification-groups tbody'});
                });
                $(document).on('click', '#unassigned-notification-groups', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving unassigned notification group to assigned');
                    _groupMove({target: button, operation: 'assignedNotifGroup', data: 'notifGroup', moveTarget: '#assigned-notification-groups tbody'});
                });
            });
            
            $(function () {
                $(document).on('click', '#assigned-customers', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving assigned customer to unassigned');
                    _customerMove({target: button, operation: 'unassignCustomer', data: 'customerId', moveTarget: '#unassigned-customers tbody'});
                });
                $(document).on('click', '#unassigned-customers', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving unassigned customer to assigned');
                    _customerMove({target: button, operation: 'assignCustomer', data: 'customerId', moveTarget: '#assigned-customers tbody'});
                });
            });
        }
        
    };
    
    return mod;
})();
$(function () { yukon.dr.curtailment.init(); });
