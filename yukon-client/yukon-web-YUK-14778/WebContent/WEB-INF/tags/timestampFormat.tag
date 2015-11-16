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
            <td>yy;yyyy</td>
            <td><i:inline key=".year.result"/></td>
            <td><i:inline key=".year.description"/></td>
        </tr>
        <tr>
            <td>M;MM;MMM;MMMM</td>
            <td><i:inline key=".month.result"/></td>
            <td><i:inline key=".month.description"/></td>
        </tr>
        <tr>
            <td>dd</td>
            <td><i:inline key=".day.month.result"/></td>
            <td><i:inline key=".day.month.description"/></td>
        </tr>
        <tr>
            <td>EEE;EEEE</td>
            <td><i:inline key=".day.week.result"/></td>
            <td><i:inline key=".day.week.description"/></td>
        </tr>
        <tr>
            <td>HH</td>
            <td><i:inline key=".hour.24.result"/></td>
            <td><i:inline key=".hour.24.description"/></td>
        </tr>
        <tr>
            <td>h</td>
            <td><i:inline key=".hour.12.result"/></td>
            <td><i:inline key=".hour.12.description"/></td>
        </tr>
        <tr>
            <td>a</td>
            <td><i:inline key=".hour.ampm.result"/></td>
            <td><i:inline key=".hour.ampm.description"/></td>
        </tr>
        <tr>
            <td>mm</td>
            <td><i:inline key=".minute.result"/></td>
            <td><i:inline key=".minute.description"/></td>
        </tr>
        <tr>
            <td>ss</td>
            <td><i:inline key=".second.result"/></td>
            <td><i:inline key=".second.description"/></td>
        </tr>
        <tr>
            <td>SSS</td>
            <td><i:inline key=".millisecond.result"/></td>
            <td><i:inline key=".millisecond.description"/></td>
        </tr>
        <tr>
            <td>z; zzzz</td>
            <td><i:inline key=".timezone.name.result"/></td>
            <td><i:inline key=".timezone.name.description"/></td>
        </tr>
        <tr>
            <td>Z</td>
            <td><i:inline key=".timezone.offset.result"/></td>
            <td><i:inline key=".timezone.offset.description"/></td>
        </tr>
    </tbody>
</table>
</cti:msgScope>