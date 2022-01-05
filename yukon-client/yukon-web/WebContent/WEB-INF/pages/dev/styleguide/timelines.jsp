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
                1: { message: 'Legos!', icon: 'icon-brick' },
                2: { message: 'OMG A SPORTS CAR!', icon: 'icon-vehicle-sports-car' },
                3: { message: 'Happy Birthday!', icon: 'icon-cake' }
            };
            
            var events = [];
            for (i = 0; i < 10; i++) {
                hours = yukon.random(1, 9);
                timestamp = new Date(now.getTime() - (1000 * 60 * hours * 60));
                stuff = event_stuff[yukon.randomInt(0, 3)];
                event = { id: i, timestamp: timestamp.getTime(), message: stuff.message };
                if (stuff.icon) event.icon = stuff.icon;
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
        1: { message: 'Legos!', icon: 'icon-brick' },
        2: { message: 'OMG A SPORTS CAR!', icon: 'icon-vehicle-sports-car' },
        3: { message: 'Happy Birthday!', icon: 'icon-cake' }
    };
    
    var events = [];
    for (i = 0; i &lt; 10; i++) {
        hours = yukon.random(1, 9);
        timestamp = new Date(now.getTime() - (1000 * 60 * hours * 60));
        stuff = event_stuff[yukon.randomInt(0, 3)];
        event = { id: i, timestamp: timestamp.getTime(), message: stuff.message };
        if (stuff.icon) event.icon = stuff.icon;
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