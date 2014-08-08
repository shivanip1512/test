/**
 * Singleton that manages the all capcontrol pages functionality
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.da');

yukon.da = (function () {

    var lock_buttons = function(el_id) {
            //el_id comes in looking like this: "editorForm:hdr_submit_button_1"
            $(document.getElementById(el_id)).click(function() {
                $('input.stdButton').each(function() {
                    // jsf puts colons in id names, but colons are invalid in id names so we must clean this up here
                    var sanitizeSelector = function (dirtyId) {
                        return dirtyId.replace(/:/g, '\\\:');
                    },
                    elseCallBack = function() {
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

        // these two functions exist to work around the inability of IE to show/hide option elements
        _hideOption = function (opt) {
            if (navigator.appName === 'Microsoft Internet Explorer') {
                $(opt).wrap('<span>').hide();
            } else {
                $(opt).hide();
            }
        },
        
        _showOption = function (opt) {
            var span;
            if (navigator.appName === 'Microsoft Internet Explorer') {
                span = $(opt).parent();
                if ($(opt).parent().is('span')) {
                    $(opt).show();
                    $(span).replaceWith(opt);
                }
            } else {
                $(opt).show();
            }
        },

        _hideAlertMessage = function() {
            $('#alertMessageContainer').hide('fade', {}, 3000);
        },

        mod = {

        init : function() {
            $(document).on('click', 'div.dynamicTableWrapper .pointAddItem', function(event) {
                pointPicker.show.call(pointPicker);
            });
            $(document).on('click', 'div.dynamicTableWrapper .bankAddItem', function(event) {
                bankPicker.show.call(bankPicker);
            });

            /* bank move */
            $(document).on('click', 'li.toggle', function(e) {
                if (e.target === e.currentTarget) {
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

            if ($('[data-has-control]').length) {
                mod.initBankTier();
            }

            /* creation menu popup */
            $('.js-cc-create').click(function() {
                var content = $('#contentPopup');
                content.load(yukon.url('/capcontrol/menu/create'), function() {
                    var title = content.find('input.title').val();
                    content.dialog({title: title});
                });
            });
        },

        initBankTier: function() {

            var banks = $('[data-bank-id]');
            banks.each( function(index, item){
                var row = $(item),
                    bankId = row.attr('data-bank-id'),
                    cbcId = row.attr('data-cbc-id'),
                    moveBankTitle = row.attr('data-move-bank-title'),
                    cbcInfoTitle = row.attr('data-cbc-info-title'),
                    bankInfoTitle = row.attr('data-bank-info-title'),
                    menu = row.find('.dropdown-trigger .dropdown-menu'),
                    moveBankOpener = menu.find('.js-move-bank'),
                    assignMovedBankOpener = menu.find('.js-assign,.js-return'),
                    bankCommandOpener = menu.find('.js-bank-command'),
                    stateMenuOpener = menu.find('.js-bank-state'),
                    bankInfoOpener = menu.find('.js-bank-info'),
                    cbcInfoOpener = menu.find('.js-cbc-info');

                moveBankOpener.click( function(event) {
                        mod.showDialog(moveBankTitle,
                                    yukon.url('/capcontrol/move/bankMove?bankid=' + encodeURIComponent(bankId)), 
                                    {'height': 650, 'width': 650, 'modal': true}, 
                                    '#contentPopup'); 
                });

                assignMovedBankOpener.click( function(event) {
                    var command = $(this).is('.js-assign') ? 'assign-here' : 'move-back',
                        url = yukon.url('/capcontrol/command/' + bankId + '/' + command);

                    $.getJSON(url).always( function (data) {
                        window.location.reload();
                    });
                });

                bankCommandOpener.click( function(event){
                    mod.getCommandMenu(bankId, event);
                });

                bankInfoOpener.click( function(event) {
                    mod.showDialog(bankInfoTitle, '../addInfo/bank?bankId=' + encodeURIComponent(bankId), {width: 450} );
                });

                cbcInfoOpener.click( function(event) {
                    mod.showDialog(cbcInfoTitle, '../capbank/cbcPoints?cbcId=' + encodeURIComponent(cbcId), {width: 600, height: 600} );
                });

                stateMenuOpener.click( function(event){
                    mod.getMenuFromURL(yukon.url('/capcontrol/menu/capBankState?id=' + bankId), event, {showNote: true});
                });
            });
        },

        initSubstation: function() {
            var feederFilter = $('.js-feeder-filter').eq(0),
                busFilter = $('.js-bus-filter').eq(0);


            busFilter.change( function(event) {
                var rows = $("#subBusTable [data-bus-id]"),
                    busIds = new Array(),
                    sel = busFilter.find(':selected'),
                    busId = + sel.val();
                
                if (busId === 0) {
                    rows.each(function (index, item) {
                        var row = $(item),
                            rowId = + row.attr('data-bus-id');
                        busIds.push(rowId);
                    });
                } else {
                    busIds.push(busId);
                }
                mod.applyBusFilter(busIds);
            });

            feederFilter.change( function(event) {
                var rows = $("#fdrTable [data-feeder-id]"),
                    feederIds = new Array(),
                    sel = feederFilter.find(':selected'),
                    feederId = + sel.val();

                if (feederId === 0) {
                    rows.each(function (index, item) {
                        var row = $(item),
                            rowId = + row.attr('data-feeder-id');
                        feederIds.push(rowId);
                    });
                } else {
                    feederIds.push(feederId);
                }
                mod.applyFeederFilter(feederIds);
            });
        },

        applyBusFilter : function(busIds) {
            var busRows = $('#subBusTable [data-bus-id]'),
                feederRows = $("#fdrTable [data-parent-id]"),
                feederFilters = $('#feederFilter [value]'),
                feederIds = new Array();

            busRows.each( function (index, item) {
                var row = $(item),
                    busId = + row.attr('data-bus-id');

                if (busIds.indexOf(busId) !== -1) {
                    row.show();
                } else {
                    row.hide();
                }
            });

            feederRows.each(function (index, item) {
                var row = $(item),
                    busId = + row.attr('data-parent-id'),
                    feederId = + row.attr('data-feeder-id');

                if (busIds.indexOf(busId) !== -1) {
                    feederIds.push( feederId );
                }
            });

            $('#feederFilter').find( '[value=0]').attr('selected', true);
            feederFilters.each( function(index, item) {
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

        applyFeederFilter : function (feederIds) {
            var feederRows = $("#fdrTable [data-feeder-id]"),
                bankRows = $('#capBankTable [data-parent-id]');

            feederRows.each(function (index, item) {
                var row = $(item);
                    feederId = + row.attr('data-feeder-id');

                if (feederIds.indexOf(feederId) !== -1) {
                    row.show();
                } else {
                    row.hide();
                }
            });

            bankRows.each(function (index, item) {
                var row = $(item),
                    feederId = + row.attr('data-parent-id');

                if (feederIds.indexOf(feederId) !== -1) {
                    row.show();
                } else {
                    row.hide();
                }
            });
        },
        
        getCommandMenu : function(id, event) {
            mod.getMenuFromURL(yukon.url('/capcontrol/menu/commandMenu?id=' + id), event);
        },
        
        checkPageExpire : function() {
            var paoIds = [];

            $('[data-pao-id]').each(function() {
                paoIds.push($(this).attr('data-pao-id'));
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

        hideContentPopup : function() {
            $('#contentPopup').dialog('close');
        },

        getMenuFromURL : function (url, event, params) {
            $.ajax(url).done( function(data) {
                $('#menuPopup').html(data);
                mod.showMenuPopup(params);
                if(params && params.showNote === true) {
                    $('.js-no-field-message').show();
                }
            });
        },

        checkAll: function(allCheckBox, selector) {
            $(selector).prop('checked', allCheckBox.checked);
        },

        hideMenu : function () {
            $('#menuPopup').dialog('close');
        },

        showDialog : function(title, url, options, selector) {
            var dialogArgs = { 'title': title,
                               'width'    : 'auto',
                               'height'   : 'auto'
                  },
                selector = selector || '<div></div>',
                dialog = $(selector);

            $.extend( dialogArgs, options);
            dialog.load(url).dialog(dialogArgs);
        },

        showMenuPopup : function (params) {
            var dialogArgs = {
                'title': $('#menuPopup #dialogTitle').val(),
                'width': 'auto',
                'height': 'auto',
                'resizable': false
            };

            $.extend(dialogArgs, params);
            $('#menuPopup').dialog(dialogArgs);
        },

        checkAll : function(allCheckBox, selector) {
            $(selector).prop('checked', allCheckBox.checked);
        },

        addLockButtonForButtonGroup : function (groupId, secs) {
            $(document).ready(function() {
                var buttons = $('#' + groupId + ' input'),
                i;

                for (i=0; i<buttons.length; i++) {
                    lock_buttons(buttons[i].id);
                }
            });
        },

        lockButtonsPerSubmit : function (groupId) {
            $('#' + groupId + ' input').each(function() {
                this.disabled = true;
            });
        },

        showAlertMessageForAction : function(action, item, result, success) {
            if (action !== '') {
                action = '"' + action + '"';
            }
            var message = item + ' ' + action + ' ' + result;
            mod.showAlertMessage(message, success);
        },

        showAlertMessage : function(message, success) {
            var contents = $('#alertMessageContents');

            if (success) {
                contents.addClass('success').removeClass('error');
            } else {
                contents.removeClass('success').addClass('error');
            }
            contents.html(message);
            $('#alertMessageContainer').show();
            setTimeout (_hideAlertMessage, success ? 3000 : 8000);
        },

        showMessage : function(message) {
            $('#alertMessageContents').html(message);
            $('#alertMessageContainer').show();
            setTimeout (_hideAlertMessage, 3000);
        }
    };

    return mod;
}());

$(function() {
    yukon.da.init();
});