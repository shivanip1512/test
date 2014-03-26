yukon.namespace('yukon.dr.program.showActionDataUpdator');

yukon.dr.program.showActionDataUpdator = (function () {
    var 
    _updateLink = function(actionsEl, action, enabled, title) {
        if (enabled) {
            actionsEl.filter("[data-"+action+"-action='disabled']").hide();
            actionsEl.filter("[data-"+action+"-action='enabled']").show();
        } else {
            actionsEl.filter("[data-"+action+"-action='enabled']").hide();
            actionsEl.filter("[data-"+action+"-action='disabled']").show().attr("title", title);
        }
    },

    _updateQuickLinks = function(paoId, canStart, canStop, title) {
        var actions = jQuery("[data-pao-id="+paoId+"]");
        _updateLink(actions, "start", canStart, title);
        _updateLink(actions, "stop", canStop, title);
    },

    _updateDetailLinks = function(linksState) {
        var actions = jQuery("[data-pao-id="+linksState.paoId+"]");
        _updateLink(actions, "start", linksState.canStart, linksState.startTitle);
        _updateLink(actions, "stop", linksState.canStop, linksState.stopTitle);
        _updateLink(actions, "change-gears", linksState.canChangeGears, linksState.gearsTitle);

        if (typeof linksState.canDisable === 'undefined') {
            _updateLink(actions, "disable", false, linksState.disableTitle);
            actions.filter("[data-enable-action]").hide();
        } else if (linksState.canDisable) {
            _updateLink(actions, "disable", true, linksState.disableTitle);
            actions.filter("[data-enable-action]").hide();
        } else {
            actions.filter("[data-disable-action]").hide();
            actions.filter("[data-enable-action]").show();
        }
    },

    mod = {
         updateDetailLinks: function (paoId) {
            return function(data) {
                var programState = JSON.parse(data.state);
                var linksState = {paoId : paoId, 
                        canStart : false, startTitle : programState.unknownMsg,
                        canStop : false, stopTitle : programState.unknownMsg, 
                        canChangeGears : false, gearsTitle : programState.unknownMsg,
                        disableTitle : programState.unknownMsg
                    };

                if (programState.unknown) {
                    _updateDetailLinks(linksState);
                } else if (programState.running) {
                    linksState.canStop = true;
                    linksState.canChangeGears = true;
                    linksState.startTitle = programState.alreadyRunningMsg;
                    linksState.canDisable = !programState.disabled;
                    _updateDetailLinks(linksState);
                } else {
                    linksState.canStart = true;
                    linksState.canDisable = !programState.disabled;
                    linksState.gearsTitle = programState.changeGearsDisabledMsg;
                    if (!programState.disabled && programState.scheduled) {
                        linksState.canStop = true;
                        _updateDetailLinks(linksState);
                    } else {
                        linksState.stopTitle = programState.notRunningMsg;
                        _updateDetailLinks(linksState);
                    }
                }
            };
         },

         updateQuickLinks: function(paoId) {
             return function(data) {
                 var programState = JSON.parse(data.state);
                 if (programState.unknown) {
                     _updateQuickLinks(paoId, false, false, programState.unknownMsg);
                 } else if (programState.running) {
                     _updateQuickLinks(paoId, false, true, programState.alreadyRunningMsg);
                 } else {
                     if (!programState.disabled && programState.scheduled) {
                         _updateQuickLinks(paoId, true, true);
                     } else {
                         _updateQuickLinks(paoId, true, false, programState.notRunningMsg);
                     }
                 }
             };
         }
    };
    return mod;
}());
