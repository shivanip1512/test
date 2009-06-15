<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div style="background: white;">
    <input id="menuPaoName" type="hidden" value="${paoName}"/>
    <table>
        <tr>
            <td>
                <a href="javascript:void(0);" class="optDeselect" onmouseover="changeOptionStyle(this);"
                   onclick="closeTierPopup(); execute_CapBankMoveBack('${paoId}','${cmdId}', '${redirectURL}');">${displayName}</a>
            </td>
        </tr>
    </table>
</div>            