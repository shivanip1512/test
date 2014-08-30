yukon.namespace('yukon.deviceGroupPicker');

/**
 * Handles behavior for the deviceGroupPicker.tag 
 * @module yukon.deviceGroupPicker
 * @requires JQUERY
 * @requires yukon
 */
yukon.deviceGroupPicker = (function () {

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
                } else {
                    groups = picker.data('groups');
                    
                    dialog = picker.next('.js-device-group-picker-dialog');
                    picker.data('dialog', dialog);
                    dialog.data('picker', picker);
                    
                    _buildTree(dialog, groups);
                    
                    yukon.ui.dialog(dialog);
                    
                    picker.addClass('js-initialized');
                    
                }
                
            });
            
            /** A selection was made, update the input(s) and close the dialog. */
            $(document).on('yukon.deviceGroupPicker.chosen', '.js-device-group-picker-dialog', function (ev) {
                
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
                        picker.find('span').text(groupNames[0] + " " + moreText.replace('{0}', groupNames.length - 1));
                    } else {
                        picker.find('span').text(groupNames[0]);
                    }
                } else {
                    picker.append('<input type="hidden" name="' + name + '">');
                    picker.find('span').text(picker.data('selectText'));
                }
                
                dialog.dialog('close');
                picker.flashYellow();
                
            });
            
            /** Adjust the tree max height when dialog is resized to avoid double scrollbars. */
            $(document).on('dialogresize', '.js-device-group-picker-dialog', function (ev, ui) {
                var container = $(this).find('.tree-canvas'),
                    controls = $(this).find('.tree-controls'),
                    maxHeight = $(this).height() - controls.outerHeight(true);
                
                container.css('max-height', maxHeight + 'px');
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();
 
$(function () { yukon.deviceGroupPicker.init(); });