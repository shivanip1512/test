<%@ tag body-content="empty" description="A table explaining timestamp formatting options." %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="classes" description="CSS class names applied to the table." %>

<style>
.timestamp-table td:nth-child(-n+2) {
    white-space: nowrap;
}
</style>

<cti:msgScope paths="yukon.common.timestamp.format">
<table class="timestamp-table ${pageScope.classes}">
    <thead>
        <tr>
            <th><i:inline key=".header.input"/></th>
            <th><i:inline key=".header.result"/></th>
            <th><i:inline key=".header.description"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <tr>
            <td><i:inline key=".year.input"/></td>
            <td><i:inline key=".year.result"/></td>
            <td><i:inline key=".year.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".month.input"/></td>
            <td><i:inline key=".month.result"/></td>
            <td><i:inline key=".month.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".day.month.input"/></td>
            <td><i:inline key=".day.month.result"/></td>
            <td><i:inline key=".day.month.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".day.week.input"/></td>
            <td><i:inline key=".day.week.result"/></td>
            <td><i:inline key=".day.week.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".hour.24.input"/></td>
            <td><i:inline key=".hour.24.result"/></td>
            <td><i:inline key=".hour.24.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".hour.12.input"/></td>
            <td><i:inline key=".hour.12.result"/></td>
            <td><i:inline key=".hour.12.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".hour.ampm.input"/></td>
            <td><i:inline key=".hour.ampm.result"/></td>
            <td><i:inline key=".hour.ampm.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".minute.input"/></td>
            <td><i:inline key=".minute.result"/></td>
            <td><i:inline key=".minute.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".second.input"/></td>
            <td><i:inline key=".second.result"/></td>
            <td><i:inline key=".second.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".millisecond.input"/></td>
            <td><i:inline key=".millisecond.result"/></td>
            <td><i:inline key=".millisecond.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".timezone.name.input"/></td>
            <td><i:inline key=".timezone.name.result"/></td>
            <td><i:inline key=".timezone.name.description"/></td>
        </tr>
        <tr>
            <td><i:inline key=".timezone.offset.input"/></td>
            <td><i:inline key=".timezone.offset.result"/></td>
            <td><i:inline key=".timezone.offset.description"/></td>
        </tr>
    </tbody>
</table>
</cti:msgScope>