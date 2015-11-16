<%-- This tag was created to i18n the dialog created in the processCommanderReplacement function --%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script>
    var params = [];
    
    function loadCommanderCommand (event) {
        var selectElement = $(event.currentTarget),
            originalCmd = selectElement.val();
        // init params for this command
        params = [];
        // parse for parameters
        getCommanderParams(originalCmd);
        // replace any escaped question marks (double ?) with single ?
        originalCmd = originalCmd.replace('??', '?');
        // kick off replacements
        processCommanderReplacement(0, originalCmd, originalCmd, selectElement.data("cmdfield"));
    }
    
    // prompt for a replacement for the parameter
    // replacements will stop when no more parameters exist
    function processCommanderReplacement (paramIdx, originalCmd, cmd, cmdField) {
        var param = params[paramIdx],
            windowHeight = $(window).height(),
            position = {my: 'top', at: 'top+' + windowHeight/5 },
            displayParam;
        if (typeof param !== 'undefined') {
            displayParam = param.replace(/'/g, '').replace(/"/g, '').replace('?', '');
            $("#commanderPrompterConfirm label>span").text(displayParam);
            $("#commanderPrompterConfirm").dialog({
                resizable: false,
                modal: true,
                title: $("#commanderPrompterConfirm").data('title'),
                position : position,
                buttons: {
                    "<cti:msg key="yukon.web.components.dialog.ok"/>": function () {
                        $( this ).dialog( "close" );
                        replacement = $( this ).find("input:text").val();
                        cmd = cmd.replace(param, replacement);
                        $(document.getElementById(cmdField)).val(cmd);
                        // try next replacements
                        processCommanderReplacement(paramIdx + 1, originalCmd, cmd, cmdField);
                    },
                    "<cti:msg key="yukon.web.components.dialog.cancel"/>": function () {
                        $( this ).find("input:text").val("");
                        $( this ).dialog( "close" );
                        //the EXT version of this tried to populate an undefined field.  Pretty sure this
                        //is what was meant, but I leave the following in as this makes more sense to me.
                        //$(document.getElementById(cmdField)).val(originalCmd);
                    }
                }
            });
        }
        else {
            // no parameter on first attempt, set command value as-is
            if (paramIdx === 0) {
                $('#' + cmdField).val(originalCmd);
            }
        }
    }
    
    function getCommanderParams (cmd) {
        var cmdIdx = 0,
            startIdx,
            endIdx,
            j,
            param,
            displayParam;
        while(cmdIdx < cmd.length) {
            // search for ?' or ?" to that indicates the beginning of a prompt replacement
            if (cmd.charAt(cmdIdx) === '?') {
                startIdx = cmdIdx;
                endIdx = null;
                if (cmd.charAt(cmdIdx + 1) === '?') {
                    cmdIdx = cmdIdx + 2;
                    continue;
                }
                // quoted prompt string if quote immediately follows ?
                if (cmd.charAt(cmdIdx + 1) === "'" || cmd.charAt(cmdIdx + 1) === '"') {
                    for (j = cmdIdx + 2; j < cmd.length; j += 1) {
                        if (cmd.charAt(j) === "'" || cmd.charAt(j) === '"') {
                            endIdx = j;
                            break;    
                        }
                    }
                }
                // unquoted prompt string
                // ends at space or quote that is to be preserved, or eol
                else {
                    // Skip over any spaces at the beginning of the prompt
                    j = cmdIdx + 1;
                    for (j = cmdIdx + 1; j < cmd.length; j += 1) {
                        if (cmd.charAt(j) !== ' ') {
                            break;
                        }
                    }
                    for (j; j < cmd.length; j += 1) {
                        if (cmd.charAt(j) === ' ' || cmd.charAt(j) === "'" || cmd.charAt(j) === '"') {
                            endIdx = j - 1;
                            break;    
                        }
                    }
                    if (endIdx === null) {
                        endIdx = cmd.length;
                    }
                }
                // pull the parameter prompt from the command
                param = cmd.substring(startIdx, endIdx + 1);
                // clean it up for display and prompt user for replacement
                displayParam = param.replace(/'/g, '').replace(/"/g, '').replace('?', '');
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
    
$(function(){
    $(document).on('change', 'select.js-loadCommanderCommand', loadCommanderCommand);
});
</script>

<div id="commanderPrompterConfirm" class="dn" data-title="<cti:msg key="yukon.common.device.commander.prompterDialog.title"/>">
    <label>
        <span>::REPLACED AT RUNTIME::</span>
        <input type="text"/>
    </label>
</div>
    