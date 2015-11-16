<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:msgScope paths="modules.operator.controlHistory">
    <dr:controlHistorySummary displayableProgramList="${previousControlHistory}"
                              showControlHistorySummary="${true}"
                              past="true"
                              completeHistoryUrl="/stars/operator/program/controlHistory/completeHistoryView"/>
</cti:msgScope>