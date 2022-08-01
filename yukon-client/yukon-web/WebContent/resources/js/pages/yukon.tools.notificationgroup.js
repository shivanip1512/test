yukon.namespace('yukon.tools.notificationgroup');

/**
 * Module that handles the behavior on Notification Group page.
 * @module yukon.tools.notificationgroup
 * @requires JQUERY
 * @requires yukon
 */
yukon.tools.notificationgroup = (function() {
    'use strict';

    var
    _initialized = false,
    
    /* Create a new object and set the properties of the node passed as parameter in the newly created object. */
    _setNodeAttributes = function (selectedNodeInTheTree) {
        var node = new Object();
        node.id = selectedNodeInTheTree.data.uuid;
        node.selected = selectedNodeInTheTree.isSelected();
        node.emailEnabled = selectedNodeInTheTree.data.isEmailEnabled;
        node.phoneCallEnabled = selectedNodeInTheTree.data.isPhoneCallEnabled;
        node.companyName = selectedNodeInTheTree.data.text;
        return node;
    },
    
    /* This function disables all the child nodes of the parent node passed as a parameter and also deselects the child nodes. */
    _disableChildNodes = function (parentNode) {
        parentNode.visit(function (childNode) {
            childNode.unselectable = true;
            childNode.toggleClass("disabled-look", true);
            childNode.render();
        });
    },
    
    _deselectEmailSelection = function (node, selectedNode) {
        if (node.data.hasOwnProperty('isEmailType') && node.data.isEmailType === false) {
            selectedNode.emailEnabled = false;
        }
    },
    
    _deselectPhoneCallSelection = function (node, selectedNode) {
        if (node.data.hasOwnProperty('isPhoneType') && node.data.isPhoneType === false) {
            selectedNode.phoneCallEnabled = false;
        }
    },

    mod = {

        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            /**
             * Check if the tree is displayed (CREATE or EDIT mode and not VIEW mode) and check chechboxes are per the initial selection if any.
             * */
            if ($("#js-notification-tree-id").exists()) {
                var rootNode = $("#js-notification-tree-id").fancytree("getTree").getRootNode();
                
                /* Initialize the tree with the pre-selected/checked nodes and expand such nodes. */
                rootNode.visit(function (treeNode) {
                    /**
                     * For the QA automation code, the checkbox for a node needs to have a unique id.
                     * When a node does have a checkbox, its span has 3 childNodes.
                     * span.childNode[0] - expander icon
                     * span.childNode[1] - checkbox
                     * span.childNode[2] - node text/title
                     * The below block of code inside the if condition sets the id for the checkbox after locating it as explained above.
                     * TODO: Check if there is a better way or an API available to locate the checkbox and set its id.
                     */
                    if (treeNode.checkbox !== false && treeNode.isVisible()) {
                        treeNode.span.childNodes[1].id = treeNode.data.id;
                    }
                    
                    if (treeNode.isSelected()) {
                        treeNode.setExpanded(true);
                        $.each(treeNode.getParentList(), function (index, parentNode) {
                            parentNode.setExpanded(true);
                        }); 
                        treeNode.render();
                        _disableChildNodes(treeNode);
                    }
                });
            }
            
            
            $("#js-notification-tree-id").on("fancytreeclick", function (event, data) {
                var node = data.node;
                /* Check if user clicked on a node without any checknox. Eg. Node with label 'CI Customers' or 'Unassigned Contact'. */
                if (node.hasOwnProperty("checkbox") && node.checkbox === false) {
                    $("#js-notification-settings").addClass("dn");
                    return;
                }
                
                var notificationSettingsCheckboxes = $("#js-notification-settings").find("input[type='checkbox']"),
                    isUnselectable = node.unselectable,
                    notificationSettingContainer = $("#js-notification-settings"),
                    isEmailEnabledBtnGrp = notificationSettingContainer.find("input[name='sendEmail']"),
                    isPhoneCallEnabledBtnGrp = notificationSettingContainer.find("input[name='makePhoneCalls']");
                
                 if (isUnselectable) {
                    node.data.isEmailEnabled = false;
                    node.data.isPhoneCallEnabled = false;
                    
                    $.each(notificationSettingsCheckboxes, function (index, checkbox) {
                        if (isUnselectable) {
                            $(checkbox).attr('disabled', true);
                        } else {
                            $(checkbox).removeAttr("disabled");
                        }
                    });
                    isEmailEnabledBtnGrp.prop("checked", node.data.isEmailEnabled);
                    isPhoneCallEnabledBtnGrp.prop("checked", node.data.isPhoneCallEnabled);
                    return;
                }

                /**
                 * Set the title of the Notification Settings container with the name of the node clicked and display
                 * appropriate value of the send email and make phone call toggle buttons.
                 * */
                notificationSettingContainer.find(".js-box-container-title").text(data.node.title + " " + yg.text.settings);
                notificationSettingContainer.removeClass("dn");
                if (!notificationSettingContainer.data('nodekey')) {
                    notificationSettingContainer.attr('data-nodekey', '');
                }
                notificationSettingContainer.data('nodekey', node.key);
                
                isEmailEnabledBtnGrp.prop("checked", node.data.isEmailEnabled);
                if (node.data.hasOwnProperty('isEmailType') && node.data.isEmailType === true) {
                    isEmailEnabledBtnGrp.removeAttr("disabled");
                } else {
                    isEmailEnabledBtnGrp.attr("disabled", true);
                }
                
                isPhoneCallEnabledBtnGrp.prop("checked", node.data.isPhoneCallEnabled);
                if (node.data.hasOwnProperty('isPhoneType') && node.data.isPhoneType === true) {
                    isPhoneCallEnabledBtnGrp.removeAttr("disabled");
                } else {
                    isPhoneCallEnabledBtnGrp.attr("disabled", true);
                }
            });
            
            /**
             * When a node is clicked uncheck all of its child nodes and disable checkboxes for them.
             */
            $("#js-notification-tree-id").on("fancytreeselect", function (event, data) {
                var node = data.node,
                    isSelected = node.isSelected();
                
                node.visit(function (thisNode) {
                    thisNode.selected = false;
                    thisNode.unselectable = isSelected;
                    thisNode.toggleClass("disabled-look", isSelected);
                    thisNode.render();
                });
            });
            
            /**
             * Value of the Send Email toggle button changed. Retain the changed value in its corresponding node as data attribute.
             */
            $(document).on("change", "input[name='sendEmail']", function (event) {
                var nodeKey = $(this).closest("#js-notification-settings").data("nodekey"),
                    node = $("#js-notification-tree-id").fancytree("getTree").getNodeByKey(nodeKey);
                node.data.isEmailEnabled = $(this).is(":checked");
            });
            
            /**
             * Value of the Make Phone Call toggle button changed. Retain the changed value in its corresponding node as data attribute.
             */
            $(document).on("change", "input[name='makePhoneCalls']", function (event) {
                var nodeKey = $(this).closest("#js-notification-settings").data("nodekey"),
                    node = $("#js-notification-tree-id").fancytree("getTree").getNodeByKey(nodeKey);
                node.data.isPhoneCallEnabled = $(this).is(":checked");
            });
            
            $(document).on('click', '#js-save-notification-group', function (event) {
                var selectedCICustomers = [],
                    selectedUnassignedContact = [],
                    ciCustomerNode = $("#js-notification-tree-id").fancytree("getTree").getNodeByKey("ciCustomers"),
                    unassignedContactsNode = $("#js-notification-tree-id").fancytree("getTree").getNodeByKey("unassignedContacts");
                
                /**
                 * Traverse the tree and prepare a JSON on selected nodes in the same hierarchy/structure as the tree. 
                 */
                $.each(ciCustomerNode.getChildren(), function (index, companyNameNode) {
                    var companyNameSelectedNode = _setNodeAttributes(companyNameNode);
                    companyNameSelectedNode.contacts = [];
                    if (companyNameNode.isSelected()) {
                        selectedCICustomers.push(companyNameSelectedNode);
                        return;
                    }
                    companyNameSelectedNode.emailEnabled = false;
                    companyNameSelectedNode.phoneCallEnabled = false;
                    var addCompanyNameSelectedNodeFlag = false;
                    $.each(companyNameNode.getChildren(), function (index, lastNameFirstNameNode) {
                        var lastNameFirstNameSelectedNode = _setNodeAttributes(lastNameFirstNameNode);
                        lastNameFirstNameSelectedNode.notifications = [];
                        if (lastNameFirstNameNode.isSelected()) {
                            companyNameSelectedNode.contacts.push(lastNameFirstNameSelectedNode);
                            addCompanyNameSelectedNodeFlag = true;
                            return;
                        }
                        lastNameFirstNameSelectedNode.emailEnabled = false;
                        lastNameFirstNameSelectedNode.phoneCallEnabled = false;
                        if (lastNameFirstNameNode.getSelectedNodes().length > 0) {
                            addCompanyNameSelectedNodeFlag = true;
                            $.each(lastNameFirstNameNode.getSelectedNodes(), function (index, notificationsNode) {
                                var notificationSelectedNode = _setNodeAttributes(notificationsNode);
                                _deselectEmailSelection(notificationsNode, notificationSelectedNode);
                                _deselectPhoneCallSelection(notificationsNode, notificationSelectedNode);
                                lastNameFirstNameSelectedNode.notifications.push(notificationSelectedNode);
                            });
                            companyNameSelectedNode.contacts.push(lastNameFirstNameSelectedNode);
                        }
                    });
                    if (addCompanyNameSelectedNodeFlag) {
                        selectedCICustomers.push(companyNameSelectedNode);
                    }
                });
                
                $.each(unassignedContactsNode.getChildren(), function (index, unassignedContactNodeName) {
                    var selectedContactNode = _setNodeAttributes(unassignedContactNodeName);
                    selectedContactNode.notifications = [];
                    if (unassignedContactNodeName.isSelected()) {
                        selectedUnassignedContact.push(selectedContactNode);
                        return;
                    }
                    selectedContactNode.emailEnabled = false;
                    selectedContactNode.phoneCallEnabled = false;
                    if (unassignedContactNodeName.getSelectedNodes().length > 0) {
                        $.each(unassignedContactNodeName.getSelectedNodes(), function (index, notificationNode) {
                            var selectedNotificationNode = _setNodeAttributes(notificationNode);
                            _deselectEmailSelection(notificationNode, selectedNotificationNode);
                            _deselectPhoneCallSelection(notificationNode, selectedNotificationNode);
                            selectedContactNode.notifications.push(selectedNotificationNode);
                        });
                        selectedUnassignedContact.push(selectedContactNode);
                    }
                });
                
                $("#js-ci-customers").val(JSON.stringify(selectedCICustomers));
                $("#js-unassigned-contacts").val(JSON.stringify(selectedUnassignedContact));
                $("#js-notification-grup-settings-form").submit();
            });
            
            /**
             * When an expander for the node is clicked, its child nodes are visible. Iterate through these visible child nodes
             * and set the checkbox id for each of these nodes. This is required for QA automation code.
             * The QA automation code required each checkbox for a node to have a unique id.
             * When a node does have a checkbox, its span has 3 childNodes.
             * span.childNode[0] - expander icon
             * span.childNode[1] - checkbox
             * span.childNode[2] - node text/title
             * The below block of code inside the if condition sets the id for the checkbox after locating it as explained above.
             * TODO: Check if there is a better way or an API available to locate the checkbox and set its id.
             */
            $("#js-notification-tree-id").on("fancytreeexpand", function (event, data) {
                $.each(data.node.getChildren(), function (index, childNode) {
                    if (childNode.checkbox !== false && childNode.isVisible()) {
                        childNode.span.childNodes[1].id = childNode.data.id;
                    }
                });
            });
            
            $(document).on("yukon:notificationGroup:delete", function () {
                yukon.ui.blockPage();
                $('#delete-notificationGroup-form').submit();
            });

            _initialized = true;
        }
    };

    return mod;
})();

$(function () { yukon.tools.notificationgroup.init(); });