<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>

<cti:msgScope paths="modules.tools.tdc">
    <flot:trend title="${title}" pointIds="${pointId}" startDate="${startDateMillis}" endDate="${endDateMillis}" interval="${interval}" converterType="${converterType}" graphType="${graphType}" ymin="0"/>
    <div class="action-area">
        <cti:button nameKey="close" onclick="$('#tdc-popup').dialog('close');" />
    </div>
</cti:msgScope>