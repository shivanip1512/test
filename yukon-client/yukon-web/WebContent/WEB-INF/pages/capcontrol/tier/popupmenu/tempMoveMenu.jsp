<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div style="background: white;">
    <input id="menuPaoName" type="hidden" value="${paoName}"/>
    <table>
        <tr>
            <td>
                <a href="javascript:void(0);" class="optDeselect" onmouseover="changeOptionStyle(this);"
                   onclick="closeTierPopup(); execute_CapBankMoveBack('${paoId}','${moveBackCmdId}', '${redirectURL}');">${moveBack}</a>
            </td>
        </tr>
        <tr>
            <td>
                <a href="javascript:void(0);" class="optDeselect" onmouseover="changeOptionStyle(this);"
                   onclick="closeTierPopup(); executeCommandController('${paoId}', '${assignHereCmdId}', 'Assign Bank Here', 'CAPBANK', false, ${opts}); setTimeout('window.location.reload()', 1000);">${assignHere}</a>
            </td>
        </tr>
    </table>
</div>            