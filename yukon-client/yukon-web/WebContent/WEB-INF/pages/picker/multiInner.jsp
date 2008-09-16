<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:pickerBasePanel pickerId="${pickerId}">

    <table class="itemPicker_buttonTable">
        <tr>
            <td class="left">
                <input type="hidden" id="sameParentItemLink" /> 
                <input type="button" value="Show All" onclick="${pickerId}.showAll()">
                <span id="itemPicker_indicator" style="display:none;"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></span>
            </td>
            <td class="right">
                <input type="button" value="${selectionLinkName}" onclick="${pickerId}.itemSelectionComplete()">
            </td>
        </tr>
    </table>
        
</tags:pickerBasePanel>