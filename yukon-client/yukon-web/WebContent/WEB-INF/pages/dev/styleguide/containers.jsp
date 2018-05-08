<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="containers">
    <tags:styleguide page="containers">
    
        <style type="text/css">
        .description {line-height: 26px;margin-left:20px;}
        </style>
        
        <tags:sectionContainer title="Box Containers" styleClass="separated-sections">
            <div class="example section">
                <div class="clearfix">
                    <tags:boxContainer title="This is a Box Container">:D</tags:boxContainer>
                    <span class="description">a normal box container</span>
                </div>
                <pre class="code prettyprint">&lt;tags:boxContainer title=&quot;This is a Box Container&quot;&gt;:D&lt;/tags:boxContainer&gt;</pre>
            </div>
        </tags:sectionContainer>
        
        <tags:sectionContainer title="Section Containers">
            <div class="example section">
                <div class="clearfix">
                    <tags:sectionContainer title="This is a Section Container">:D</tags:sectionContainer>
                    <span class="description">a normal section container</span>
                </div>
                <pre class="code prettyprint">&lt;tags:sectionContainer title=&quot;This is a Section Container&quot;&gt;:D&lt;/tags:sectionContainer&gt;</pre>
            </div>
        </tags:sectionContainer>
        
        <tags:sectionContainer title="Sizable Scrolling container">
            <div class="y-resizable">
                <ul>
                    <c:forEach var="i" begin="1" end="30" step="1">
                        <li> ${i}. Item</li>
                    </c:forEach>
                </ul>
            </div>
            <div class="stacked">
                A scrolling container that allows resizing.<br>
                <span class="label label-default">Note:</span> Tables must be contianed inside an addition div for correct behavior.<br>
            </div>
        
            <pre class="code prettyprint">
        &lt;div class=&quot;y-resizable&quot;&gt;
            &lt;ul&gt;
                &lt;c:forEach var=&quot;i&quot; begin=&quot;1&quot; end=&quot;30&quot; step=&quot;1&quot;&gt;
                    &lt;li&gt; &#36;{i}. Item&lt;/li&gt;
                &lt;/c:forEach&gt;
            &lt;/ul&gt;
        &lt;/div&gt;</pre>
        </tags:sectionContainer>
        
        
        <tags:sectionContainer title="Tabs">
            <cti:tabs>
                <cti:tab title="First">First Content</cti:tab>
                <cti:tab title="Second">Second Content</cti:tab>
                <cti:tab title="Third">Third Content</cti:tab>
            </cti:tabs>
            <cti:tabs>
                <cti:tab title="First">First Content</cti:tab>
                <cti:tab title="Second" selected="true" >Second Content</cti:tab>
                <cti:tab title="Third">Third Content</cti:tab>
            </cti:tabs>
        
        
        </tags:sectionContainer>
        <tags:sectionContainer title="Containers Side by Side Test">
            <div class="column-6-6-6-6">
                <div class="column one">
                    <tags:boxContainer title="Box Container">box container contents</tags:boxContainer>
                </div>
                <div class="column two">
                    <script type="text/javascript">
                    $(function() {$('#tabs-test').tabs();});
                    </script>
                    <div id="tabs-test">
                        <ul>
                            <li><a href="#tab1">Tab 1</a></li>
                            <li><a href="#tab2">Tab 2</a></li>
                        </ul>
                        <div id="tab1" class="clearfix">tab 1 contents</div>
                        <div id="tab2" class="clearfix">tab 2 contents</div>
                    </div>
                </div>
                <div class="column three">
                    <cti:tabs>
                        <cti:tab title="Tab 1">tab 1 should start hidden</cti:tab>
                        <cti:tab title="Tab 2" selected="true">tab 2 should be active first</cti:tab>
                    </cti:tabs>
                </div>
                <div class="column four nogutter">
                    <tags:sectionContainer title="Section Container">section container contents</tags:sectionContainer>
                </div>
            </div>
        </tags:sectionContainer>
        
        <tags:sectionContainer title="Accordion">
            An accordion view is useful when there are multiple steps to a process. This provides a stepped view without needing to move back and forth between different pages.
            It can also be used to organize content under multiple headers. We use the <a href="https://jqueryui.com/accordion">jQuery UI Accordion widget</a><br/><br/>
            <div id="sampleAccordion">
                <h3>First Header</h3>
                <div>
                    Content for First Header
                </div>
                <h3>Second Header</h3>
                <div>
                    Content for Second Header
                </div>
                <h3>Third Header</h3>
                <div>
                    Content for Third Header
                </div>
            </div>
            
            <script>
                $('#sampleAccordion').accordion({
                    heightStyle: "content",
                    collapsible: true
                });
            </script><br/><br/>
            Code Example:
            <pre class="code prettyprint">
&lt;div id="sampleAccordion"&gt;
    &lt;h3&gt;First Header&lt;/h3&gt;
    &lt;div&gt;
        Content for First Header
    &lt;/div&gt;
    &lt;h3>Second Header&lt;/h3&gt;
    &lt;div&gt;
        Content for Second Header
    &lt;/div&gt;
    &lt;h3&gt;Third Header&lt;/h3&gt;
    &lt;div&gt;
        Content for Third Header
    &lt;/div&gt;
&lt;/div&gt;

&lt;script&gt;
    $('#sampleAccordion').accordion({
        heightStyle: "content",
        collapsible: true
    });
&lt;/script&gt;
            </pre>
        </tags:sectionContainer>
    
    </tags:styleguide>
</cti:standardPage>