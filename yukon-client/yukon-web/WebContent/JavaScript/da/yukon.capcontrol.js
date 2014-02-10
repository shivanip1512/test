var yukon = (function (yukonMod) {
    return yukonMod;
})(yukon || {});
yukon.namespace('yukon.CapControl');
yukon.CapControl = (function () {

    var lock_buttons = function(el_id) {
            //el_id comes in looking like this: "editorForm:hdr_submit_button_1"
            jQuery(document.getElementById(el_id)).click(function() {
                jQuery('input.stdButton').each(function() {
                    // jsf puts colons in id names, but colons are invalid in id names so we must clean this up here
                    var sanitizeSelector = function (dirtyId) {
                        return dirtyId.replace(':', '\\\:');
                    },
                    elseCallBack = function() {
                        el_id = sanitizeSelector (el_id);
                        jQuery('[id=' + el_id + ']')[0].disabled=true;
                    };

                    if (this.id !== el_id) {
                        this.disabled = true;
                    } else {
                        setTimeout( elseCallBack, 1000);
                    }
                });
            });
        },

        _hideAlertMessage = function() {
            jQuery('#alertMessageContainer').hide('fade', {}, 3000);
        },

        capControlMod = {

        init : function() {
            jQuery(document).on('click', 'div.dynamicTableWrapper .pointAddItem', function(event) {
                pointPicker.show.call(pointPicker);
            });
            jQuery(document).on('click', 'div.dynamicTableWrapper .bankAddItem', function(event) {
                bankPicker.show.call(bankPicker);
            });

            /* bank move */
            jQuery(document).on('click', 'li.toggle', function(e) {
                if (e.target === e.currentTarget) {
                    var li = jQuery(this);
                    var subMenu = li.find('ul:first');
                    if (subMenu[0]) {
                        subMenu.toggle();
                        li.toggleClass('minus').toggleClass('plus');
                    }
                    return false;
                }
                return true;
            });

            /* creation menu popup */
            jQuery('.f-cc-create').click(function() {
                var content = jQuery('#contentPopup');
                content.load('/capcontrol/menu/create', function() {
                    var title = content.find('input.title').val();
                    content.dialog({title: title});
                });
            });
        },

        initBankTier: function() {
            
            var banks = jQuery('[data-bank-id]');
            banks.each( function(index, item){
                var row = jQuery(item),
                    bankId = row.attr('data-bank-id'),
                    cbcId = row.attr('data-cbc-id'),
                    moveBankTitle = row.attr('data-move-bank-title'),
                    cbcInfoTitle = row.attr('data-cbc-info-title'),
                    bankInfoTitle = row.attr('data-bank-info-title'),
                    menu = row.find('.dropdown-container .dropdown-menu'),
                    moveBankOpener = menu.find('.f-move-bank'),
                    bankCommandOpener = menu.find('.f-bank-command'),
                    stateMenuOpener = menu.find('.f-bank-state'),
                    bankInfoOpener = menu.find('.f-bank-info'),
                    cbcInfoOpener = menu.find('.f-cbc-info');

                if( moveBankOpener.is('.warning') ){
                    moveBankOpener.click( function(event) {
                        _getMovedBankMenu(bankId, event);
                    });
                } else {
                    moveBankOpener.click( function(event) {
                        capControlMod.showDialog( moveBankTitle, 
                                    '/capcontrol/move/bankMove?bankid=' + encodeURIComponent(bankId), 
                                    {'height': 650, 'width': 650, 'modal': true}, 
                                    '#contentPopup'); 
                    });
                }
                bankCommandOpener.click( function(event){
                    capControlMod.getCommandMenu(bankId, event);
                });

                bankInfoOpener.click( function(event) {
                    capControlMod.showDialog(bankInfoTitle, '../addInfo/bank?bankId=' + encodeURIComponent(bankId), {width: 450} );
                });

                cbcInfoOpener.click( function(event) {
                    capControlMod.showDialog(cbcInfoTitle, '../capbank/cbcPoints?cbcId=' + encodeURIComponent(cbcId), {width: 600, height: 600} );
                });

                stateMenuOpener.click( function(event){
                    capControlMod.getMenuFromURL('/capcontrol/menu/capBankState?id=' + bankId, event);
                });

            });
        },

        initSubstation: function() {
            var feederFilter = jQuery('.f-feeder-filter').eq(0),
                busFilter = jQuery('.f-bus-filter').eq(0);


            busFilter.change( function(event) {
                var rows = jQuery("#subBusTable [data-bus-id]"),
                    busIds = new Array(),
                    sel = busFilter.find(':selected'),
                    busId = + sel.val();
                
                if (busId === 0) {
                    rows.each(function (index, item) {
                        var row = jQuery(item),
                            rowId = + row.attr('data-bus-id');
                        busIds.push(rowId);
                    });
                } else {
                    busIds.push(busId);
                }
                capControlMod.applyBusFilter(busIds);
            });

            feederFilter.change( function(event) {
                var rows = jQuery("#fdrTable [data-feeder-id]"),
                    feederIds = new Array(),
                    sel = feederFilter.find(':selected'),
                    feederId = + sel.val();

                if (feederId === 0) {
                    rows.each(function (index, item) {
                        var row = jQuery(item),
                            rowId = + row.attr('data-feeder-id');
                        feederIds.push(rowId);
                    });
                } else {
                    feederIds.push(feederId);
                }
                capControlMod.applyFeederFilter(feederIds);
            });
        },

        applyBusFilter : function(busIds) {
            var busRows = jQuery('#subBusTable [data-bus-id]'),
                feederRows = jQuery("#fdrTable [data-parent-id]"),
                feederFilters = jQuery('#feederFilter [value]'),
                feederIds = new Array();

            busRows.each( function (index, item) {
                var row = jQuery(item),
                    busId = + row.attr('data-bus-id');

                if (busIds.indexOf(busId) !== -1) {
                    row.show();
                } else {
                    row.hide();
                }
            });

            feederRows.each(function (index, item) {
                var row = jQuery(item),
                    busId = + row.attr('data-parent-id'),
                    feederId = + row.attr('data-feeder-id');

                if (busIds.indexOf(busId) !== -1) {
                    feederIds.push( feederId );
                }
            });

            jQuery('#feederFilter').find( '[value=0]').attr('selected', true);
            feederFilters.each( function(index, item) {
                var option = jQuery(item),
                    optionId = + option.val();

                if (feederIds.indexOf(optionId) !== -1 || optionId === 0) {
                    option.show();
                } else {
                    option.hide();
                }
            });

            capControlMod.applyFeederFilter(feederIds);
        },

        applyFeederFilter : function (feederIds) {
            var feederRows = jQuery("#fdrTable [data-feeder-id]"),
                bankRows = jQuery('#capBankTable [data-parent-id]');

            feederRows.each(function (index, item) {
                var row = jQuery(item);
                    feederId = + row.attr('data-feeder-id');

                if (feederIds.indexOf(feederId) !== -1) {
                    row.show();
                } else {
                    row.hide();
                }
            });

            bankRows.each(function (index, item) {
                var row = jQuery(item),
                    feederId = + row.attr('data-parent-id');

                if (feederIds.indexOf(feederId) !== -1) {
                    row.show();
                } else {
                    row.hide();
                }
            });
        },

        getCommandMenu : function(id, event) {
            capControlMod.getMenuFromURL('/capcontrol/menu/commandMenu?id=' + id, event);
        },

        checkPageExpire : function() {
            var paoIds = [];

            jQuery('[data-pao-id]').each(function() {
                paoIds.push(jQuery(this).attr('data-pao-id'));
            });

            jQuery.ajax({
                url: '/capcontrol/pageExpire',
                data: {'paoIds': paoIds}
            }).success(function(data) {
                if (data.expired) {
                    jQuery('#updatedWarning').dialog('open');
                } else {
                    setTimeout(capControlMod.checkPageExpire, 15000);
                }
            });
        },

        hideContentPopup : function() {
            jQuery('#contentPopup').dialog('close');
        },

        getMenuFromURL : function (url, event, params) {

            jQuery.ajax(url).done( function(data) {
                jQuery('#menuPopup').html(data);
                capControlMod.showMenuPopup(params);
            });
        },

        getMovedBankMenu : function(id, event) {
            capControlMod.getMenuFromURL('/capcontrol/menu/movedBankMenu?id=' + id, event);
        },

        checkAll: function(allCheckBox, selector) {
            jQuery(selector).prop('checked', allCheckBox.checked);
        },

        hideMenu : function () {
            jQuery('#menuPopup').dialog('close');
        },

        showDialog : function(title, url, options, selector) {
            var dialogArgs = { 'title': title,
                               'width'    : 'auto',
                               'height'   : 'auto'
                  },
                selector = selector || '<div></div>',
                dialog = jQuery(selector);

            jQuery.extend( dialogArgs, options);
            dialog.load(url).dialog(dialogArgs);
        },

        showMenuPopup : function (params) {
            var dialogArgs = { 'title': jQuery('#menuPopup input[id="dialogTitle"]').val(),
                             'width'    : 'auto',
                             'height'   : 'auto',
                             'resizable': false
            };

            jQuery.extend(dialogArgs, params);
            jQuery('#menuPopup').dialog(dialogArgs);
        },

        checkAll : function(allCheckBox, selector) {
            jQuery(selector).prop('checked', allCheckBox.checked);
        },

        addLockButtonForButtonGroup : function (groupId, secs) {
            jQuery(document).ready(function() {
                var buttons = jQuery('#' + groupId + ' input'),
                i;

                for (i=0; i<buttons.length; i++) {
                    lock_buttons(buttons[i].id);
                }
            });
        },

        lockButtonsPerSubmit : function (groupId) {
            jQuery('#' + groupId + ' input').each(function() {
                this.disabled = true;
            });
        },

        showAlertMessageForAction : function(action, item, result, success) {
            if (action !== '') {
                action = '"' + action + '"';
            }
            var message = item + ' ' + action + ' ' + result;
            capControlMod.showAlertMessage(message, success);
        },

        showAlertMessage : function(message, success) {
            var contents = jQuery('#alertMessageContents');

            if (success) {
                contents.addClass('success').removeClass('error');
            } else {
                contents.removeClass('success').addClass('error');
            }
            contents.html(message);
            jQuery('#alertMessageContainer').show();
            setTimeout (_hideAlertMessage, success ? 3000 : 8000);
        },

        showMessage : function(message) {
            jQuery('#alertMessageContents').html(message);
            jQuery('#alertMessageContainer').show();
            setTimeout (_hideAlertMessage, 3000);
        }
    };

    return capControlMod;
}());

jQuery(function() {
    yukon.CapControl.init();
});