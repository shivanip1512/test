<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div style="background: white; border: 1px solid black;">
    <table>
        <tr>
            <td align="center" class="top">${paoName}</td>
            <td align="center" class='top' onclick='cClick()'><a href='javascript:void(0)'>X</a></td>
        </tr>
        <tr>
            <td colspan="2">
                <a href="javascript:void(0);" 
                   class="optDeselect"
                   onmouseover="changeOptionStyle(this);"
                   onclick="cClick(); execute_CapBankMoveBack('${paoId}','${cmdId}', '${redirectURL}');">${displayName}</a>
            </td>
        </tr>
    </table>
</div>            