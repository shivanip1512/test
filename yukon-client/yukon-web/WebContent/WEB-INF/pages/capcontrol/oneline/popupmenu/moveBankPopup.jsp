<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<div>
    <table>
        <tr>
            <td align="right" style="color:#9FBBAC; font-weight: bold; font-size: 16;">Temp Move Menu</td>
            <td align="right">
                <a href="javascript:void(0);" style="color: gray; font-weight: bold; font-size: 16;" title="Click To Close" onclick="closePopupWindow();">x</a>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <hr style="color: gray;"/>
                <a href="javascript:void(0);" style="color: white;" onclick="showMoveBankPage(${paoId})">Move Bank</a>
            </td>
        </tr>

    </table>
</div>    
