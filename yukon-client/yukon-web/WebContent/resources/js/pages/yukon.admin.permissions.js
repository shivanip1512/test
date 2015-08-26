yukon.namespace('yukon.admin.permission');

/**
 * Module to handle the behavior on the permissions pages.
 * @module yukon.admin.permission
 * @requires JQUERY
 * @requires yukon
 */
yukon.admin.permission = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    /** 
     * @type {object} data - Holds page data allowing the same page to be used for a user or a group.
     * @type {String} data.type - Either 'user' or 'group' 
     * @type {Number} data.id - The user or group id. 
     * @type {Array} data.lm_exclude - Array of pao ids to exclude from lm picker. 
     * @type {Array} data.vv_exclude - Array of pao ids to exclude from vv picker. 
     */
    _data = {};
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            _data = yukon.fromJson('#permission-data');
            
            // Tell pickers what paos already have permissions set
            yukon.pickers['lmPicker'].excludeIds = _data.lm_exclude;
            yukon.pickers['vvPicker'].excludeIds = _data.vv_exclude;
            
            // Remove a permission when remove button is clicked.
            $(document).on('click', '.js-delete', function (ev) {
                
                var btn = $(this);
                var row = btn.closest('tr');
                var table = btn.closest('table');
                var paoId = row.data('id');
                var permission = table.data('permission');
                var pickerId = table.is('#lm-table') ? 'lmPicker' : 'vvPicker';
                var picker = yukon.pickers[pickerId];
                
                yukon.ui.busy(btn);
                
                debug.log('remove permission on:' + paoId);
                
                $.ajax({
                    url: yukon.url('/admin/' + _data.type + '/' + _data.id + '/permissions/remove'),
                    data: {
                        permission: permission,
                        paoId: paoId
                    }
                }).done(function() {
                    // Delete row and remove paoId from picker exclusion.
                    row.find('td').fadeOut('fast', function() {
                        row.remove();
                    });
                    var index = picker.excludeIds.indexOf(paoId);
                    picker.excludeIds.splice(index, 1);
                });
            });
            
            // Add permissions after picker selection made.
            $(document).on('yukon:admin:permission:add', function (ev, items, picker) {
                
                debug.log('add permissions:', ev, items, picker);
                
                var paoIds = items.map(function (item) { return item.paoId; });
                var permission = picker.pickerId == 'lmPicker' ? 'LM_VISIBLE' : 'PAO_VISIBLE';
                var table = picker.pickerId == 'lmPicker' ? '#lm-table' : '#vv-table';
                
                $.ajax({
                    url: yukon.url('/admin/' + _data.type + '/' + _data.id + '/permissions/add'),
                    data: {
                        'paoIds': paoIds,
                        permission: permission
                    },
                    type: 'post'
                }).done(function(paos) {
                    
                    // Add rows to table
                    paos.forEach(function (pao) {
                        
                        var row = $('.js-templates .js-template-row').clone()
                        .removeAttr('id').data('id', pao.id).attr('data-id', pao.id)
                        .appendTo(table);
                        
                        row.find('td:first-child').text(pao.name)
                        .next().text(pao.type);
                    });
                    
                    // Clear picker selection and add new paos to pickers excluded list.
                    picker.clearEntireSelection();
                    picker.excludeIds = picker.excludeIds.concat(paoIds);
                }).fail(function(){
                    yukon.ui.alertError(yg.text.ajaxError);
                });
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.admin.permission.init(); });