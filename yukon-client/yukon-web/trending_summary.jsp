<jrun:servlet code="PeakDataGenerator">
<jrun:servletparam name="gdefid" value="<%= Integer.toString(graphDefinitionId) %>" />
<jrun:servletparam name="start" value="<%= dateFormat.format(start) %>" />
<jrun:servletparam name="page" value="<%= page %>" />
<jrun:servletparam name="end" value="<%= dateFormat.format(stop) %>" />
<jrun:servletparam name="db" value="<%= dbAlias %>" />
</jrun:servlet>

