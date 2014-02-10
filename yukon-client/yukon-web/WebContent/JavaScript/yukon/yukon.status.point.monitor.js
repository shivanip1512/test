
/**
 * Singleton that manages the javascript for Yukon Status Response Monitor
 * 
 * @requires jQuery 1.8.3+
 */

var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});

yukon.namespace('yukon.StatusPointMonitor');

yukon.StatusPointMonitor = (function () {
    
    var mod = {
        
        /** 
         * Re-index a row so that the spring form binding works 
         */
        reindex: function(row, index) {
            row.data('row', index);
            row.find('.f-row-prev-state').attr('name', 'processors[' + index + '].prevState');
            row.find('.f-row-next-state').attr('name', 'processors[' + index + '].nextState');
            row.find('.f-row-action-type').attr('name', 'processors[' + index + '].actionType');
            row.find('.f-remove').removeAttr('id');
        },
        
        /** 
         * Re-index all rows for spring form binding and
         * adjusts input states for each row.
         */
        reindexAll: function() {
            var rows = jQuery('#processors-table tbody tr');
            rows.each(function(index, elem) {
                mod.reindex(jQuery(elem), index);
            });
        }
    };
    
    return mod;
}());

jQuery(function() {
    
    /** ADD RULE */
    jQuery('.f-add').click(function(event) {
        
        var row = jQuery('.f-template-row').clone();
        
        row.removeClass('f-template-row');
        row.appendTo('#processors-table tbody');
        
        yukon.StatusPointMonitor.reindexAll();
    });
    
    /** REMOVE RULE */
    jQuery(document).on('click','.f-remove', function(event) {
        jQuery(this).closest('tr').remove();
        yukon.StatusPointMonitor.reindexAll();
    });
    
});