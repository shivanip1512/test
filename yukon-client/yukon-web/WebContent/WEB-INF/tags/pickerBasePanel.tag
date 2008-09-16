<%@ attribute name="pickerId" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div id="itemPicker_popup" class="titledContainer boxContainer">

    <div id="itemPicker_popup_titleBar" class="titleBar boxContainer_titleBar">
            
        <div class="controls" onclick="${pickerId}.cancel()">
            <img class="minMax" alt="close" src="/WebConfig/yukon/Icons/close_x.gif">
        </div>

        <div class="title boxContainer_title">
            <table class="itemPicker_titleTable">
                <tr>
                    <td class="query">
                        <label>Query:<input name="itemPicker_query" id="itemPicker_query" type="text" onkeyup="javascript:${pickerId}.doKeyUp(this);false;"></label>
                    </td>
                    <td class="prevNavArrow">
                        <div id="itemPicker_popup_titleBar_prevLink"></div>
                    </td>
                    <td class="pageNum">
                        <div id="itemPicker_popup_titleBar_pageNum"></div>
                    </td>
                    <td class="nextNavArrow">
                        <div id="itemPicker_popup_titleBar_nextLink"></div>
                    </td>
                </tr>
            </table>
        </div>
            
    </div>
    
    <div class="content boxContainer_content">

        <div id="itemPicker_results">
        </div>
        
        <%-- BUTTON CONTROLS (from pages/picker/inner.jsp or multiInner.jsp) --%>
        <jsp:doBody />
        
    </div>    
                
</div>