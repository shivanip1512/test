yukon.namespace('yukon.da.bus');

/**
 * Module for the volt/var bus page.
 * @module yukon.da.area
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.bus = (function () {

    'use strict';
    var initialized = false,

    mod = {

        addDMVPrefixInCommand : function (selectedItems, picker) {
            var pickerId = picker.pickerId.replace('dmvTestPicker','');
            var commandName = selectedItems[0].dmvTestName;
            var dmvTestCommand = $("input[name=dmvCommandPrefix]").val();
            $('[name="schedules[' + pickerId + '].command"]').val(dmvTestCommand + ": " + commandName);
        },

        /** Initialize this module. */
        init: function () {

            if (initialized) return;

            /** User clicked save on stations assignment dialog. */
            $(document).on('yukon:vv:children:save', function (ev) {

                var container = $(ev.target),
                    parentId = container.data('parentId'),
                    children = [],
                    available = [];

                container.find('.select-box-selected .select-box-item').each(function (idx, item) {
                    children.push($(item).data('id'));
                });

                container.find('.select-box-available .select-box-item').each(function (idx, item) {
                    available.push($(item).data('id'));
                });
                
                $.ajax({
                    url: yukon.url('/capcontrol/buses/' + parentId + '/feeders'),
                    method: 'post',
                    data: { children: children,
                            available: available}
                }).done(function () {
                    window.location.reload();
                });

            });
            
            $(document).on('yukon:vv:schedule:save', function (ev) {

                var dialog = $('.js-edit-sched-popup');

                $('.js-edit-sched-popup form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        dialog.dialog('close');
                        window.location.reload();
                    },
                    error: function (xhr, status, error, $form) {
                        dialog.html(xhr.responseText);
                    }
                });
            });
            
            /** User confirmed intent to delete bus. */
            $(document).on('yukon:da:bus:delete', function () {
                $('#delete-bus').submit();
            });
            
            /** User clicked Enable Dual Bus toggle button; clear out selected alternate sub bus. */
            $(document).on('click', '.js-dual-bus', function () {
                
                var toggle = $(this),
                    enableDualBusRow = toggle.closest('tr'),
                    active = enableDualBusRow.find('.switch-btn-checkbox').prop('checked');

                if (!active) {
                    yukon.pickers['altBusPicker'].removeEvent();
                    $('#altBusError').hide();
                }

            });
            
            /** Schedules popup **/
            $(document).on('click', '.js-add-schedule', function () {
                var idx = $('#schedules-table tr').length - 1;
                var lastIndex = idx - 1;
                var indexString = "[" + lastIndex + "]";
                var nextIndexString = "[" + idx + "]";
                
                var original = $('.js-schedule-add-row');
                var clone = $('.js-schedule-add-row').clone(true);
                clone.removeClass('js-schedule-add-row dn');
                clone.addClass('js-schedule-row');
                clone.insertBefore(original);
                
                //change the name of the elements so it's ready for the next add
                original.find('select, input').each(function(index, item){
                    var name = item.name;
                    var newName = name.replace(indexString, nextIndexString);
                    item.name = newName;
                });
                //also need to change the id for the switch button
                var checkbox = original.find('.switch-btn-checkbox')[0];
                var newId = checkbox.id + "-2";
                checkbox.id = newId;
                original.find('.left, .right').each(function(index, item){
                    item.htmlFor = newId;
                });
            });

            $(document).on('click', '.js-remove-schedule', function () {
                $(this).closest(".js-schedule-row").remove();
            });


            $(document).on('change', '.js-command-options', function () {
                var row = $(this).closest('tr'), dmvTestCommand = $("input[name=dmvCommandPrefix]").val(), 
                          selectIndex = row.index();

                if (dmvTestCommand === this.value) {
                    $.ajax(yukon.url('/capcontrol/buses/' + selectIndex + '/addDmvTestPickerRow')).done( function(data) {
                      row.find('.js-command').append(data);
                      $('[name="schedules[' + selectIndex + '].command"]').addClass('dn');
                    });
                } else {
                    var rowData = row.find('.js-command');
                    rowData.find('.dmv-test-picker-' + selectIndex).remove();
                    $('[name="schedules[' + selectIndex + '].command"]').removeClass('dn');
                    this.nextElementSibling.value = this.value;
                }
            });
            
            yukon.ui.highlightErrorTabs();
        }
    };

    return mod;
})();

$(function () { yukon.da.bus.init(); });