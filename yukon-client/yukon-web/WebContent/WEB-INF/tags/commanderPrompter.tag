<%-- This tag was created to i18n the dialog created in the processCommanderReplacement function --%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script>
    var params = [];
    
    function loadCommanderCommand(event) {
        // init params for this command
        params = [];
        var selectElement = jQuery(event.currentTarget);
        var originalCmd = selectElement.val();
        // parse for parameters
        getCommanderParams(originalCmd);
        // replace any escaped question marks (double ?) with single ?
        originalCmd = originalCmd.replace('??', '?');
        // kick off replacements
        processCommanderReplacement(0, originalCmd, originalCmd, selectElement.attr("data-cmdfield"));
    }
    
    // prompt for a replacement for the parameter
    // replacements will stop when no more parameters exist
    function processCommanderReplacement(paramIdx, originalCmd, cmd, cmdField) {
        var param = params[paramIdx];
        if (param != undefined) {
            var displayParam = params[paramIdx].replace(/'/g, '').replace(/"/g, '').replace('?', '');
            jQuery("#commanderPrompterConfirm label>span").text(displayParam);
            jQuery("#commanderPrompterConfirm").dialog({
                resizable: false,
                modal: true,
                title: jQuery("#commanderPrompterConfirm").attr('data-title'), 
                buttons: {
                    "<cti:msg key="yukon.web.components.dialog.ok"/>": function() {
                        jQuery( this ).dialog( "close" );
                        replacement = jQuery( this ).find("input:text").val();
                        cmd = cmd.replace(params[paramIdx], replacement);
                        jQuery(document.getElementById(cmdField)).val(cmd);
                        // try next replacements
                        processCommanderReplacement(paramIdx + 1, originalCmd, cmd, cmdField);
                    },
                    "<cti:msg key="yukon.web.components.dialog.cancel"/>": function() {
                        jQuery( this ).find("input:text").val("");
                        jQuery( this ).dialog( "close" );
                        //the EXT version of this tried to populate an undefined field.  Pretty sure this
                        //is what was meant, but I leave the following in as this makes more sense to me.
                        //jQuery(document.getElementById(cmdField)).val(originalCmd);
                    }
                }
            });
        }
        else {
            // no parameter on first attempt, set command value as-is
            if (paramIdx == 0) {
                $(cmdField).value = originalCmd;
            }
        }
        
    }
    
    function getCommanderParams(cmd) {
        var cmdIdx = 0;
        while(cmdIdx < cmd.length) {
            // search for ?' or ?" to that indicates the beginning of a prompt replacement
            if (cmd.charAt(cmdIdx) == '?') {
                var startIdx = cmdIdx;
                var endIdx = null;
                if (cmd.charAt(cmdIdx + 1) == '?') {
                    cmdIdx = cmdIdx + 2;
                    continue;
                }
                // quoted prompt string if quote immediately follows ?
                if (cmd.charAt(cmdIdx + 1) == "'" || cmd.charAt(cmdIdx + 1) == '"') {
                    for (j = cmdIdx + 2; j < cmd.length; j++) {
                        if (cmd.charAt(j) == "'" || cmd.charAt(j) == '"') {
                            endIdx = j;
                            break;    
                        }
                    }
                }
                // unquoted prompt string
                // ends at space or quote that is to be preserved, or eol
                else {
                    // Skip over any spaces at the beginning of the prompt
                    var j = cmdIdx + 1;
                    for (j = cmdIdx + 1; j < cmd.length; j++) {
                        if (cmd.charAt(j) != ' ') {
                            break;
                        }
                    }
                    for (j; j < cmd.length; j++) {
                        if (cmd.charAt(j) == ' ' || cmd.charAt(j) == "'" || cmd.charAt(j) == '"') {
                            endIdx = j - 1;
                            break;    
                        }
                    }
                    if (endIdx == null) {
                        endIdx = cmd.length;
                    }
                }
                // pull the parameter prompt from the command
                var param = cmd.substring(startIdx, endIdx + 1);
                // clean it up for display and prompt user for replacement
                var displayParam = param.replace(/'/g, '').replace(/"/g, '').replace('?', '');
                // save original prompt string and replacement
                params.push(param);
                // move index forward past this prompt to continue parsing command
                cmdIdx = endIdx;
                continue;
            }
            cmdIdx++;
        }
        return cmd;
    }
    
jQuery(function(){
    jQuery(document).on('change', 'select.f_loadCommanderCommand', loadCommanderCommand);
});
</script>

<div id="commanderPrompterConfirm" class="dn" data-title="<cti:msg key="yukon.common.device.commander.prompterDialog.title"/>">
    <label>
        <span>::REPLACED AT RUNTIME::</span>
        <input type="text"/>
    </label>
</div>
    