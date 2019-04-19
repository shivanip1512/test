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
            if (search_term !== '') search.addClass('error');
        }
    };
    
    return {
        
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
                    if (search.val() === '') {
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
        },
        
        /** 
         * Build the tree in the container using dynatree.
         * 
         * @param {object} options          - Object containing the options listed below.
         * @param {JQuery object} container - The container element.
         *                                    Expected to contain a child element with a class name of 'tree-canvas'.
         * @param {object} groups           - The JSON object of groups for the dynatree plugin.
         * @param {String} groupDataUrl     - The URL for the data for the tree.
         * @param {Array} [selected]        - Optional array of selected node id's. Default: [].
         * @param {Array} [nodeId]          - The name of the metadata property to use when preselecting nodes. 
         *                                    Default: 'groupName'. i.e. 'groupName' would indicate each node has a 
         *                                    'node.data.metadata.groupName' property.
         * @param {boolean} [multi]         - If 'true' tree selection mode will be multi-selection, 
         *                                    otherwise single-selection. Default: 'false'.
         */
        build: function(options, dynatreeArgs) {
            
            options = $.extend({
                selected: [],
                nodeId: 'groupName',
                multi: false
            }, options);
            
            options.container.find('.tree-canvas').dynatree($.extend({
                
                initAjax: {
                    url: options.groupDataUrl,
                },
                children: options.groups,
                
                // Prevent the top level elements (visually - dynatree has 1 hidden root by default) 
                // from expanding/collapsing.
                minExpandLevel: 2,
                
                selectMode: options.multi ? 2 : 1,
                checkbox: false,
                onClick: function (node, event) {
                    if (node.getEventTargetType(event) !== 'expander') {
                        if (!node.data.isFolder) {
                            node.toggleSelect();
                        }
                    }
                },
                onSelect: function(selected, node) {
                    if (selected) {
                        var nodes = node.tree.getSelectedNodes(false);
                        nodes.forEach(function (selectedNode) {
                            debug.log('Node \'' + selectedNode.data.metadata[options.nodeId] + '\' selected.');
                        });
                    } else {
                        debug.log('Node \'' + node.data.metadata[options.nodeId] + '\' unselected.');
                    }
                },
                onDblClick: function (node, event) {
                    node.toggleExpand();
                },
                clickFolderMode: 2,
                activeVisible: false
            }, dynatreeArgs || {}));
            
            var tree = options.container.find('.tree-canvas').dynatree('getTree');
            
            if (options.selected.length) {
                // Show the initially selected item(s)
                tree.visit(function (node) {
                    if (options.selected.indexOf(node.data.metadata[options.nodeId]) !== -1) {
                        node.makeVisible();
                        node.select();
                    }
                });
            } else {
                // Open all of the first level children.
                var root = tree.getRoot();
                if (root.childList != null) {
                    for (var i = 0; i < root.childList.length; i++) {
                        root.childList[i].expand(true);
                    }
                }
            }
            
            // Store the array of selected groups so we can revert to it if they 'cancel'.
            options.container.data('selected', options.selected);
            
        },
        
        /** Adjust the tree max height avoid double scrollbars. */
        adjustMaxHeight: function (container) {
            container = $(container);
            var
            canvas = container.find('.tree-canvas'),
            controls = container.find('.tree-controls'),
            maxHeight = container.height() - controls.outerHeight(true);
            canvas.css('max-height', maxHeight + 'px');
        }
        
    };
    
})();

$(function() { yukon.dynatree.init(); });