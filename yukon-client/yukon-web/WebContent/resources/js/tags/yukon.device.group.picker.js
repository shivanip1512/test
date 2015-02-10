yukon.namespace('yukon.tags.deviceGroupPicker');

/**
 * Handles behavior for the deviceGroupPicker.tag 
 * @module yukon.tags.deviceGroupPicker
 * @requires JQUERY
 * @requires yukon
 */
yukon.tags.deviceGroupPicker = (function () {

    'use strict';
    
    var
    _initialized = false,
    
    /** 
     * Build the tree in the dialog using dynatree.
     * @param {object} dialog - The dialog element.
     * @param {object} groups - The JSON object of groups for the dynatree plugin.
     */
    _buildTree = function(dialog, groups) {
        
        var
        tree,
        picker = dialog.data('picker'),
        mode = picker.is('[data-multi]') ? 2 : 1,
        selected = [],
        
        args = {
            children: groups,
            minExpandLevel: 2, // Prevent the top level elements (visually - dynatree has 1 hidden root by default) from expanding/collapsing
            selectMode: mode,
            checkbox: false,
            onClick: function (node, event) {
                if (node.getEventTargetType(event) != 'expander') {
                    if (!node.data.isFolder) {
                        node.toggleSelect();
                    }
                }
            },
            onDblClick: function (node, event) {
                node.toggleExpand();
            },
            clickFolderMode: 2,
            activeVisible: false
        };
        
        dialog.find('.tree-canvas').dynatree(args);
        
        tree = dialog.find('.tree-canvas').dynatree('getTree');
        
        picker.find(':hidden').each(function (idx, input) {
            // When nothing is selected there will be one hidden input with no value attribute.
            var value = $(input).val();
            if (value) selected.push(value);
        });
        if (selected.length > 0) {
            // Show the initially selected item(s)
            tree.visit(function (node) {
                if (selected.indexOf(node.data.metadata.groupName) != -1) {
                    node.makeVisible();
                    node.select();
                }
            });
        } else {
            // Open all of the first level children.
            var root = tree.getRoot();
            for (var i = 0; i < root.childList.length; i++) {
                root.childList[i].expand(true);
            }
        }
        
        // Store the array of selected groups so we can revert to it on a later 'cancel' action.
        dialog.data('selected', selected);
        
        // Dump this massive attribute since we don't need it anymore.
        picker.removeAttr('data-groups');
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            /** Initialize/Open a device group picker */
            $(document).on('click', '.js-device-group-picker', function (ev) {
                
                var
                picker = $(this),
                initialized = picker.is('.js-initialized'),
                groups, dialog;
                
                if (initialized) {
                    
                    dialog = picker.data('dialog');
                    dialog.dialog('open');
                    yukon.dynatree.adjustMaxHeight(dialog);
                    
                } else {
                    
                    groups = picker.data('groups');
                    dialog = picker.next('.js-device-group-picker-dialog');
                    picker.data('dialog', dialog);
                    dialog.data('picker', picker);
                    
                    _buildTree(dialog, groups);
                    
                    yukon.ui.dialog(dialog);
                    yukon.dynatree.adjustMaxHeight(dialog);
                    
                    picker.addClass('js-initialized');
                    
                }
                
            });
            
            /** A selection was made, update the input(s) and close the dialog. */
            $(document).on('yukon:tags:device:group:picker:chosen', '.js-device-group-picker-dialog', function (ev) {
                
                var
                dialog = $(this),
                picker = dialog.data('picker'),
                tree = dialog.find('.tree-canvas').dynatree('getTree'),
                name = picker.find(':hidden').attr('name'),
                groups = tree.getSelectedNodes(false),
                groupNames = [];
                
                picker.find(':hidden').remove();
                if (groups.length > 0) {
                    groups.forEach(function (group) {
                        var groupName = group.data.metadata.groupName,
                            input = $('<input type="hidden" name="' + name + '">').val(groupName);
                        picker.append(input);
                        groupNames.push(groupName);
                    });
                    if (groupNames.length > 1) {
                        var moreText = picker.data('moreText');
                        moreText.replace('{0}', groupNames.length -1);
                        picker.find('span').text(groupNames[0] + ' ' + moreText.replace('{0}', groupNames.length - 1));
                    } else {
                        picker.find('span').text(groupNames[0]);
                    }
                } else {
                    picker.append('<input type="hidden" name="' + name + '">');
                    picker.find('span').text(picker.data('selectText'));
                }
                
                // Add a marker class so we know a selection was made BEFORE we close the dialog.
                // Too bad we can't add data to the close event :(
                dialog.addClass('js-selection-made');
                dialog.dialog('close');
                picker.flash();
                picker.trigger('yukon:device:group:picker:selection', [groupNames]);
                
            });
            
            /** Adjust the tree max height when dialog is resized to avoid double scrollbars. */
            $(document).on('dialogresize', '.js-device-group-picker-dialog', function (ev, ui) {
                yukon.dynatree.adjustMaxHeight($(this));
            });
            
            /** The dialog was closed. If they didn't click the 'OK' button, undo any selctions they made. */
            $(document).on('dialogclose', '.js-device-group-picker-dialog', function (ev, ui) {
                var 
                dialog = $(this),
                selected = dialog.data('selected'),
                selectionMade = dialog.is('.js-selection-made'),
                tree = dialog.find('.tree-canvas').dynatree('getTree');
                
                if (selectionMade) {
                    dialog.removeClass('js-selection-made');
                    selected = $.map(tree.getSelectedNodes(), function (node) {
                        return node.data.metadata.groupName;
                    });
                    dialog.data('selected', selected);
                } else {
                    // User canceled, revert to previous selections.
                    tree.visit(function (node) {
                        if (selected.indexOf(node.data.metadata.groupName) != -1) {
                            node.makeVisible();
                            node.select();
                        } else { node.select(false); }
                    });
                }
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.tags.deviceGroupPicker.init(); });