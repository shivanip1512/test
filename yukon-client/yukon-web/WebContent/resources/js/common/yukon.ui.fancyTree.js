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
    
    /** @type {number} - The setTimeout Id for checking user input */
    _timeout = null,
    
    /** 
     * Returns the search results based on user input.
     * @param {Object} input - input element
     */
    _search = function (input) {
        var hits = [],
            search = $(input),
            search_term = search.val(),
            tree = search.closest('.inline-tree').find('.tree-canvas'),
            match = search.val().toLowerCase();
        
        $('.found', tree).removeClass('found');
        
        if (match.length > 0) {
            tree.fancytree('getTree').visit(function (node) {
                if (node.data.text.toLowerCase().match(match)) {
                    node.makeVisible();   //show this guy
                    if (node.span) {
                        node.span.className += ' found';
                    }
                }
            }, false);
        }
        hits = $('.found', tree);
        if (hits[0]) {
            tree.scrollTo($('.found:eq(0)', tree)[0]);
        } else {
            if (search_term !== '') search.addClass('error');
        }
    },
        
    _initializeTree = function () {
        $('.js-fancy-tree').each(function() {
            var dataUrl = $(this).data('url'),
                treeParameters = yukon.fromJson($(this).find('#js-tree-parameters')),
                source = dataUrl ? { url: dataUrl } : yukon.fromJson($(this).find('#js-json-data')),
                options = $.extend({
                    source: source,
                    minExpandLevel: 2,
                    icon: false,
                    escapeTitles: true
                }, JSON.parse(treeParameters) || {});
            $(this).fancytree(options);
            $(this).find('.fancytree-container').addClass('fancytree-connectors');
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            _initializeTree();
            
            if (_initialized) return;
            
            /** Expand All. */
            $(document).on('click', '.fancytree-open-all', function () {
                var treeId = $(this).data('treeId');
                $('#' + treeId).fancytree('getTree').expandAll();
            });
            
            /** Collapse All. */
            $(document).on('click', '.fancytree-close-all', function () {
                var treeId = $(this).data('treeId');
                $('#' + treeId).fancytree('getTree').expandAll(false);
            });
            
            $(document).on('keyup', 'input.fancytree-search', function (ev) {
                // Only search when the user has paused typing (275ms).
                var input = $(ev.currentTarget);
                clearTimeout(_timeout);
                input.removeClass('error');
                _timeout = setTimeout(function () { _search(input); }, 275);
            });
            
            $(document).on('blur', 'input.fancytree-search', function (ev) {
                var search = $(ev.currentTarget);
                if (search.val() === '') {
                    search.removeClass('error');
                }
            });
                                                
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ui.fancyTree.init(); });