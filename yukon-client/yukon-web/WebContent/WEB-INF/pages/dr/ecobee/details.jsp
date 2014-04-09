<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="ecobee.details">
    
    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="queryStats">
                <div class="stacked">
                    <tags:nameValueContainer2 naturalWidth="false">
                        <c:forEach begin="1" end="12">
                            <tr>
                                <td class="name">April:</td>
                                <td class="value full-width">
                                    <div class="progress" style="width: 80px;float:left;">
                                        <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 27.0%"></div>
                                        <div class="progress-bar" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 50.0%"></div>
                                        <div class="progress-bar progress-bar-default" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 23.0%"></div>
                                    </div>
                                    <div class="fl" style="margin-left: 10px;" title="data collection / reports / reads">
                                        <span style="margin-right: 10px;width:48px;display: inline-block;">15200</span>
                                        <span class="label label-success">4104</span>
                                        <span class="label label-info">7600</span>
                                        <span class="label label-default">3496</span>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tags:nameValueContainer2>
                </div>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
        </div>
    </div>
    
</cti:standardPage>