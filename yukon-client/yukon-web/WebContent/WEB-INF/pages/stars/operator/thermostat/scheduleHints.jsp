<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

    <h3>
        <cti:msg key="yukon.dr.operator.scheduleHints.header" /><br>
    </h3>
    
    <div class="message">
        <div style="text-align: left;">
            <cti:msg key="yukon.dr.operator.scheduleHints.hint" />
        </div>
        
        <a href="${backUrl}">
            <cti:msg key="yukon.dr.operator.scheduleHints.back" />
        </a>
    </div>
