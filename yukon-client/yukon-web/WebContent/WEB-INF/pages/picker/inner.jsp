<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:pickerBasePanel pickerId="${pickerId}">

    <table class="itemPicker_buttonTable">
        <tr>
            <td class="left">
                <input type="button" value="Show All" onclick="${pickerId}.showAll()">
                <span id="itemPicker_indicator" style="display:none;"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></span>
            </td>
            
            <c:choose>
                <c:when test="${not empty sameItemLink}">
                    <td class="right">
                        <input id="sameParentItemLink" type="button" value="${sameItemLink}" onclick="${pickerId}.sameParentItem()">
                    </td>
                </c:when>
                <c:otherwise>
                    <input type="hidden" id="sameParentItemLink" /> 
                </c:otherwise>
            </c:choose>
        </tr>
    </table>
        
</tags:pickerBasePanel>