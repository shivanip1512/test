yukon.namespace('yukon.dr.dataUpdater.showAction');

/**
 * Module that provides functionality for updating dr action menus
 * @module yukon.dr.dataUpdater.showAction
 * @requires JQUERY
 */
yukon.dr.dataUpdater.showAction = (function () {

    /**
     * Method to hide the menu action and also set the title.
     * @param {Array.<Object>}  menu - Array of actions.
     * @param {number} paoId - paoId
     */
    var 
    _updateMenu = function(paoId, menu) {
        var actionsEl = $('[data-pao-id='+paoId+']');
        for (var action in menu) {
            actionsEl.filter('[data-' + action + '-action]').hide();
            actionsEl.filter('[data-' + action + '-action="' + menu[action].state + '"]')
                .show().attr('title', menu[action].title);
        }
    },

    /**
     * Method to create and returns a 'menu' defaulting every action to 'on'.
     * @param {Array.<Object>}  menu - Array of actions.
     */
    _getDefaultMenuForActions = function(actions) {
        var menu = {};
        var numActions = actions.length;
        for (var i = 0; i < numActions; i++) {
            menu[actions[i]] = {state : 'on'};
        }
        return menu;
    },

    mod = {

        /**
          * Method to handle enabling and disabling control area menu items.
          * @param {number} paoId - paoId
          * @param {boolean} isGearChangeEnabled - Parameter will enable/disable the gear based on role assigned to user.
          * @param {boolean} enableDisableProgramsAllowed - Parameter will enable/disable the program based on role assigned to user.
        */
        updateControlAreaMenu: function (paoId, isGearChangeEnabled, enableDisableProgramsAllowed) {
            return function(data) {
                var controlAreaState = JSON.parse(data.state);
                var menu = _getDefaultMenuForActions(['start', 'stop', 'change-triggers',
                    'change-time', 'change-triggers', 'enable-control-area','disable-control-area','change-gears', 
                    'enable-programs', 'disable-programs', 'reset-peak']);

                if (controlAreaState.unknown) {
                    for (var action in menu) {
                        menu[action].state = 'off';
                        menu[action].title = controlAreaState.unknownMsg;
                    }
                } else if (controlAreaState.noAssignedPrograms) {
                    for (var action in menu) {
                        menu[action].state = 'off';
                        menu[action].title = controlAreaState.noAssignedProgramsMsg;
                    }
                } else {
                    // if the control area is partially active we leave both 'start' and 'stop' actions enabled
                	if (!isGearChangeEnabled) {
                		menu['change-gears'].state = 'off';
                        menu['change-gears'].title = controlAreaState.noChangeGearsMsg;
                      
                	}
                	if (!enableDisableProgramsAllowed) {
                		menu['enable-control-area'].state = 'off';
                        menu['enable-control-area'].title = controlAreaState.noEnableDisableMsg;
                        menu['disable-control-area'].state = 'off';
                        menu['disable-control-area'].title = controlAreaState.noEnableDisableMsg;
                        menu['enable-programs'].state = 'off';
                        menu['enable-programs'].title = controlAreaState.noEnableDisableMsg;
                        menu['disable-programs'].state = 'off';
                        menu['disable-programs'].title = controlAreaState.noEnableDisableMsg;
                	}  
                    if (controlAreaState.fullyActive) {
                        menu['start'].state = 'off';
                        menu['start'].title = controlAreaState.fullyActiveMsg;
                    } else if(controlAreaState.inactive) {
                        menu['stop'].state = 'off';
                        menu['stop'].title = controlAreaState.inactiveMsg;
                        menu['change-gears'].state = 'off';
                        menu['change-gears'].title = controlAreaState.inactiveMsg;
                    }
                    if (controlAreaState.disabled) {
                        menu['disable-control-area'].state = 'disable-off';
                    } else {
                        menu['enable-control-area'].state = 'disable-on';
                    }
                }
                
                _updateMenu(paoId, menu);
            };
        },



        /**
         * Method to handle enabling and disabling program menu items.
         * @param {number} paoId - paoId
         * @param {boolean} isGearChangeEnabled - Parameter will enable/disable the gear based on role assigned to user.
         * @param {boolean} enableDisableProgramsAllowed - Parameter will enable/disable the program based on role assigned to user.
         */
         updateProgramMenu: function (paoId, isGearChangeEnabled, enableDisableProgramsAllowed) {
            return function(data) {
                var programState = JSON.parse(data.state);
                
                var menu = _getDefaultMenuForActions(['start', 'stop', 'change-gears', 
                                                                'enable-program','disable-program']);

                if (programState.unknown) {
                    for (var action in menu) {
                        menu[action].state = 'off';
                        menu[action].title = programState.unknownMsg;
                    }
                } else {
                    if (programState.running) {
                    	if (!isGearChangeEnabled) {
                    		menu['change-gears'].state = 'off';
                            menu['change-gears'].title = programState.noChangeGearsMsg;
                    	} 
                    	if (!enableDisableProgramsAllowed) {
                       	 menu['enable-program'].state = 'off';
                         menu['enable-program'].title = programState.noEnableDisableMsg;
                         menu['disable-program'].state = 'off';
                         menu['disable-program'].title = programState.noEnableDisableMsg;
                       	} 
                        menu['start'].state = 'off';
                        menu['start'].title = programState.alreadyRunningMsg;
                    } else {
                        menu['change-gears'].state = 'off';
                        menu['change-gears'].title = programState.changeGearsDisabledMsg;
                        if (!programState.scheduled) {
                            // Not running but only turn off 'stop' action if we arn't scheduled
                            menu['stop'].state = 'off';
                            menu['stop'].title = programState.notRunningMsg;
                        }
                        if (!enableDisableProgramsAllowed) {
                        	menu['enable-program'].state = 'off';
                            menu['enable-program'].title = programState.noEnableDisableMsg;
                            menu['disable-program'].state = 'off';
                            menu['disable-program'].title = programState.noEnableDisableMsg;
                       	}
                    }
                    if (programState.disabled) {
                    	 menu['disable-program'].state = 'disable-off';
                    } else {
                    	 menu['enable-program'].state = 'enable-off';
                    }
                }
                _updateMenu(paoId, menu);
            };
         },
    };
    return mod;
}());
