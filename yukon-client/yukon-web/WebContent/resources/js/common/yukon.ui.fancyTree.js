yukon.namespace('yukon.ui.fancyTree');

/**
 * Module that handles the behavior for the fancyTree tags
 * @module yukon.ui.fancyTree
 * @requires JQUERY
 * @requires yukon
 */
yukon.ui.fancyTree= (function () {
    
    'use strict';
    
    var
    _initialized = false,
        
    _initializeTree = function () {
        $('.js-fancy-tree').each(function() {
            var dataUrl = $(this).data('url'),
                source = dataUrl ? { url: dataUrl } : yukon.fromJson($(this).find('#js-json-data'));
            $(this).fancytree({
                source: source,
                minExpandLevel: 2,
                icon: false,
                escapeTitles: true,
             });
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            _initializeTree();
            
            if (_initialized) return;
                                                
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ui.fancyTree.init(); });