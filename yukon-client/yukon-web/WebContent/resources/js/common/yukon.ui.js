/** 
 * UI module - General purpose ui functionality for yukon.
 * 
 * @requires JQUERY
 * @requires blockUI 2.39
 * @requires yukon
 */
yukon.namespace('yukon.ui');
yukon.ui = (function () {
    /** Object to glass out an element, used by #block and #unblock */
   var elementGlass = {
        show: function (element) {
            
            element = $(element);
            var glass;
            
            if (element[0]) {
                glass = element.find('.glass');
                if (!glass[0]) {
                    element.prepend($('<div>').addClass('glass'));
                    glass = element.find('.glass');
                }
                return elementGlass.redraw(glass);
            }
            // nothing to block
            return null;
        },
        
        hide: function (element) {
            $(element).find('.glass:first').fadeOut(200, function () {$(this).remove();});
        },
        
        redraw: function (glass) {
            var container = glass.closest('.js-block-this');
            if (!container.length) {
                container = $(glass).parent();
            }
            // resize the glass
            glass.css('width', container.width())
            .css('height', container.height()).fadeIn(200);
        },
        
        resize: function (ev) {
            elementGlass.redraw($(ev.currentTarget));
        }
    };

    /** Object to glass out the page, used by #block and #unblock */
    var pageGlass = {
        
        requested: false, 
        
        show: function (args) {
            
            var defaults = $.extend({ color:'#000', alpha: 0.25 }, args || {}),
                glass = $('#modal-glass');
            
            if (glass == null) {
                glass = $('<div>').attr('id', 'modal-glass')
                .append($('<div>').addClass('tint').append($('<div>').addClass('loading')))
                .prependTo('body');
            }
            glass.find('.tint').css('opacity', defaults.alpha).css('background-color', defaults.color);
            glass.fadeIn(200);
        },
        
        hide: function () {
            $('#modal-glass').fadeOut(200);
        }
    };
    
    var initialized = false,
    
    /** Initialize the site wide search autocomplete. */
     _initSearch = function () {
        
        var field = $('.yukon-search-form .search-field');
        field.autocomplete({
            delay: 100, // Delay 100ms after keyUp before sending request.
            minLength: 2, // User must type 2 characters before any search is done.
            source: function (request, response) {
                $.ajax({
                    type: 'get',
                    url: yukon.url('/search/autocomplete.json'),
                    dataType: 'json',
                    data: {
                        q: request.term
                    }
                }).done(function (data) {
                    response($.map(data, function (item) {
                        return {
                            label: item.name,
                            value: item.name,
                            link: item.link
                        };
                    }));
                });
            },
            select: function (event, ui) {
                window.location = yukon.url(ui.item.link);
            }
        });
    };

    /** Initialize any chosen selects */
    var initChosen = function (container) {

        $(container).find('.js-init-chosen').each(function () {
            $(this).chosen({ 'width' : $(this).actual('innerWidth') + 11 + 'px' });
        }).removeClass('js-init-chosen');

        $(document).off('click.yukon.chosen', '.chosen-single');
        $(document).on('click.yukon.chosen', '.chosen-single', function () {

            var chosenElem = $(this),
                chosenContainer = chosenElem.closest('.chosen-container'),
                chosenHeight = chosenElem.outerHeight() + chosenContainer.find('.chosen-drop').outerHeight(),
                offsetInContainer = chosenContainer.offset().top - chosenContainer.offsetParent().offset().top,
                minBottom = offsetInContainer + chosenHeight,

                /* The following properties are specific to being in a dialog */
                scrollContainer = chosenElem.closest('.ui-dialog-content'),
                currentBottom = scrollContainer.outerHeight(),
                scrollOffset = scrollContainer.scrollTop();

            /* If we're not in a dialog, scroll the page */
            if (!scrollContainer.length) {

                scrollContainer = $(window);
                currentBottom = scrollContainer.outerHeight() + window.pageYOffset;
                scrollOffset = 0;
            }

            if (minBottom > currentBottom) {
                scrollContainer.scrollTop(minBottom - scrollContainer.outerHeight() + scrollOffset);
            }
        });
    };

    var addEventListeners = function () {

        /** Follow clicks on top level nav menus when not using a touch screen. */
        $(document).on('click', '.yukon-header .menu-title', function (ev) {
            if ($(this).is('[data-url]') && !Modernizr.touch) {
                window.location.href = $(this).data('url');
            }
        });

        $(document).on('dialogresizestart', '.ui-dialog-content', function (ev, ui) {
            var resizeContainer = $(this).find('.js-resize-with-dialog').eq(0);
            var originalHeight = resizeContainer.height();
            resizeContainer.data('originalHeight', originalHeight);
        });

        $(document).on('dialogresize', '.ui-dialog-content', function (ev, ui) {
            var heightDifference = ui.size.height - ui.originalSize.height;
            var resizeContainer = $(this).find('.js-resize-with-dialog').eq(0);
            var originalHeight = resizeContainer.data('originalHeight');
            resizeContainer.css('max-height', originalHeight + heightDifference);
        });

        $(document).on('click', '[data-edit-toggle]', function () {
            var btn = $(this);
            var groupName = btn.data('editToggle');
            var fields = $('[data-edit-group="' + groupName + '"]');

            var editing = btn.closest('.js-edit,.js-view').is('.js-view');

            fields.find('.js-view').toggleClass('dn', editing);
            fields.find('.js-edit').toggleClass('dn', !editing);

            fields.find('.js-edit :input').each(function (idx, elem) {
                elem = $(elem);
                var originalValue;
                if (editing) {
                    originalValue = elem.val();
                    if (elem.is(':checkbox') || elem.is(':radio')) {
                        originalValue = elem.is(':checked');
                    }
                    elem.data('originalValue', originalValue);
                    elem.attr('data-original-value', originalValue);
                } else {
                    originalValue = elem.data('originalValue');
                    if (elem.is(':checkbox') || elem.is(':radio')) {
                        elem.prop('checked', originalValue);
                    }
                    elem.val(originalValue).trigger('change');
                }
            });
        });

        /** Fix file path on all yukon <tags:file> usage in windows. */
        $(document).on('change', '.file-upload input[type="file"]', function (ev) {

            var input = $(this),
                container = input.closest('.file-upload'),
                nameInput = container.find('.file-name'),
                value = input.val();

            // Windwows adds C:\fakepath\ for security reasons.
            value = value.replace('C:\\fakepath\\', '');
            nameInput.text(value);
        });

        /** Toggle selection (pipe) on rows of selectable tables. */
        $(document).on('click', 'table.selectable tbody tr, ul.selectable li', function (ev) {
            $(this).addClass('selected').siblings().removeClass('selected');
        });

        /** Sorting Handler: Sort a table by column. */
        $(document).on('click', '.sortable', function (ev) {

            var anchor = $(this),
                dir = anchor.is('.desc') ? 'asc' : 'desc',
                sort = anchor.data('sort'),
                container = anchor.closest('[data-url]'),
                loadEvent = container.data('loadEvent'),
                url = container.data('url'),
                pagingArea = container.find('.paging-area'),
                params = {sort: sort, dir: dir};

            // Add page size if paging is available
            if (pagingArea.length) {
                params.itemsPerPage = pagingArea.data('pageSize');
                params.page = 1;
            }
            if (container.is('[data-static]')) {
                var joiner = url.indexOf('?') === -1 ? '?' : '&';
                window.location.href = url + joiner + $.param(params);
            } else {
                $.get(url, params).done(function (data) {
                    container.html(data);
                    if (loadEvent) container.trigger(loadEvent);
                });
            }
            
        });

        /** Paging Handler: Get the next or previous page, or change page size. */
        $(document).on('click', yg.selectors.paging, function (ev) {

            var
            target = $(this),
            container = target.closest('[data-url]'),
            loadEvent = container.data('loadEvent'),
            sortables = container.find('.sortable'),
            url = container.data('url'),
            pagingArea = container.find('.paging-area'),
            page = pagingArea.data('currentPage'),
            pageSize = pagingArea.data('pageSize'),
            changePage = target.parent().is('.previous-page') || target.parent().is('.next-page'),
            params = {},
            sort;

            if (changePage) {
                // they clicked the next or previous page buttons
                params.page = target.parent().is('.previous-page') ? page - 1 : page + 1;
                params.itemsPerPage = pageSize;
            } else {
                // they clicked one of the page size links
                params.page = 1;
                params.itemsPerPage = target.data('pageSize');
            }

            // add sorting parameters if necessary
            if (sortables.length) {
                sort = sortables.filter('.desc');
                if (sort.length) {
                    params.dir = 'desc';
                    params.sort = sort.data('sort');
                } else {
                    sort = sortables.filter('.asc');
                    if (sort.length) {
                        params.dir = 'asc';
                        params.sort = sort.data('sort');
                    }
                }
            }

            if (container.is('[data-static]')) {
                var joiner = url.indexOf('?') === -1 ? '?' : '&';
                window.location.href = url + joiner + $.param(params);
            } else {
                $.get(url, params).done(function (data) {
                    container.html(data);
                    container.trigger(yg.events.pagingend);
                    if (loadEvent) container.trigger(loadEvent);
                });
            }

            return false; // return false to stop form submissions
        });

        /** Show or hide an element when something is clicked. */
        $(document).on('click', '[data-show-hide]', function (ev) {
            var trigger = $(this);
            var target = $(trigger.data('showHide'));
            if (target.is('[data-url]')) {
                if (target.is(':visible')) {

                    target.slideUp(150);

                    if (trigger.is('.revealer')) {
                        trigger.removeClass('revealer-expanded');
                    }
                } else {
                    target.load(target.data('url'), function () {

                        target.slideDown(150);

                        if (target.is('[data-event]')) {
                            target.trigger(target.data('event'));
                        }
                        if (trigger.is('.revealer')) {
                            trigger.addClass('revealer-expanded');
                        }
                    });
                }
            } else {
                if (target.is(':visible')) {

                    target.slideUp(150);

                    if (trigger.is('.revealer')) {
                        trigger.removeClass('revealer-expanded');
                    }
                } else {

                    target.slideDown(150);
                    if (target.is('[data-event]')) {
                        target.trigger(target.data('event'));
                    }
                    if (trigger.is('.revealer')) {
                        trigger.addClass('revealer-expanded');
                    }
                }
            }
        });

        /** Toggle buttons in a button group */
        $(document).on('click', '.button-group-toggle .button', function (ev) {

            var btn = $(this), input, value;

            btn.addClass('on').siblings('.button').removeClass('on');
            // Toggle visibility of show/hide elements
            if (btn.is('[data-show]')) {
                btn.siblings('[data-show]').each(function () {
                    $($(this).data('show')).hide();
                });
                $(btn.data('show')).show();
            }
            // Set an input if we need to
            if (btn.is('[data-value]')) {
                value = btn.data('value');
                input = btn.is('[data-input]') ? $(btn.data('input')) : btn.siblings('[data-input]');
                if (input.length) input.val(value).change();
            }
        });

        /** Elements that navigate on click */
        $(document).on('click', '[data-href]', function (ev) { window.location = $(this).attr('data-href'); });

        /** Page blockers */
        $(document).on('click', '.js-blocker', function () { mod.block(this); });
        $(document).on('resize', '#modal-glass', mod.blockPage);

        /** Clear page blocker */
        $(document).on('click', '.js-clearBlocker', mod.unblockPage);

        /** Disable a form element after clicked */
        $(document).on('click', '.js-disable-after-click', function (ev) {

            var button = $(this), group, form;

            if (button.is(':input')) {
                button.prop('disabled', true);
                group = button.attr('data-disable-group');
                if (group !== '') {
                    $("[data-disable-group='" + group + "']").each(function (idx) {
                        $(this).prop('disabled', true);
                    });
                }

                // if this is a busy button, add the spinner icon and use the busy text
                if (button.is('[data-busy]')) {
                    mod.busy(button);
                }
                
                // if this is a block page button, block out the page
                if (button.is('[data-block-page]')) {
                    mod.blockPage();
                }

                // if this is a submit button, trigger the submit event on the form
                if (button.is(':submit')) {
                    form = $(this.form);

                    // insert the name and or value of the button into the form action
                    if (typeof button.attr('name') != 'undefined' && button.attr('name').length !== 0) {
                        form.prepend('<input name="'+ button.attr('name') +
                            '" value="' + button.attr('value') + '" type="hidden"/>');
                    }
                    form.trigger('submit');
                }
            }
            return false;
        });

        /** Prevent forms from submitting via enter key */
        $(document).on('keydown', 'form.js-no-submit-on-enter', function (e) {
            // allow override submission elements
            if ($(e.target).hasClass('js-submit-on-enter')) {
                return true;
            }
            if (e.keyCode == yg.keys.enter) {
                return false;
            }
        });

        /** Close dialogs when clicking .js-close elements or the yukon.dialog.ok event fires. */
        $(document).on('click', '.js-close', function (ev) {
            var dialog = $(ev.target).closest('.ui-dialog');
            if (dialog.length) dialog.find('.ui-dialog-content').dialog('close');
        });
        $(document).on('yukon.dialog.ok', function (ev) {
            $(ev.target).closest('.ui-dialog-content').dialog('close');
        });

        $(document).on('blur', 'input.js-format-phone', function (event) {
            mod.formatPhone(event.target);
        });

        /** Enable or disable elements based on the state of a checkbox. */
        $(document).on('change click', '[data-toggle]', function (ev) {
            mod.toggleInputs($(this));
        });

        /** Select all checkbox was clicked, select or unselect all items in a .js-select-all-container. */
        $(document).on('click', '.js-select-all', function (ev) {
            $(this).closest('.js-select-all-container').find('.js-select-all-item').prop('checked', $(this).prop('checked'));
        });

        /** A checkbox in a 'select all' container was clicked. */
        $(document).on('click', '.js-select-all-item', function (ev) {

            var selectAll = true,
                selected = $(this).prop('checked'),
                container = $(this).closest('.js-select-all-container'),
                allItems = container.find('.js-select-all-item');

            if (selected) {
                allItems.each(function (idx, item) { if (!$(item).prop('checked')) selectAll = false; });
                container.find('.js-select-all').prop('checked', selectAll);
            } else {
                container.find('.js-select-all').prop('checked', false);
            }
        });

        /** STEPPER SELECT BEHAVIOR. */
        /** Move selection backward when previous button clicked. */
        $(document).on('mousedown', '.stepper .stepper-prev', function (ev) {

            var btn = $(this);
            var wrapper = btn.closest('.stepper');
            var select = wrapper.find('.stepper-select');
            var prev = select.find('option:selected').prev('option');

            if (!prev.length) {
                prev = select.find('option:last-child');
            }
            prev.prop('selected', true);
            select.trigger('change');

            return true;
        });

        /** Move selection forward when next button clicked. */
        $(document).on('mousedown', '.stepper .stepper-next', function (ev) {

            var btn = $(this);
            var wrapper = btn.closest('.stepper');
            var select = wrapper.find('.stepper-select');
            var next = select.find('option:selected').next('option');

            if (!next.length) {
                next = select.find('option:first-child');
            }
            next.prop('selected', true);
            select.trigger('change');

            return true;
        });

        /** END STEPPER SELECT BEHAVIOR. */

        /** 
         * Show a popup when a popup trigger (element with a [data-popup] attribute) is clicked.
         * If the trigger element has a [data-popup-toggle] attribute and the popup is currently open,
         * the popup will be closed instead and the event propigated normally...otherwise yukon.ui.dialog is 
         * called passing the popup element.
         */
        $(document).on('click', '[data-popup]:not(.disabled)', function (ev) {

            var trigger = $(this),
                popup = $(trigger.data('popup'));

            try { /* Close popup if the trigger is a toggle and the popup is open */
                if (trigger.is('[data-popup-toggle]') && popup.dialog('isOpen')) {
                    popup.dialog('close');
                    // Return so we don't re-open it, return true to propigate event incase others are listening.
                    return true;
                }
            } catch (error) {/* Ignore error, occurs when dialog not initialized yet. */ }

            // Set a create/edit/view mode if possible
            if (trigger.is('[data-mode]')) {
                var mode = trigger.data('mode');
                popup.attr('data-mode', mode).data('mode', mode);
            }

            // show the popup
            mod.dialog(popup);
        });
        
        /**
         * For anchors whose behavior is completely defined by javascript. The anchor is given an href of "#" and
         * "js-no-link" class. This ensures that clicking the link has no effect other than the behavior defined
         * by its other js classes.
         */
        $(document).on('click', '.js-no-link', function (ev) {
            ev.preventDefault();
        });
    };

    var setupPageButtons = function () {

        /** Init page 'Actions' button */
        var pageActions = $('#page-actions');
        var pageActionsButton = $('#b-page-actions');
        var menu = pageActionsButton.find('.dropdown-menu');
        if (pageActions.length) {
            menu.append(pageActions.html());
        }

        /** Init page buttons */
        var pageButtons = $('#page-buttons');
        if (pageButtons.length) {
            pageButtons.remove();
            $('.page-actions').append(pageButtons.html());
        }

        /** Add additional options to page 'Actions' button */
        $(document).find('.js-page-additional-actions').each(function (index, elem) {
            elem = $(elem);
            menu.append(elem.html());
            elem.remove();
        });

        if (menu.children().length) {
            if (menu.find('.icon').length === menu.find('.icon-blank').length) {
                menu.addClass('no-icons');
            }
            pageActionsButton.show();
        }
    };

    var mod = {
        
        init: function () {
            if (!initialized) {
                
                _initSearch();
                
                addEventListeners();
                setupPageButtons();
                mod.initContent();
                
                initialized = true;
                
                mod.wizard.init();
            }
        },
        
        exclusiveSelect: function (item) {
            item = $(item);
            item.siblings().removeClass('selected');
            item.addClass('selected');
        },
        
        busy: function (item) {
            var btn = $(item),
                spinner = btn.children('.icon.busy'),
                label,
                busyText,
                originalText;
            
            btn.prop('disabled', true);
            btn.addClass('busy');
            // if this button has an icon hide it
            btn.children('.icon').hide();
            if (!spinner.length) {
                btn.prepend('<i class="icon busy"></i>');
                spinner = btn.children('.icon.busy');
            }
            spinner.show();
            
            label = btn.children('.b-label');
            busyText = btn.attr('data-busy');
            if (busyText && label.length > 0) {
                originalText = label.html();
                label.html(busyText);
                btn.data('originalText', originalText);
            }
            
            return btn;
        },
        
        unbusy: function (item) {
            var btn = $(item),
                label,
                originalText;
            
            btn.prop('disabled', false);
            btn.removeClass('busy');
            // if this button has an icon show it
            btn.children('.icon').show();
            btn.children('.icon.busy').hide();
        
            label = btn.children('.b-label');
            originalText = btn.data('originalText');
            if (originalText && label.length > 0) {
                label.html(originalText);
            }
            
            return btn;
        },
        
        /** 
         * Returns button array for jquery ui dialogs that contain a 'Cancel' and 'OK' buttons and an
         * optional 'Delete' button.
         * The 'OK' action can have a custom target and/or event.
         * 
         * @param {object} [options]                   - An object literal with the following properties:
         *        {string} [event=yukon.dialog.ok]     - The name of the event to fire when 'ok' button is clicked. 
         *                                               Defaults to 'yukon.dialog.ok'.
         *        {string} [mode=VIEW]                 - When set to 'CREATE', the delete button will not be shown. 
         *                                               Defaults to 'VIEW'.
         *        {string, element} [form]             - If present, submits the form supplied or the first form element 
         *                                               found inside the popup. 'event' is not fired when 'form' is 
         *                                               present. 
         *        {string|element} [target]            - The target of the event (element or selector). 
         *                                               Defaults to the popup.
         *        {string} [okClass='']                - CSS class names applied to the primary (ok) button.
         *        {string} [okText=yg.text.ok]         - The text of the ok button. Defaults to yg.text.ok.
         *        {boolean} [okDisabled=false]         - If true, the primary (ok) button will be initially disabled.
         *        {boolean} [confirm=false]            - If true, after clicking the primary (ok) button, 
         *                                               the dialog will ask for confirmation.
         *        {string} [cancelClass='']            - CSS class names applied to the secondary (cancel) button.
         *        {string} [cancelText=yg.text.cancel] - The text of the cancel button. Defaults to yg.text.cancel.
         *        {boolean} [cancelOmit=false]         - If true, the cancel button will be omitted.
         *        {boolean} [delete=false]             - If true, a 'Delete' button will also be included.
         *                                               Only used when mode is not 'CREATE'.
         */
        buttons: function (options) {
            
            options = $.extend({
                cancelClass : '',
                cancelText : yg.text.cancel,
                cancelOmit : false,
                confirm : false,
                'delete' : false,
                event : 'yukon.dialog.ok',
                mode : 'VIEW',
                okClass : '',
                okDisabled : false,
                okText : yg.text.ok
            }, options || {});
            
            var buttons = [],
            
            okClicked = function (dialog, target) {
                
                if (options.hasOwnProperty('form')) {
                    var form = $(options.form);
                    if (!form.is('form')) form = dialog.find('form');
                    form.submit();
                } else {
                    $(target).trigger(options.event);
                }
            };
            
            // Cancel Button
            if (!options.cancelOmit) {
                buttons.push({
                    text : options.cancelText,
                    click : function (ev) { $(this).dialog('close'); },
                    'class': 'js-secondary-action ' + (options.cancelClass || '')
                });
            }
            
            if (options['delete'] && options.mode !== 'CREATE') {
                // Delete Button
                buttons.push({
                    text : yg.text.deleteButton,
                    click: function (ev) {
                        
                        var dialog = $(this).closest('.ui-dialog-content'),
                            target = options.target || dialog;
                        mod.confirm({
                            dialog : dialog,
                            yesText: yg.text.deleteButton,
                            noText: yg.text.cancel
                        });
                        dialog.off('yukon:ui:dialog:confirm').on('yukon:ui:dialog:confirm', function (ev) {
                            $(target).trigger('yukon:ui:dialog:delete');
                        });
                    },
                    'class' : 'delete'
                });
            }
            
            // OK Button
            buttons.push({
                text : options.okText,
                click : function (ev) {
                    
                    var dialog = $(this).closest('.ui-dialog-content'),
                    target = options.target ? options.target : dialog;
                    if (options.confirm) {
                        mod.confirm({ dialog : dialog });
                        dialog.off('yukon:ui:dialog:confirm').on('yukon:ui:dialog:confirm', function (ev) {
                            okClicked(dialog, target);
                        });
                    } else {
                        okClicked(dialog, target);
                    }
                },
                'class' : 'primary action js-primary-action ' + options.okClass,
                disabled : options.okDisabled
            });
            
            return buttons;
        },
        
        /** 
         * Show a popup. Popup style/behavior should be stored in data attributes described below.
         * 
         * @param {jQuery|string} popup - DOM Object, jQuery DOM object, or css selector string of the popup
         *                               element, usually a div, to use as a popup.
         * @param {string} [url] - If provided, the popup element will be loaded with the response from an
         *                         ajax request to that url. It will override the [data-url] attribute value 
         *                         if it exists.
         *                               
         * The popup element's attributes are as follows:
         * 
         * data-dialog        - If present the popup will have 'ok', 'cancel' buttons. See yukon.ui.buttons
         *                      function for button behaviors.
         * data-dialog-tabbed - If present, the title bar will be tabs. Correct JQuery tabs markup is expected.
         * data-width         - Width of the popup. Default is 'auto'.
         * data-height        - Height of the popup. Default is 'auto'.
         * data-title         - The title of the popup.
         * data-event         - If present and [data-dialog] is present, the value of [data-event] will be the name
         *                      of the event to fire when clicking the 'ok' button.
         * data-target        - If present and [data-dialog] is present' the value of [data-target] will be the 
         *                      target of the event fired when clicking the ok button.
         * data-url           - If present, the contents of the popup element will be replaced with the 
         *                      response of an ajax request to the url before the popup is shown.
         * data-load-event    - If present, this event will be fired right before the popup is shown.
         *                      If 'data-url' is used, the event will be fired after the dialog is loaded with
         *                      the response body.
         * data-popup-toggle  - If present, the trigger element can be clicked to close the popup as well.
         * data-confirm       - If present, after clicking the 'OK' button, the dialog will ask for confirmation.
         * data-mode          - If present, sets a page mode for the popup, passed to buttons. 
         *                      Values: 'CREATE', 'EDIT', 'VIEW'.
         *
         * data-big-content   - If present, takes shortcuts in the dialog rendering.
         *                      Requires data-width and data-height attributes.
         *                      Inspired by
         *                      http://johnculviner.com/a-jquery-ui-dialog-open-performance-issue-and-how-to-fix-it/
         *
         * Positioning options: see http://api.jqueryui.com/position/
         * data-position-my   - 'left|center|right top|center|bottom', Order matters. Default is 'center'
         * data-position-at   - 'left|center|right top|center|bottom', Order matters. Default is 'center'
         * data-position-of   - selector|element.  Default is 'window'.
         */
        dialog: function (popup, url) {
            
            popup = $(popup);
            
            var dialog = popup.is('[data-dialog]'),
                tabbed = popup.is('[data-dialog-tabbed]'),
                bigContent = popup.is('[data-big-content]'),
                loadEvent = popup.data('loadEvent'),
                options = {
                    modal: true,
                    minWidth: popup.is('[data-min-width]') ? popup.data('minWidth') : '150',
                    width: popup.is('[data-width]') ? popup.data('width') : 'auto',
                    height: popup.is('[data-height]') ? popup.data('height') : 'auto',
                    minHeight: popup.is('[data-min-height]') ? popup.data('minHeight') : '150',
                    classes: {
                        "ui-dialog": popup.is('[data-class]') ? 'yukon-dialog ' + popup.data('class') : 'yukon-dialog'
                    },
                    open: function () {
                        if (bigContent) {
                            popup.append(content);
                        }
                        // Check for a focus element
                        mod.autofocus(popup);
                    }
                },
                buttonOptions = {},
                positionOptions = {
                    my: 'center',
                    at: 'center',
                    of: window
                };
            
            if (popup.is('[data-title]')) options.title = popup.data('title');
            if (popup.is('[title]')) options.title = popup.attr('title');
            
            if (dialog) {
                if (popup.is('[data-ok-text]')) buttonOptions.okText = popup.data('okText');
                if (popup.is('[data-ok-class]')) buttonOptions.okClass = popup.data('okClass');
                if (popup.is('[data-ok-disabled]')) buttonOptions.okDisabled = true;
                if (popup.is('[data-cancel-text]')) buttonOptions.cancelText = popup.data('cancelText');
                if (popup.is('[data-cancel-class]')) buttonOptions.cancelClass = popup.data('cancelClass');
                if (popup.is('[data-cancel-omit]')) buttonOptions.cancelOmit = true;
                if (popup.is('[data-event]')) buttonOptions.event = popup.data('event');
                if (popup.is('[data-target]')) buttonOptions.target = $(popup.data('target'));
                if (popup.is('[data-form]')) buttonOptions.form = popup.data('form');
                if (popup.is('[data-confirm]')) buttonOptions.confirm = true;
                if (popup.is('[data-mode]')) buttonOptions.mode = popup.data('mode');
                if (popup.is('[data-delete]')) buttonOptions['delete'] = true;
                options.buttons = mod.buttons(buttonOptions);
            }
            
            if (popup.is('[data-position-my]')) positionOptions.my = popup.data('positionMy');
            if (popup.is('[data-position-at]')) positionOptions.at = popup.data('positionAt');
            if (popup.is('[data-position-of]')) positionOptions.of = popup.data('positionOf');
            options.position = positionOptions;
            
            var content = popup;

            if (popup.is('[data-url]') || url) {
                url = url || popup.data('url');

                if (bigContent) {
                    popup.empty();
                    content = $('<div>');
                }
                mod.blockPage(500);

                content.load(url, function () {
                    
                    // if no title provided, try to find one hidden in the popup contents
                    if (!options.title) {
                        var title = popup.find('.js-popup-title');
                        if (title[0]) options.title = title[0].value;
                    }
                    if (loadEvent) popup.trigger(loadEvent);
                    
                    if (tabbed) {
                        popup.tabbedDialog(options);
                        mod.unblockPage();
                    } else {
                        if (yg.dev_mode) {
                            debug.time('dialog');
                        }
                        popup.dialog(options);
                        if (yg.dev_mode) {
                            debug.timeEnd('dialog');
                        }
                        mod.initContent(popup);
                        mod.unblockPage();
                    }
                });
            } else {
                
                if (loadEvent) popup.trigger(loadEvent);
                if (tabbed) {
                    popup.tabbedDialog(options);
                } else {
                    if (bigContent) {
                        content = popup.children().detach();
                    }
                    popup.dialog(options);
                    mod.initContent(popup);
                }
            }
        },
        
        initContent: function (container) {

            container = container || document;
            container = $(container);

            container.find('[data-edit-group] .js-view')
                .find(':input').not(':button')
                .prop('disabled', true);

            initChosen(container);

            /** Initialize any tabbed containers */
            container.find('.js-init-tabs').each(function (idx, elem) {
                elem = $(elem);
                elem.tabs({'active': elem.data('selectedTab')}).show().removeClass('js-init-tabs');

                if (elem.is('[data-container-name]')) {
                    var containerName = elem.data('containerName');
                    var pageEditMode = $('#pageEditMode').val();
                    if (pageEditMode === 'true') {
                        elem.tabs('option', 'active', window.localStorage.getItem(containerName));
                    } else {
                        window.localStorage.removeItem(containerName);
                        elem.tabs('option', 'active', elem.data('selectedTab'));
                    }
                    elem.tabs('option','activate', function (ev, ui){
                        window.localStorage.setItem(containerName, ui.newTab.index());
                    });
                }
            });
            
            container.find('.y-resizable').each(function (index, elem) {
                var item = $(elem),
                    naturalHeight = item.height();

                item.css({
                    'height': '200px',
                });

                item.resizable({
                    'handles': 's',
                    'minHeight': 50,
                    'maxHeight': naturalHeight
                });
            });

            /** Add placeholder functionality if needed. */
            if (!Modernizr.input.placeholder) {
                container.find('input, textarea').placeholder();
            }

            /** Format phone numbers initially and on input blur */
            container.find('input.js-format-phone').each(function (idx, elem) {
                mod.formatPhone(elem);
            });

            container.find('[data-toggle]').each(function (idx, item) { 
                mod.toggleInputs(item); 
            });


            /** Add additional options to page 'Actions' button */
            container.find('.js-page-additional-actions').each(function (index, elem) {
                elem = $(elem);
                pageActionsButton.find('.dropdown-menu').append(elem.html());
                elem.remove();
            });
            
            /** Focus the designated input element */
            mod.autofocus();
        },
        
        /**
         * Turn dialogs buttons into confirm
         * @param {jQuery} options.dialog        - jQuery element that has had .dialog called on it
         * @param {string} [options.event]       - Event to be fired if the confirm button is pressed
                                                   default is 'yukon:ui:dialog:confirm'
         * @param {jQuery} [options.target]      - element to trigger the event. Default is dialog.
         * @param {string} [options.confirmText] - Confirm text next to buttons
         * @param {string} [options.yesText]
         * @param {string} [options.noText]
         */
        confirm: function (options) {
            
            var dialog = options.dialog,
                target = options.target || dialog,
                event = options.event || 'yukon:ui:dialog:confirm',
                confirmText = options.confirmText || yg.text.confirmMessage,
                yesText = options.yesText || yg.text.yes,
                noText = options.noText || yg.text.no,
                oldButtons = dialog.dialog('option', 'buttons');
            var confirmButton = {
                    text: yesText,
                    click: function (ev) {
                        confirmSpan.remove();
                        dialog.dialog('option', 'buttons', oldButtons);
                        $(target).trigger(event);
                    },
                    'class': 'primary action js-primary-action'
                };
            var cancelAction = function (ev) {
                confirmSpan.remove();
                dialog.dialog('option', 'buttons', oldButtons);
            };
            
            dialog.on('change', ':input', cancelAction);

            var btnPane = dialog.closest('.ui-dialog').find('.ui-dialog-buttonpane');

            var cancelButton = {
                   text: noText,
                   click: cancelAction,
                   'class': 'js-secondary-action '
                };
            var confirmSpan = $('<span>')
                .attr('class', 'fr')
                .css({'line-height': '36px'})
                .text(confirmText)
                .flash({
                    duration: 750,
                    complete: function () {
                        var confirmBtn = btnPane.find('.js-primary-action');
                        confirmBtn.prop('disabled', false);
                    }
                });
            
            dialog.dialog('option', 'buttons', [cancelButton, confirmButton]);
            btnPane.append(confirmSpan);
            btnPane.find('.js-primary-action').prop('disabled', true);
            dialog.on('dialogclose', function () {
                confirmSpan.remove();
            });
        },
        
        AUTOFOCUS_TRIES: 0,
        
        autofocus: function (container) {
            
            var focusElement = !container 
                ? $('[autofocus], .js-focus:first')[0] // find focus in page 
                : $(container).find('[autofocus], .js-focus:first')[0]; // find focus in container
            
            if (focusElement) {
                try { //Play nice with IE
                    focusElement.focus();
                } catch(err) {
                    //give the autofocus element 3 seconds to show itself
                    if (mod.AUTOFOCUS_TRIES < 30) {
                        //certain browsers will error if the element to be focused is not yet visible
                        setTimeout(mod.autofocus, 100);
                    }
                }
            }
        },
        
        /** Sets the focus to the first input/textarea found on the page having a class of "error" */
        focusFirstError: function () {
            mod.setFocusFirstError();
            mod.autofocus();
        },
        
        /** Applies the "js-focus" class to the first input/textarea element having a class of "error" */
        setFocusFirstError: function () {
            
            var error_field = $('input.error, textarea.error').first();
            
            if (error_field.length === 1) {
                $('.js-focus').removeClass('js-focus');
                error_field.addClass('js-focus');
            }
        },
        
        /** Highlights all tabs that have errors in Red and Sets the Active Tab to the First One With Errors */
        highlightErrorTabs: function () {

            /* Save tab selection */
           $('.tabbed-container').each(function (i, tab) {
               var tabContainer = $(tab);
               var firstErroredTab = tabContainer.find('.ui-tabs-panel')[0];
               var errorEncountered = false;
               tabContainer.find('.ui-tabs-panel').each(function (idx, elem) {
                   elem = $(elem);
                   if (elem.find('.error').length) {
                       var id = elem.attr('id');
                       var link = $('[href="#' + id + '"]');
                       link.closest('li').addClass('error');
                       if (!errorEncountered) {
                           firstErroredTab = idx;
                           errorEncountered = true;
                       }
                   }
           });
           tabContainer.tabs();
           tabContainer.tabs('option', 'active', firstErroredTab);

           });
       },

        
        /** Block out the closest valid container to the target element, or the page */
        block: function (target, timeout) {
            timeout = timeout || 0;

            var blockElement = $(target).closest('.js-block-this').eq(0);
            if (blockElement.length) {
                blockElement.data('yukonUiBlock', true); 
                setTimeout(function () {
                    if (blockElement.data('yukonUiBlock')) {
                        elementGlass.show(blockElement);
                    }
                    if (yg.dev_mode) {
                        debug.log("block");
                    }
                }, timeout);
            } else {
                mod.blockPage(timeout);
            }
        },
        
        /** Unblock the closest valid container to the target element, or the page */
        unblock: function (target) {
            var blockElement = $(target).closest('.js-block-this').eq(0);
            if (blockElement.length) {
                blockElement.data('yukonUiBlock', false); 
                elementGlass.hide(blockElement);
                if (yg.dev_mode) {
                    debug.log("unblock");
                }
            } else {
                mod.unblockPage();
            }
        },
        
        /** Block out the whole page */
        blockPage: function (timeout) {
            timeout = timeout || 0;
            pageGlass.waitingToBlock = true;
            setTimeout(function () {
                if (pageGlass.waitingToBlock) {
                    pageGlass.show();
                    if (yg.dev_mode) {
                        debug.log("blockPage");
                    }
                }
            }, timeout);
        },
        
        /** Unblock the whole page */
        unblockPage: function () {
            pageGlass.waitingToBlock = false;
            pageGlass.hide();
            if (yg.dev_mode) {
                debug.log("unblockPage");
            }
        },
        
        /** Add a success alert box. */
        alertSuccess: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'success' });
        },
        
        /** Add an info alert box. */
        alertInfo: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'info' });
        },
        
        /** Add a warning alert box. */
        alertWarning: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'warning' });
        },
        
        /** Add an error alert box. */
        alertError: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'error' });
        },
        
        /** Add a pending alert box. */
        alertPending: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'pending' });
        },
        
        /** Remove all alert boxes from the page. */
        removeAlerts : function () { $('.main-container').removeMessages(); },
        
        /** Format a phone number input */
        formatPhone: function (input) {
            input = $(input);
            // strip the input down to just numbers, then format
            var stripped = input.val().replace(/[^\d]/g, ''),
                i,
                regex,
                format;
                
            if (stripped.length > 0) {
                for (i=0; i<yg.formats.phone.length; i++) {
                    regex = yg.formats.phone[i].regex;
                    format = yg.formats.phone[i].format;
                    if (regex.test(stripped)) {
                        input.val(stripped.replace(regex, format));
                        break;
                    }
                }
            } else {
                input.val('');
            }
        },
        
        /** Enable or disable elements based on the state of a checkbox. */
        toggleInputs: function (checkbox) {
            
            checkbox = $(checkbox);
            var enable = checkbox.is(':checked'),
                toggleGroup = checkbox.data('toggle'),
                inputs = $('[data-toggle-group="' + toggleGroup + '"]');
            
            if (checkbox.is('[data-toggle-inverse]')) {
                enable = !enable;
            }

            if (toggleGroup === '') return;
                
            var action = checkbox.data('toggleAction');

            if (action === 'hide') {
                inputs.each(function (idx, input) {
                    $(input).toggleClass('dn', !enable);
                });
            } else if (action === 'invisible') {
                inputs.each(function (idx, input) {
                    $(input).toggleClass('vh', !enable);
                });
            } else if (action === 'readonly') {
                inputs.each(function (idx, input) {
                    $(input).prop('readonly', !enable);
                    $(input).find('*').attr('readonly', !enable);
                });
            } else {
                inputs.each(function (idx, input) {
                    $(input).prop('disabled', !enable);
                    $(input).find('*').attr('disabled', !enable);
                });
            }
                
        },
        
        /** 
         * Reindex the name of every input in a table to support spring binding.
         * Will also enable/disable any move up/move down buttons properly.
         * 
         * @param {jQuery, string} table - Element or css selector for the table/table ancestor.
         * @param {function} [rowCallback] - Optional function to fire after processing each row.
         *                                   Takes the row element as an arg.
         */
        reindexInputs: function (table, rowCallback) {
            
            table = $(table);
            var rows = table.find('tbody tr');
            yukon.ui.reindexRows(rows, rowCallback);
        },
        
        
        /** 
         * Reindex the name of every input in the given array to support spring binding.
         * Will also enable/disable any move up/move down buttons properly.
         * 
         * @param {jQuery, string} rows - Element or css selector for the array/rows.
         * @param {function} [rowCallback] - Optional function to fire after processing each row.
         *                                   Takes the row element as an arg.
         */
        reindexRows: function (rows, rowCallback) {
            rows.each(function (idx, row) {
                row = $(row);
                var inputs = row.find('input, select, textarea, button');
                
                inputs.each(function (inputIdx, input) {
                    
                    input = $(input);
                    var name;
                    
                    if (input.is('[name]')) {
                        name = $(input).attr('name');
                        input.attr('name', name.replace(/\[(\d+|\?)\]/, '[' + idx + ']'));
                    }
                });
                
                // fix up the move up/down buttons if there are any
                if (row.has('.js-up, .js-down').length) {
                    if (rows.length === 1) { // only one row
                        row.find('.js-up, .js-down').prop('disabled', true);
                    } else if (idx === 0) { // first row
                        row.find('.js-up').prop('disabled', true);
                        row.find('.js-down').prop('disabled', false);
                    } else if (idx === rows.length -1) { // last row
                        row.find('.js-up').prop('disabled', false);
                        row.find('.js-down').prop('disabled', true);
                    } else { // middle row
                        row.find('.js-up').prop('disabled', false);
                        row.find('.js-down').prop('disabled', false);
                    }
                }
                
                if (typeof(rowCallback) === 'function') rowCallback(row);
            });
        },
        
        /** 
         * Adjusts row move up/down buttons so that the first row's move up
         * and the last row's move down buttons are disabled. 
         * 
         * @param {jQuery|string} table - Element or css selector for the table/table ancestor.
         * @param {function} [rowCallback] - Optional function to fire after processing each row.
         *                                   Takes the row element as an arg.
         */
        adjustRowMovers: function (table, rowCallback) {
            
            table = $(table);
            var rows = table.find('tbody tr');
            
            rows.each(function (idx, row) {
                
                row = $(row);
                
                // fix up the move up/down buttons
                if (row.has('.js-up, .js-down').length) {
                    if (rows.length === 1) { // only one row
                        row.find('.js-up, .js-down').prop('disabled', true);
                    } else if (idx === 0) { // first row
                        row.find('.js-up').prop('disabled', true);
                        row.find('.js-down').prop('disabled', false);
                    } else if (idx === rows.length -1) { // last row
                        row.find('.js-up').prop('disabled', false);
                        row.find('.js-down').prop('disabled', true);
                    } else { // middle row
                        row.find('.js-up').prop('disabled', false);
                        row.find('.js-down').prop('disabled', false);
                    }
                }
                
                if (typeof(rowCallback) === 'function') rowCallback(row);
            });
        },
        
        /** Manages wizard style paging, useful in complex popups. */
        wizard: {
            
            _initialized: false,
            
            init: function () {
                $('.js-wizard').each(function (idx, elem) {
                    $(elem).find('.js-next').each(function (index, nextButton) {
                        $(nextButton).on('click', function (ev) {
                            mod.wizard.nextPage($(ev.target).closest('.js-page'));
                        });
                    });
                    
                    $(elem).find('.js-prev').each(function (index, prevButton) {
                        $(prevButton).on('click', function (ev) {
                            mod.wizard.prevPage($(ev.target).closest('.js-page'));
                        });
                    });
                    
                    $(elem).find('.js-page').each(function (idx, elem) {
                        if (idx > 0) {
                            $(elem).hide();
                        } else {
                            $(elem).show();
                        }
                    });
                });
                
                mod.wizard._initialized = true;
            },
            
            nextPage: function (page) {
                
                page = $(page);
                
                var nextPage;
                    
                if (typeof page !== 'undefined') {
                    nextPage = page.nextAll('.js-page')[0];
                    if (typeof nextPage !== 'undefined') {
                        page.hide();
                        $(nextPage).show();
                    }
                }
            },
            
            prevPage: function (page) {
                
                var prevPage;
                
                if (typeof page !== 'undefined') {
                    prevPage = page.prevAll('.js-page')[0];
                    if (typeof prevPage !== 'undefined') {
                        $(page).hide();
                        $(prevPage).show();
                    }
                }
            },
            
            /**
             * Resets the page of the wizard to the first/initial page. Does NOT do
             * anything with the contents
             * 
             * @param wizard - Can be any element in the DOM. 
             *                 If it is the js-wizard container itself, it will reset the page. 
             *                 If it is an arbitrary node, it will search for and reset ALL js-wizard containers within.
             */
            reset: function (wizard) {
                
                wizard = $(wizard);
                
                if (wizard.hasClass('js-wizard')) {
                    $('.js-page', wizard).hide();
                    $('.js-page:first', wizard).show();
                } else {
                    $('.js-wizard .js-page').hide();
                    $('.js-wizard .js-page:first').show();
                }
            }
        },
        /**
         * @param {jQuery|String} row - Element (or selector to element) to be removed.
         *    Expected to have attributes:
         *    data-removed-text: text to confirm operation (eg. 'Item removed from list')
         *    data-undo-text: text to revert change (eg. 'Undo')
         * @param {Function} actionDo - Remove action
         * @param {Function} actionUndo - Undo remove action
         */
        removeWithUndo: function (row, actionDo, actionUndo) {
            
            row = $(row);
            
            var type = row.prop('tagName'),
                removedText = row.closest('[data-removed-text]').attr('data-removed-text'),
                undoText = row.closest('[data-undo-text]').attr('data-undo-text'),
                undoLink = $('<a class="fr" href="javascript:void(0)">' + undoText + '</a>'),
                undo;
            
            undo = $('<' + type + ' class="undo-row"></' + type + '>').hide();
            undo.append($('<span>' + removedText + '</span>'));
            undo.append(undoLink);
            
            actionDo();
            
            undoLink.click(function () {
                actionUndo();
                undo.fadeOut(100, function () {
                    undo.after(row);
                    row.fadeIn(100);
                    undo.remove();
                });
            });
            
            row.fadeOut(100, function () {
                row.after(undo);
                undo.fadeIn(100);
            });
        },
        
        getSortingPagingParameters: function(params) {
            var
            sorting = $('.sortable.desc, .sortable.asc'),
            paging = $('.paging-area');
            
            if (sorting.length > 0) {
                params.sort = sorting.data('sort'),
                params.dir = sorting.is('.desc') ? 'desc' : 'asc';
            }
            params.itemsPerPage = paging.length > 0 ? paging.data('pageSize') : 10;
            params.page = paging.length > 0 ? paging.data('currentPage') : 1;
        },
        
    };
    
    return mod;
})();
/** Initialize the lib */
$(function () {
	yukon.ui.init();
    //turn off ajax caching application-wide by default
    $.ajaxSetup({cache: false});
});