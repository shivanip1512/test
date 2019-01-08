yukon.namespace('yukon.da.common');

/**
 * Singleton that manages some common capcontrol page functionality.
 * 
 * @module yukon.da
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.da.common = (function () {
    
    var lock_buttons = function (el_id) {
        //el_id comes in looking like this: "editorForm:hdr_submit_button_1"
        $(document.getElementById(el_id)).click(function () {
            $('input.stdButton').each(function () {
                // jsf puts colons in id names, but colons are invalid in id names so we must clean this up here
                var sanitizeSelector = function (dirtyId) {
                    return dirtyId.replace(/:/g, '\\\:');
                },
                elseCallBack = function () {
                    el_id = sanitizeSelector (el_id);
                    $('[id=' + el_id + ']')[0].disabled=true;
                };

                if (this.id !== el_id) {
                    this.disabled = true;
                } else {
                    setTimeout( elseCallBack, 1000);
                }
            });
        });
    },
    
    /** 
     * This method exist to work around the inability of IE to hide option elements.
     * @param {Object} opt - Options for element.
     */
    _hideOption = function (opt) {
        if ($('html').is('.ie')) {
            $(opt).wrap('<span>').hide();
        } else {
            $(opt).hide();
        }
    },
    
    /** 
     * This method exist to work around the inability of IE to show option elements.
     * @param {Object} opt - Options for element
     */
    _showOption = function (opt) {
        var span;
        if ($('html').is('.ie')) {
            span = $(opt).parent();
            if ($(opt).parent().is('span')) {
                $(opt).show();
                $(span).replaceWith(opt);
            }
        } else {
            $(opt).show();
        }
    },
    
    /** Hides the alert message.*/
    _hideAlertMessage = function () {
        $('#alert-message-container').hide('fade', {}, 3000);
    },
    
    /** Initialize the cap bank tier.*/
    _initBankTier = function () {
        
        var hasControl = $('[data-has-control]').length;

        var banks = $('[data-bank-id]');
        banks.each(function (idx, item){
            
            var row = $(item),
                bankId = row.data('bankId'),
                cbcId = row.data('cbcId'),
                moveBankTitle = row.data('moveBankTitle'),
                cbcInfoTitle = row.data('cbcInfoTitle'),
                bankInfoTitle = row.data('bankInfoTitle'),
                menu = row.find('.dropdown-trigger .dropdown-menu'),
                moveBankOpener = menu.find('.js-move-bank'),
                assignMovedBankOpener = menu.find('.js-assign,.js-return'),
                bankCommandOpener = menu.find('.js-bank-command'),
                stateMenuOpener = menu.find('.js-bank-state'),
                bankInfoOpener = menu.find('.js-bank-info'),
                cbcInfoOpener = menu.find('.js-cbc-info');
            
            if (hasControl) {
                moveBankOpener.click(function (event) {
                    mod.showDialog(moveBankTitle,
                            yukon.url('/capcontrol/move/bankMove?bankid=' + encodeURIComponent(bankId)), 
                            {'height': 650, 'width': 650, 'modal': true}, 
                    '#contentPopup');
                });

                assignMovedBankOpener.click(function () {

                    var command = $(this).is('.js-assign') ? 'assign-here': 'move-back',
                            url = yukon.url('/capcontrol/command/' + bankId + '/' + command);

                    $.getJSON(url).always(function (data) {
                        window.location.reload();
                    });
                });

                bankCommandOpener.click(function (event){
                    mod.getCommandMenu(bankId, event);
                });

                stateMenuOpener.click(function (event){
                    mod.getMenuFromURL(yukon.url('/capcontrol/menu/capBankState?id=' + bankId), 
                            event, 
                            { showNote: true });
                });
            }
            
            bankInfoOpener.click(function (event) {
                mod.showDialog(bankInfoTitle, 
                        '../addInfo/bank?bankId=' + encodeURIComponent(bankId), 
                        { width: 450 });
            });
            
            cbcInfoOpener.click(function (event) {
                mod.showDialog(cbcInfoTitle, 
                        '../capbank/cbcPoints?cbcId=' + encodeURIComponent(cbcId), 
                        { width: 600, height: 600 });
            });

        });
    },
    
    mod = {
        
        init: function () {
            
            /**ASSIGNMENT DIALOG BEHAVIOR */
            
            /** Assign/unassign item */
            $(document).on('click', '.select-box .js-add, .select-box .js-remove', function () {
                
                var btn = $(this),
                    remove = btn.is('.js-remove'),
                    container = btn.closest('.select-box').find(remove ? '.select-box-available' : '.select-box-selected'),
                    item = btn.closest('.select-box-item').remove();
                
                // Move item.
                item.appendTo(container)
                .find('.js-remove, .js-add').toggleClass('js-remove js-add')
                .find('.icon').toggleClass('icon-plus-green icon-cross');
                
                // Show/hide movers.
                item.find('.select-box-item-movers').toggle(!remove);
                
                // Tell yukon's ordered list handler to update the mover buttons.
                container.closest('.select-box')
                .find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
            });
            
            $(document).on('click', 'div.dynamicTableWrapper .pointAddItem', function () {
                pointPicker.show.call(pointPicker);
            });
            
            $(document).on('click', 'div.dynamicTableWrapper .bankAddItem', function () {
                bankPicker.show.call(bankPicker);
            });
            
            /** User changed the season schedule, load season for schedule. */
            $(document).on('change', '.js-season-schedule-select', function () {
                
                var scheduleId = $(this).val(),
                    paoId = $('#paoId').val();
                
                $.ajax({
                    url: yukon.url('/capcontrol/strategy-assignment/' + paoId + '/schedule/' + scheduleId + '/seasons')
                }).done(function (seasons) {
                    $('.js-seasons-table tbody').html(seasons);
                });
            });
            
            /** User changed the holiday schedule, hide holiday strat if 'No Holiday' is chosen. */
            $(document).on('change', '.js-holiday-schedule-select', function () {
                var scheduleId = $(this).val();
                $('.js-holiday-strat').toggleClass('dn', +scheduleId === -1);
            });
            
            /** User clicked save on edit info dialog. */
            $(document).on('yukon:vv:strategy-assignment:save', function (ev) {
                
                var dialog = $('.js-edit-strat-popup');
                
                $('.js-edit-strat-popup form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        dialog.dialog('close');
                        window.location.reload();
                    },
                    error: function (xhr, status, error, $form) {
                        dialog.html(xhr.responseText);
                    }
                });
            });
            
            /** User clicked User Per Phase; clear out phase b and phase c points and change label. */
            $(document).on('click', '.js-per-phase', function () {
                
                var toggle = $(this),
                    perPhaseRow = toggle.closest('tr'),
                    active = perPhaseRow.find('.switch-btn-checkbox').prop('checked'),
                    varText = document.getElementById('varLabel').innerText,
                    phaseAText = document.getElementById('phaseALabel').innerText;

                if (!active) {
                    $('#varPointLabel').find('td:first').text(varText);
                    yukon.pickers['phaseBPointPicker'].removeEvent();
                    yukon.pickers['phaseCPointPicker'].removeEvent();
                } else {
                    $('#varPointLabel').find('td:first').text(phaseAText);
                }

            });
            
            /** Bank Move */
            $(document).on('click', 'li.toggle', function (ev) {
                
                if (ev.target === ev.currentTarget) {
                    var li = $(this);
                    var subMenu = li.find('ul:first');
                    if (subMenu[0]) {
                        subMenu.toggle();
                        li.toggleClass('minus').toggleClass('plus');
                    }
                    return false;
                }
                return true;
            });
            
            _initBankTier();
            
            /* creation menu popup */
            $('.js-cc-create').click(function () {
                var content = $('#contentPopup');
                content.load(yukon.url('/capcontrol/menu/create'), function () {
                    var title = content.find('input.title').val();
                    content.dialog({title: title, modal: true});
                });
            });
            

            $('.js-show-comments').on('click', function () {
                var link = $(this),
                    paoId = link.data('paoId'),
                    paoName = link.data('paoName');

                mod.showComments(paoId, paoName);
            });
        },
        
        /** 
         * Initialize the Sub-station 
         */
        initSubstation: function () {
            
            var feederFilter = $('.js-feeder-filter').eq(0),
                busFilter = $('.js-bus-filter').eq(0);
            
            busFilter.on("change", function (event) {
                
                var rows = $("#subBusTable [data-bus-id]"),
                    busIds = new Array(),
                    sel = busFilter.find(':selected'),
                    busId = + sel.val();
                
                if (busId === 0) {
                    rows.each(function (index, item) {
                        var row = $(item),
                            rowId = + row.data('busId');
                        busIds.push(rowId);
                    });
                } else {
                    busIds.push(busId);
                }
                
                mod.applyBusFilter(busIds);
            });
            
            feederFilter.change(function () {
                
                var rows = $("#fdrTable [data-feeder-id]"),
                    feederIds = new Array(),
                    sel = feederFilter.find(':selected'),
                    feederId = + sel.val();
                
                if (feederId === 0) {
                    rows.each(function (index, item) {
                        var row = $(item),
                        rowId = + row.data('feederId');
                        feederIds.push(rowId);
                    });
                } else {
                    feederIds.push(feederId);
                }
                
                mod.applyFeederFilter(feederIds);
            });
        },
        
        /** 
         * Apply the Bus Filter
         * @param Array.<number> busIds - List of Bus Ids
         */
        applyBusFilter: function (busIds) {
            
            var busRows = $('#subBusTable [data-bus-id]'),
                feederRows = $("#fdrTable [data-parent-id]"),
                feederFilters = $('#feederFilter [value]'),
                feederIds = new Array();
            
            busRows.each(function (index, item) {
                
                var row = $(item),
                    busId = + row.data('busId');
                
                if (busIds.indexOf(busId) !== -1) {
                    row.show();
                } else {
                    row.hide();
                }
            });
            
            feederRows.each(function (index, item) {
                
                var row = $(item),
                    busId = + row.data('parentId'),
                    feederId = + row.data('feederId');
                
                if (busIds.indexOf(busId) !== -1) {
                    feederIds.push( feederId );
                }
            });
            
            $('#feederFilter').find( '[value=0]').attr('selected', true);
            feederFilters.each(function (index, item) {
                
                var option = $(item),
                    optionId = + option.val();
                
                if (feederIds.indexOf(optionId) !== -1 || optionId === 0) {
                    _showOption(option);
                } else {
                    _hideOption(option);
                }
            });
            
            mod.applyFeederFilter(feederIds);
        },
        
        /** 
         * Apply the Feeder filter on the list 
         * @param Array.<number> feederIds - List of feeder Ids
         */
        applyFeederFilter: function (feederIds) {
            
            var feederRows = $("#fdrTable [data-feeder-id]"),
                bankRows = $('#capBankTable [data-parent-id]');
            
            feederRows.each(function (index, item) {
                var row = $(item);
                    feederId = + row.data('feederId');
                    
                if (feederIds.indexOf(feederId) !== -1) {
                    row.show();
                } else {
                    row.hide();
                }
            });
            
            bankRows.each(function (index, item) {
                
                var row = $(item),
                    feederId = + row.data('parentId');
                
                if (feederIds.indexOf(feederId) !== -1) {
                    row.show();
                } else {
                    row.hide();
                }
            });
        },
        
        /** 
         * Get command Menu 
         * @param {number} id - id of the menu item
         * @param {string} event - event name 
         */ 
        getCommandMenu: function (id, event) {
            mod.getMenuFromURL(yukon.url('/capcontrol/menu/commandMenu?id=' + id), event);
        },
        
        /** Check if the page has expired */
        checkPageExpire: function () {
            
            var paoIds = [];
            
            $('[data-pao-id]').each(function () {
                paoIds.push($(this).data('paoId'));
            });
            
            $.ajax({
                url: yukon.url('/capcontrol/pageExpire'),
                data: {'paoIds': paoIds}
            }).done(function (data, textStatus, jqXHR) {
                if (data.expired) {
                    $('#updatedWarning').dialog('open');
                } else {
                    setTimeout(mod.checkPageExpire, 15000);
                }
            });
        },
        
        /** Hide the content pop-up */
        hideContentPopup: function () {
            $('#contentPopup').dialog('close');
        },
        
        /** 
         * Get menu from the URL 
         * @param {string} url - url
         * @param {Object} event - Jquery event object
         * @param {Object} params - Request parameters
         */
        getMenuFromURL: function (url, event, params) {
            
            $.ajax(url).done(function (data) {
                
                $('#menuPopup').html(data);
                mod.showMenuPopup(params);
                
                if (params && params.showNote === true) {
                    $('.js-no-field-message').show();
                }
            });
        },
        
        /** 
         * Select/Check all the check boxes 
         * @param {Object} allCheckBox - Check Box List
         * @param {Object} selector - Selected check box list
         */
        checkAll: function (allCheckBox, selector) {
            $(selector).prop('checked', allCheckBox.checked);
        },
        
        /** Hides the menu pop up.*/
        hideMenu: function () {
            $('#menuPopup').dialog('close');
        },
        
        /** 
         * Show dialog 
         * @param {string} title - Title.
         * @param {string} url - url.
         * @param {string} options - options.
         * @param {Object} selector - Request parameters.
         */
        showDialog: function (title, url, options, selector) {
            
            var dialogArgs = {
                'title': title,
                'width': 'auto',
                'height': 'auto',
                'modal': true
            },
            selector = selector || '<div></div>',
            dialog = $(selector);
            
            $.extend(dialogArgs, options);
            dialog.load(url).dialog(dialogArgs);
        },
        
        /** 
         * Show the menu pop-up  
         * @param {Object} params - Request parameters.
         */
        showMenuPopup: function (params) {
            var dialogArgs = {
                'title': $('#menuPopup #dialogTitle').val(),
                'width': 'auto',
                'height': 'auto',
                'resizable': false,
                'modal': true
            };
            
            $.extend(dialogArgs, params);
            $('#menuPopup').dialog(dialogArgs);
        },
        
        showComments: function (id, name) {
            var title = $('#cc-i18n-text').data('commentsTitle')
                .replace('{0}', name);

            mod.showDialog(title,
                yukon.url('/capcontrol/comments/paoComments?paoId=' + id), 
                {}, 
                '#contentPopup'
            );
        },

        /**  
         * Select/check all the checkboxes
         * @param {Object} allCheckBox - Check Box List
         * @param {Object} selector - Selected check box list
         */
        checkAll: function (allCheckBox, selector) {
            $(selector).prop('checked', allCheckBox.checked);
        },
        
        /** 
         * Lock the buttons for a particular groupId
         * @param {string} groupId - groupId to be locked or disabled.
         * @param {number} secs - seconds
         */
        addLockButtonForButtonGroup: function (groupId, secs) {
            $(document).ready(function () {
                var buttons = $('#' + groupId + ' input'),
                i;
                
                for (i=0; i<buttons.length; i++) {
                    lock_buttons(buttons[i].id);
                }
            });
        },
        
        /** 
         * Disable the buttons on submit 
         * @param {string} groupId - groupId to be locked or disabled.
         */
        lockButtonsPerSubmit: function (groupId) {
            $('#' + groupId + ' input').each(function () {
                this.disabled = true;
            });
        },
        
        /** 
         * Show alert message for the particular action performed
         * @param {string} action - Action performed.
         * @param {string} item - Item on which the action is performed.
         * @param {string} result - Result of action
         * @param {boolean} success - Success indicator flag.
         */
        showAlertMessageForAction: function (action, item, result, success) {
            if (action !== '') {
                action = '"' + action + '"';
            }
            var message = item + ' ' + action + ' ' + result;
            mod.showAlertMessage(message, success);
        },
        
        /** 
         * Show alert message.
         * @param {string} message - Alert message
         * @param {boolean} success - Success indicator flag.
         */
        showAlertMessage: function (message, success) {
            
            var contents = $('#alert-message-contents');
            contents.toggleClass('error', !success);
            contents.toggleClass('success', success);
            
            var userMessage = $("<span/>").text(message);
            contents.html(userMessage.html());
            $('#alert-message-container').show();
            setTimeout (_hideAlertMessage, success ? 3000: 8000);
        },
        
        /** 
         * Show message.
         * @param {string} message - Message to be shown.
         */
        showMessage: function (message) {
            $('#alert-message-contents').html(message);
            $('#alert-message-container').show();
            setTimeout (_hideAlertMessage, 3000);
        }
        
    };
    
    return mod;
}());

$(function () { yukon.da.common.init(); });