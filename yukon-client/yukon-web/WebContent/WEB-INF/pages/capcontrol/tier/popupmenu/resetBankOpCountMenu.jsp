<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="yukon.web.modules.capcontrol.menu">
    <div id="menuPopupBoxContainer">
		<input type="hidden" id="dialogTitle" value="${title}">        
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".resetOpCountTo">
                <input id="newOpCount" type="text" value="0" maxlength="4" size="4">
            </tags:nameValue2>
        </tags:nameValueContainer2>
        <div class="actionArea">
            <cti:button nameKey="reset" onclick="doResetBankOpCount(${bankId}, $F('newOpCount'))"/>
        </div>
    </div>
</cti:msgScope>