<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<style>
.labelGutter label {
    width: 8em;
    float: left;
    minHeight: 2em;
    text-align: right;
    padding-right: 5px;
}

.dateContainer {
    overflow: hidden;
    float: left;
    margin: 0 10px 10px 0;
}

.labelGutter small {
    font-size: 70%;
}

</style>

<p class="pageDescription">

</p>

<tags:sectionContainer title="HTML 5">
    <h3>Placeholder</h3>
    <input type="text" name="search" placeholder="Placeholder Text" >
</tags:sectionContainer>

<tags:sectionContainer title="Dates & Time" styleClass="labelGutter">

    <h3>Date Picker</h3>
    <div class="dateContainer"><label>Basic</label><dt:date value="${now}"/></div>
    <div class="dateContainer"><label>Basic - disabled </label><dt:date value="${now}" disabled="true"/></div>
    <div class="dateContainer"><label>Maximum Date<br><small>1 week from now</small></label><dt:date value="${now}" maxDate="${end}" /></div>
    <div class="dateContainer"><label>Minimum Date<br><small>1 week ago</small></label><dt:date value="${now}" minDate="${start}" /></div>
    <div class="dateContainer"><label>Limited Range<br><small>1 week ago -<br>1 week from now</small></label><dt:date value="${now}" minDate="${start}" maxDate="${end}" /></div>
    <div class="dateContainer"><label>Date Range</label><dt:dateRange startValue="${now}" endValue="${end}" /></div>

    <hr class="dashed">

    <h3>Time Picker</h3>
    <div class="dateContainer"><label>Basic</label><dt:time value="${now}" /></div>
    <div class="dateContainer"><label>Step Hour<br><small>Hour:3</small></label><dt:time value="${now}" stepHour="3"/></div>
    <div class="dateContainer"><label>Step Minute<br><small>Minutes:7</small></label><dt:time value="${now}" stepMinute="7"/></div>
    <div class="dateContainer"><label>Step Minute & Hour<br><small>Minutes:8 / Hour:4</small></label><dt:time value="${now}" stepMinute="8" stepHour="4"/></div>

    <hr class="dashed">

    <h3>Date & Time Picker</h3>
    <div class="dateContainer"><label>Basic</label><dt:dateTime value="${now}"/></div>
    <div class="dateContainer"><label>Maximum Date<br><small>1 week from now</small></label><dt:dateTime value="${now}" maxDate="${end}" /></div>
    <div class="dateContainer"><label>Minimum Date<br><small>1 week ago</small></label><dt:dateTime value="${now}" minDate="${start}" /></div>
    <div class="dateContainer"><label>Limited Range<br><small>1 week ago -<br>1 week from now</small></label><dt:dateTime value="${now}" minDate="${start}" maxDate="${end}" /></div>

</tags:sectionContainer>
