<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<div>
<table>
    <tr>
        <td style="color:#9FBBAC; font-weight: bold; font-size: 16;">kVAR</td>
        <td style="color:#9FBBAC; font-weight: bold; font-size: 16;">PhaseA</td>
        <td style="color:#9FBBAC; font-weight: bold; font-size: 16;">PhaseB</td>
        <td style="color:#9FBBAC; font-weight: bold; font-size: 16;">PhaseC</td>
        <td style="color:#9FBBAC; font-weight: bold; font-size: 16;">Total <a align="right" href="javascript:void(0);" style="color: gray; font-weight: bold; font-size: 16;" title="Click To Close" onclick="closePopupWindow();">X</a>
        </td>
    </tr>
    <tr>
        <td colspan='5'>
            <hr style="color: gray;"/>
        </td>
    </tr>
    <tr>${beforeRow}</tr>
    <tr>${afterRow}</tr>
    <tr>${changeRow}</tr>
</table>
</div>