yukon.namespace('yukon.dynatree');

/**
 * Module that provides some extra functionality (search and expand/collapse toggles) to the jquery dynatree plugin.
 * 
 * @module   yukon.dynatree
 * @requires JQUERY
 * @requires jquery.dynatree.js
 */
yukon.dynatree = (function () {
    
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
        var
        hits = [],
        search = $(input),
        search_term = search.val(),
        tree = search.closest('.inline-tree').find('.tree-canvas'),
        match = search.val().toLowerCase();
        
        $('.found', tree).removeClass('found');

        if (match.length > 0) {
            tree.dynatree('getRoot').visit(function (node) {
                if (node.data.title.toLowerCase().match(match)) {
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
            if (search_term != '') search.addClass('error');
        }
    },
    
    _mod = {
        
        /** Initialize the module registering the event handlers for the Expand/Collapse buttons and search inputs. */
        init: function () {
            
            if (!_initialized) {
                
                /** Expand / collapse toggle link. */
                $(document).on('click', '.open-all, .close-all', function (ev) {
                     $(this).closest('.inline-tree').find('.tree-canvas').dynatree('getRoot').visit(function (node) {
                         node.expand($(ev.currentTarget).hasClass('open-all'));
                     });
                });
                
                $(document).on('keyup', 'input.tree-search', function (ev) {
                    // Only search when the user has paused typing (275ms).
                    var input = $(ev.currentTarget);
                    clearTimeout(_timeout);
                    input.removeClass('error');
                    _timeout = setTimeout(function () { _search(input); }, 275);
                });
                
                $(document).on('blur', 'input.tree-search', function (ev) {
                    var search = $(ev.currentTarget);
                    if (search.val() == '') {
                        search.removeClass('error');
                    }
                });
                
            }
            
            _initialized = true;
        },
        

        /** 
         * Set the href value of selected node.
         * @param {Object} node - dyna tree node element.
         */
        redirectOnActivate: function (node) {
            if (node.data && node.data.href) {
                window.location = node.data.href;
            }
        }

    };
    
    return _mod;
})();

$(function() { yukon.dynatree.init(); });