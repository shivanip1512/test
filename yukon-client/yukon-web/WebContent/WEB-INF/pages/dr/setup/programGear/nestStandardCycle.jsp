
<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                    <tags:nameValue2 nameKey=".preparationLoadShaping">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${preparationLoadShaping}" path="fields.prep" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.prep}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2> 
                     <tags:nameValue2 nameKey=".peakLoadShaping">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${peakLoadShaping}" path="fields.peak" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.peak}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2> 
                    <tags:nameValue2 nameKey=".postPeakLoadShaping">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${postPeakLoadShaping}" path="fields.post" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.post}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>  
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>
