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
            var custTableRows = $('#customerTableDiv').find('tbody > tr'),
                currentLoadSpan,
                cfdSpan,
                loadReductSpan,
                currentLoadValue,
                cfdValue,
                loadReduction;
            
            custTableRows.each(function(index, item) {
                // locate the spans we need in this row
                currentLoadSpan = $(item).find('.js-current-load span');
                cfdSpan = $(item).find('.js-cfd span');
                loadReductSpan = $(item).find('.js-load-reduct');
                
                if (currentLoadSpan.length > 0 && cfdSpan.length > 0) {
                    // parse the current load and CFD values into comma-free numbers
                    currentLoadValue = parseInt($(currentLoadSpan[0]).html().replace(/,/g,""), 10);
                    cfdValue = parseInt($(cfdSpan[0]).html().replace(/,/g,""), 10);
                    // calculate the load reduction for this customer
                    loadReduction = currentLoadValue - cfdValue;
                    if (isNaN(loadReduction)) {
                        // happens when either currentLoadSpan[0] or cfdSpan[0] are set to ellipses
                        loadReduction = 'n/a';
                    }
                }
                // write the load reduction value to its cell
                $(loadReductSpan[0]).html(loadReduction);
            });
        },
        
        _doFormatLoadValues = function () {
            var custTableRows = $('#customerTableDiv').find('tbody > tr');
            custTableRows.each(function (index, item) {
                var loadReduct = $(item).find('.js-load-reduct').get(0);
                $(loadReduct).html(_commaFormat($(loadReduct).html()));
            });
        },

        _commaFormat = function (amount) {
            // Force to numeric with unary +
            amount = +amount;
            return amount.toLocaleString();
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
            if (unassigning) {
            	icon.html(yg.iconSvg.iconAdd);
            } else {
            	icon.html(yg.iconSvg.iconDelete);
            }
            //change input name
            input.attr('name', opts.operation);
            
            debug.log('operation: ' + opts.operation + ' for ' + opts.data + ' groupId ' + groupId + ' successfully processed');
        },
        
        _programMove = function (opts) {
            var programId = $(opts.target).data(opts.data);
            rowToMove = $(opts.target).closest('tr'),
            moveTarget = $(opts.moveTarget),
            icon = rowToMove.find('.icon'),
            unassigning = opts.operation.indexOf('un') === 0;
            
            if(unassigning) {
                _swapNames(rowToMove, 'active', 'available');
            } else {
                _swapNames(rowToMove, 'available', 'active');
            }
            
            rowToMove.appendTo(moveTarget);
            icon.removeClass(unassigning ? 'icon-delete' : 'icon-add');
            icon.addClass(unassigning ? 'icon-add' : 'icon-delete');
            if (unassigning) {
            	icon.html(yg.iconSvg.iconAdd);
            } else {
            	icon.html(yg.iconSvg.iconDelete);
            }
            yukon.ui.reindexInputs($('table#active-programs'));
            yukon.ui.reindexInputs($('table#available-programs'));
            
            debug.log('operation: ' + opts.operation + ' for programId ' + programId + ' successfully processed');
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
            if (unassigning) {
            	icon.html(yg.iconSvg.iconAdd);
            } else {
            	icon.html(yg.iconSvg.iconDelete);
            }
            yukon.ui.reindexInputs($('table#assigned-customers'));
            yukon.ui.reindexInputs($('table#unassigned-customers'));
            
            debug.log('operation: ' + opts.operation + ' for customerId ' + customerId + ' successfully processed');
        },
        
        _enableDisableActionsMenu = function() {
            //Display the actions and auto-refresh if the current active tab is Trends
            var isTrendsTabActive = $(".ui-tabs").find(".ui-tabs-active").hasClass('ccTrends');
            if (isTrendsTabActive) {
                $('.page-actions').css('display','block');
            } else {
                $('.page-actions').css('display','none');
            }
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
            // first, calculate the load reduction for each row
            _doCalcLoadReduction();
            
            var custTableRows = $('#customerTableDiv').find('tbody > tr'),
                loadReductTotal = 0,
                checkbox,
                customerLoadReductSpan,
                customerLoadReduct;
            
            // for each checked customer, add their load reduction to the total
            custTableRows.each(function (index, row) {
                checkbox = $(row).find('input[type=checkbox]');
                if ($(checkbox[0]).is(':checked')) {
                    customerLoadReductSpan = $(row).find('.js-load-reduct').get(0);
                    customerLoadReduct = parseFloat($(customerLoadReductSpan).html());
                    //ignore the value if it parses to NaN
                    if (!isNaN(customerLoadReduct)) {
                        loadReductTotal += customerLoadReduct;
                    }
                }
            });
            
            // update the load reduction total
            if (custTableRows.length > 0) {
                $('.js-load-reduct-total').html(_commaFormat(loadReductTotal))
                                          .flash(3.5);
                
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
                
                //validate that name is not empty
                if (!programName.trim()) {
                    $('#program-name').addClass('error');
                    $('#program-errors').show();
                } else {
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
                }
            });
            
            //validation for CommercialCurtailment program detail
            $(document).on('click', '#program-save', function (ev) {
                $('#program-last-identifier').removeClass('error');
                $('#program-error').hide();
                var errorFound = false,
                    progLastId = $('#program-last-identifier').val();
                if (!$.isNumeric(progLastId) || progLastId < 0) {
                        $('#program-last-identifier').addClass('error');
                        $('#program-error').show();
                        errorFound = true;
                }
                for (var i=0 ; i < 7 ; i++) {
                    $('#parameter-value-'+i).removeClass('error');
                    $('#parameter-error-'+i).hide();
                    var paramId = $('#parameter-value-'+i).val();
                    if (typeof paramId !== 'undefined') {
                        if(!$.isNumeric(paramId) || paramId < 0) {
                            $('#parameter-value-'+i).addClass('error');
                            $('#parameter-error-'+i).show();
                            errorFound = true;
                        }
                    }
                }
                if (errorFound) {
                   return false;
                }
                $('#program').submit();
            });

            $(document).on('click', '#curtailmentTabs', function () {
                _enableDisableActionsMenu();
            });

            $(document).on("yukon:event:confirm", function () {
                $('#confirm-form').submit();
            });

            $(function () {
                _enableDisableActionsMenu();
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
            
            $(function () {
                $(document).on('click', '#active-programs', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving active customer program to available');
                    _programMove({target: button, operation: 'unassignProgram', data: 'programId', moveTarget: '#available-programs tbody'});
                });
                $(document).on('click', '#available-programs', function (ev) {
                    var button = $(ev.target).closest('button');
                    debug.log('moving available customer program to active');
                    _programMove({target: button, operation: 'assignProgram', data: 'programId', moveTarget: '#active-programs tbody'});
                });
            });
        }
        
    };
    
    return mod;
})();
$(function () { yukon.dr.curtailment.init(); });
