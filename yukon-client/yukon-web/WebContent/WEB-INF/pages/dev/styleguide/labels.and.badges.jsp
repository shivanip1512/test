<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="labels.badges">
<tags:styleguide page="labels.badges">

<style>
.description { line-height: 22px; }
</style>

<p class="description">
    Yukon uses <a target="_blank" href="http://getbootstrap.com/components/#labels">bootstrap's</a> labels and badges. 
    They are useful for adding usablity through coloring text when appropriate.
</p>

<h2>Labels</h2>

<p class="description">
    Labels are useful for showing state or calling attention to something.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <p>
            <span class="label label-default">Default</span>
            <span class="label label-attr">Attribute</span>
            <span class="label label-primary">Primary</span>
            <span class="label label-success">Success</span>
            <span class="label label-info">Info</span>
            <span class="label label-warning">Warning</span>
            <span class="label label-danger">Danger</span>
        </p>
        <table class="compact-results-table">
            <thead>
                <tr><th>Task</th><th>Status</th></tr>
            </thead>
            <tr>
                <td>Job 42 - Delete all the things</td>
                <td><span class="label label-info">Pending</span></td>
            </tr>
            <tr>
                <td>Job 43 - Write all the codes</td>
                <td><span class="label label-success">Done</span></td>
            </tr>
            <tr>
                <td>Job 44 - Write all the documentation</td>
                <td><span class="label label-danger">Failed</span></td>
            </tr>
            <tr>
                <td>Job 44 - Fire the intern</td>
                <td><span class="label label-warning">Aborted</span></td>
            </tr>
        </table>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;p&gt;
    &lt;span class=&quot;label label-default&quot;&gt;Default&lt;/span&gt;
    &lt;span class=&quot;label label-attr&quot;&gt;Attribute&lt;/span&gt;
    &lt;span class=&quot;label label-primary&quot;&gt;Primary&lt;/span&gt;
    &lt;span class=&quot;label label-success&quot;&gt;Success&lt;/span&gt;
    &lt;span class=&quot;label label-info&quot;&gt;Info&lt;/span&gt;
    &lt;span class=&quot;label label-warning&quot;&gt;Warning&lt;/span&gt;
    &lt;span class=&quot;label label-danger&quot;&gt;Danger&lt;/span&gt;
&lt;/p&gt;
&lt;table class=&quot;compact-results-table&quot;&gt;
    &lt;thead&gt;
        &lt;tr&gt;&lt;th&gt;Task&lt;/th&gt;&lt;th&gt;Status&lt;/th&gt;&lt;/tr&gt;
    &lt;/thead&gt;
    &lt;tr&gt;
        &lt;td&gt;Job 42 - Delete all the things&lt;/td&gt;
        &lt;td&gt;&lt;span class=&quot;label label-info&quot;&gt;Pending&lt;/span&gt;&lt;/td&gt;
    &lt;/tr&gt;
    &lt;tr&gt;
        &lt;td&gt;Job 43 - Write all the codes&lt;/td&gt;
        &lt;td&gt;&lt;span class=&quot;label label-success&quot;&gt;Done&lt;/span&gt;&lt;/td&gt;
    &lt;/tr&gt;
    &lt;tr&gt;
        &lt;td&gt;Job 44 - Write all the documentation&lt;/td&gt;
        &lt;td&gt;&lt;span class=&quot;label label-danger&quot;&gt;Failed&lt;/span&gt;&lt;/td&gt;
    &lt;/tr&gt;
&lt;/table&gt;
</pre>

<h2>Badges</h2>

<p class="description">
    Badges are useful for showing counts of things.
</p>
<div class="column-4-20 clearfix style-guide-example">
    <div class="column one"><h4 class="subtle">Example:</h4></div>
    <div class="column two nogutter">
        <span class="badge">default</span>
        <span class="badge badge-error">error</span>
        <span class="badge badge-warning">warning</span>
        <span class="badge badge-success">success</span>
        <p><a href="#">Alerts <span class="badge">42</span></a></p>
        <p>Failed <span class="badge badge-error">42</span></p>
        <p>Partially Successful <span class="badge badge-warning">42</span></p>
        <p>Successful <span class="badge badge-success">42</span></p>
        <p><span class="badge">2342352</span> Unread Emails</p>
    </div>
</div>
<h4 class="subtle">Code:</h4>
<pre class="code prettyprint">
&lt;span class=&quot;badge&quot;&gt;default&lt;/span&gt;
&lt;span class=&quot;badge badge-error&quot;&gt;error&lt;/span&gt;
&lt;span class=&quot;badge badge-warning&quot;&gt;warning&lt;/span&gt;
&lt;span class=&quot;badge badge-success&quot;&gt;success&lt;/span&gt;
&lt;p&gt;&lt;a href=&quot;#&quot;&gt;Alerts &lt;span class=&quot;badge&quot;&gt;42&lt;/span&gt;&lt;/a&gt;&lt;/p&gt;
&lt;p&gt;Failed &lt;span class=&quot;badge badge-error&quot;&gt;42&lt;/span&gt;&lt;/p&gt;
&lt;p&gt;Partially Successful &lt;span class=&quot;badge badge-warning&quot;&gt;42&lt;/span&gt;&lt;/p&gt;
&lt;p&gt;Successful &lt;span class=&quot;badge badge-success&quot;&gt;42&lt;/span&gt;&lt;/p&gt;
&lt;p&gt;&lt;span class=&quot;badge&quot;&gt;2342352&lt;/span&gt; Unread Emails&lt;/p&gt;
</pre>

</tags:styleguide>
</cti:standardPage>