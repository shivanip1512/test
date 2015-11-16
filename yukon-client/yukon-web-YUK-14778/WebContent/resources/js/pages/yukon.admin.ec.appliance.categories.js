/**
 * Handles the energy company appliance category page.
 * 
 * @requires JQUERY
 * @requires JQUERY UI
 */

yukon.namespace('yukon.admin.ec.ac');

yukon.admin.ec.ac = (function () {
    
    var 
    _initialized = false,
    
    /** Update the display name i18n key based on user input. */
    _updateKey = function () {
        // This prefix must match com.cannontech.stars.dr.program.model.Program.java
        var prefix = 'yukon.dr.program.displayname.', 
            key = yukon.ui.util.generateMessageCode(prefix, $('#program-display-name :text').val());

        $('#program-display-name-key .value').html(key);
    },
    
    /** Setup the fields of the program popup */ 
    _setupProgramPopup = function () {
        var form = $('#program-edit').find('form'),
        checkbox;
    
        if (!form.data('multiple') && !form.data('virtual')) {
            checkbox = $('#program-display-name :checkbox');
            if (checkbox.prop('checked')) {
                $('#program-display-name :text').prop('disabled', true)
                    .val($('#program-name .value').text());
                
            }
        }
        
        if (!form.data('multiple')) {
            checkbox = $('#program-short-name :checkbox');
            if (checkbox.prop('checked')) {
                $('#program-short-name :text').prop('disabled', true)
                    .val($('#program-display-name :text').val());
                
            }
        }
        if (!form.data('multiple')) {
            _updateKey();
        }
    },
    
    mod = {};

    mod = {
        
        init: function () {
            if (_initialized) {
                return;
            }
            
            yukon.ui.adjustRowMovers('#assigned-programs');
            
            /** Setup category display name behavior based on 'same as' checkbox */
            $('#display-name-row :checkbox').change(function (ev) {
                var checkbox = $(this);
                if (checkbox.prop('checked')) {
                    $('#category-display-name').val($('#category-name').val()).prop('disabled', true);
                } else {
                    $('#category-display-name').prop('disabled', false).focus();
                }
            });
            
            /** Setup program display name behavior based on 'same as' checkbox */
            $(document).on('change', '#program-display-name :checkbox', function (ev) {
                var checkbox = $(this);
                if (checkbox.prop('checked')) {
                    $('#program-display-name :text').val($('#program-name .value').text()).prop('disabled', true);
                    if ($('#program-short-name :checkbox').prop('checked')) {
                        $('#program-short-name :text').val($('#program-display-name :text').val());
                    }
                } else {
                    $('#program-display-name :text').prop('disabled', false).focus();
                }
                _updateKey();
            });
            
            /** Setup program short name behavior based on 'same as' checkbox */
            $(document).on('change', '#program-short-name :checkbox', function (ev) {
                var checkbox = $(this);
                if (checkbox.prop('checked')) {
                    $('#program-short-name :text').val($('#program-display-name :text').val()).prop('disabled', true);
                } else {
                    $('#program-short-name :text').prop('disabled', false).focus();
                }
            });
            
            /** Make sure to un-disable the display name when editing a category
             *  so it comes with in the request. 
             */
            $('#category-form').on('submit', function (ev) {
                $('#category-display-name').prop('disabled', false);
            });
            
            /** Keep category and display name in sync when 'same as' is checked. */
            $('#category-name').on('input', function (ev) {
                if ($('#display-name-row :checkbox').prop('checked')) {
                    $('#category-display-name').val($('#category-name').val());
                }
            });
            
            /** Keep program short name and program display name in sync when 'same as' is checked. */
            $(document).on('input', '#program-display-name :text', function (ev) {
                if ($('#program-short-name :checkbox').prop('checked')) {
                    $('#program-short-name :text').val($(this).val());
                }
                _updateKey();
            });
            
            /** Load edit popup for program and setup field behavior before showing. */
            $(document).on('click', '.js-edit', function (ev) {
                var row = $(this).closest('tr'),
                    container = $('#assigned-programs');
                
                $('#program-edit').load('editAssignedProgram', {
                    ecId: container.data('ec'),
                    applianceCategoryId: container.data('category'),
                    assignedProgramId: row.data('program')
                }, function () {
                    
                    _setupProgramPopup();
                    
                    $('#program-edit').dialog({ 
                        width: 650,
                        buttons: yukon.ui.buttons({ event: 'yukon.admin.ec.ac.program.save' })
                    });
                });
            });
            
            /** Load unassign popup for program and setup field behavior before showing. */
            $(document).on('click', '.js-remove', function (ev) {
                var row = $(this).closest('tr'),
                    container = $('#assigned-programs');
                
                $('#program-unassign').load('confirmUnassignProgram', {
                    ecId: container.data('ec'),
                    applianceCategoryId: container.data('category'),
                    assignedProgramId: row.data('program')
                }, function () {
                    
                    $('#program-unassign').dialog({ 
                        width: 650,
                        buttons: yukon.ui.buttons({ 
                            event: 'yukon.admin.ec.ac.program.unassign',
                            target: row
                        })
                    });
                });
            });
            
            /** Ok button clicked on edit program popup, save the settings. */
            $(document).on('yukon.admin.ec.ac.program.save', function (ev) {
                
                $('#program-display-name :text, #program-short-name :text').prop('disabled', false);
                
                $('#program-form').ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        window.location.href = window.location.href; 
                    },
                    error: function(xhr, status, error, $form) {
                        $('#program-edit').html(xhr.responseText);
                        _setupProgramPopup();
                    }
                });
            });
            
            /** Ok button clicked on unassign program popup, perform the un-assignment. */
            $(document).on('yukon.admin.ec.ac.program.unassign', function (ev) {
                
                var row = $(ev.target),
                    container = $('#assigned-programs'),
                    params = {
                        ecId: container.data('ec'),
                        applianceCategoryId: container.data('category'),
                        assignedProgramId: row.data('program')
                    };
                
                window.location.href = 'unassignProgram?' + $.param(params); 
                
            });
            

            /** Handle move up and move down clicks for programs. */
            $(document).on('click', '.js-up, .js-down', function (ev) {
                
                var row = $(this).closest('tr'),
                    container = $('#assigned-programs'),
                    direction = $(this).is('.js-up') ? 'up' : 'down';
                
                $.ajax('moveProgram', { 
                    data: {
                        direction: direction,
                        applianceCategoryId: container.data('category'),
                        ecId: container.data('ec'),
                        assignedProgramId: row.data('program')
                    }
                }).done(function (data) {
                    container.html(data);
                });
            });
            
            $(document).on('yukon.admin.ec.ac.program.filter', function (ev) {
                var 
                container = $('#assigned-programs'),
                params = {
                    applianceCategoryId: container.data('category'),
                    ecId: container.data('ec'),
                    filterBy: $('#program-filter').val(),
                    sort: 'PROGRAM_NAME',
                    dir: $('#assigned-programs .sortable').is('desc') ? 'asc' : 'desc'
                };
                container.load('programs-list', params);
                container.data('url', 'programs-list?' + $.param({
                    applianceCategoryId: container.data('category'),
                    ecId: container.data('ec'),
                    filterBy: $('#program-filter').val()
                }));
                $('.js-filter-popup').dialog('close');
                
                var filter = params.filterBy.trim();
                if (filter.length > 0) {
                    $('.js-filter').addClass('left')
                    .find('.b-label').text(yg.text.filter + ': ' + filter);
                    $('.js-clear-filter').show();
                } else {
                    $('.js-filter').removeClass('left')
                    .find('.b-label').text(yg.text.filter);
                    $('.js-clear-filter').hide();
                }
            });
            
            $('.js-clear-filter').click(function (ev) {
                $('#program-filter').val('');
                $(this).trigger('yukon.admin.ec.ac.program.filter');
            });
            
            $('#create-virtual-program-btn').click(function (ev) {
                
                $('#program-edit').load($(this).data('url'), function () {
                    _setupProgramPopup();
                    $('#program-edit').dialog({ 
                        width: 650,
                        buttons: yukon.ui.buttons({ event: 'yukon.admin.ec.ac.program.save' })
                    });
                });
            });
            
            _initialized = true;
        },
        
        addProgramsPopup: function() {
            
            $('#assign-program-form').ajaxSubmit({
                success: function(data, status, xhr, $form) {
                    $('#program-edit').html(data);
                    _setupProgramPopup();
                    $('#program-edit').dialog({ 
                        width: 650,
                        buttons: yukon.ui.buttons({ event: 'yukon.admin.ec.ac.program.save' })
                    });
                }
            });
            
        }
        
    
    };
    
    return mod;
}());

$(function () { yukon.admin.ec.ac.init(); });