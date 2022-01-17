<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="timelines">
<tags:styleguide page="timelines">

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    Timeline components are used to visualize when a set of events occurred. They are built as a jquery widget using 
    jquery's <a href="https://jqueryui.com/widget/">widget factory</a>.
</p>

<h2>Simple Timeline with Some Random Events</h2>

<p class="description">
    Create a timeline component through the jquery timeline function added by the new timeline widget.
    Add the begin and end date and events to the timeline.   You can also optionally add showLabels, which will add labels in between the start/end on the timeline.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <div class="js-simple-timeline"></div>
        <script>
        (function () {
            var now = new Date();
            var twelveHoursAgo = new Date(now.getTime() - (1000 * 60 * 12 * 60));
            var tl = $('.js-simple-timeline');
            var i, timestamp, hours, stuff, event;
            var event_stuff = {
                0: { message: 'Earthquake!' },
                1: { message: 'Error!', icon: 'icon-exclamation', iconSvg: "<svg xmlns=\"http://www.w3.org/2000/svg\" fill=\"#da3227\" height=\"18\" viewBox=\"0 0 25 25\" width=\"18\"><path d=\"M0 0h24v24H0z\" fill=\"none\"/><path d=\"M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z\"/></svg>" },
                2: { message: 'Warning!', icon: 'icon-error', iconSvg: "<svg xmlns=\"http://www.w3.org/2000/svg\" fill=\"#f47721\" height=\"18\" viewBox=\"0 0 25 25\" width=\"18\"><path d=\"M0 0h24v24H0z\" fill=\"none\"/><path d=\"M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z\"/></svg>" },
                3: { message: 'Down!', icon: 'icon-arrow-down-green', iconSvg: "<svg xmlns=\"http://www.w3.org/2000/svg\" fill=\"#39b620\" height=\"18\" viewBox=\"0 0 25 25\" width=\"18\"><path d=\"M0 0h24v24H0V0z\" fill=\"none\"/><path d=\"M20 12l-1.41-1.41L13 16.17V4h-2v12.17l-5.58-5.59L4 12l8 8 8-8z\"/></svg>" }
            };
            
            var events = [];
            for (i = 0; i < 10; i++) {
                hours = yukon.random(1, 9);
                timestamp = new Date(now.getTime() - (1000 * 60 * hours * 60));
                stuff = event_stuff[yukon.randomInt(0, 3)];
                event = { id: i, timestamp: timestamp.getTime(), message: stuff.message };
                if (stuff.icon) event.icon = stuff.icon;
                if (stuff.iconSvg) event.iconSvg = stuff.iconSvg;
                events.push(event);
            }
            
            var options = {};
            options.end = now.getTime();
            options.begin = twelveHoursAgo.getTime();
            options.events = events;
            options.showLabels = true;
            
            tl.timeline(options);
        })();
        </script>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;div class=&quot;js-simple-timeline&quot;&gt;&lt;/div&gt;
&lt;script&gt;
(function () {
    var now = new Date();
    var twelveHoursAgo = new Date(now.getTime() - (1000 * 60 * 12 * 60));
    var tl = $('.js-simple-timeline');
    var i, timestamp, hours, stuff, event;
    var event_stuff = {
	    0: { message: 'Earthquake!' },
	    1: { message: 'Error!', icon: 'icon-exclamation', iconSvg: &quot;&lt;svg xmlns=\&quot;http://www.w3.org/2000/svg\&quot; fill=\&quot;#da3227\&quot; height=\&quot;18\&quot; viewBox=\&quot;0 0 25 25\&quot; width=\&quot;18\&quot;&gt;&lt;path d=\&quot;M0 0h24v24H0z\&quot; fill=\&quot;none\&quot;/&gt;&lt;path d=\&quot;M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z\&quot;/&gt;&lt;/svg&gt;&quot; },
	    2: { message: 'Warning!', icon: 'icon-error', iconSvg: &quot;&lt;svg xmlns=\&quot;http://www.w3.org/2000/svg\&quot; fill=\&quot;#f47721\&quot; height=\&quot;18\&quot; viewBox=\&quot;0 0 25 25\&quot; width=\&quot;18\&quot;&gt;&lt;path d=\&quot;M0 0h24v24H0z\&quot; fill=\&quot;none\&quot;/&gt;&lt;path d=\&quot;M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z\&quot;/&gt;&lt;/svg&gt;&quot; },
	    3: { message: 'Down!', icon: 'icon-arrow-down-green', iconSvg: &quot;&lt;svg xmlns=\&quot;http://www.w3.org/2000/svg\&quot; fill=\&quot;#39b620\&quot; height=\&quot;18\&quot; viewBox=\&quot;0 0 25 25\&quot; width=\&quot;18\&quot;&gt;&lt;path d=\&quot;M0 0h24v24H0V0z\&quot; fill=\&quot;none\&quot;/&gt;&lt;path d=\&quot;M20 12l-1.41-1.41L13 16.17V4h-2v12.17l-5.58-5.59L4 12l8 8 8-8z\&quot;/&gt;&lt;/svg&gt;&quot; }
    };
    
    var events = [];
    for (i = 0; i &lt; 10; i++) {
        hours = yukon.random(1, 9);
        timestamp = new Date(now.getTime() - (1000 * 60 * hours * 60));
        stuff = event_stuff[yukon.randomInt(0, 3)];
        event = { id: i, timestamp: timestamp.getTime(), message: stuff.message };
        if (stuff.icon) event.icon = stuff.icon;
        if (stuff.iconSvg) event.iconSvg = stuff.iconSvg;
        events.push(event);
    }
    
    var options = {};
    options.end = now.getTime();
    options.begin = twelveHoursAgo.getTime();
    options.events = events;
    options.showLabels = true;
    
    tl.timeline(options);
})();
&lt;/script&gt;
</pre>

</tags:styleguide>
</cti:standardPage>