
<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                    <tags:nameValue2 nameKey=".preparationLoadShaping">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${preparationLoadShaping}" path="fields.preparationLoadShaping" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.preparationLoadShaping}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2> 
                     <tags:nameValue2 nameKey=".peakLoadShaping">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${peakLoadShaping}" path="fields.peakLoadShaping" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.peakLoadShaping}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2> 
                    <tags:nameValue2 nameKey=".postPeakLoadShaping">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${postPeakLoadShaping}" path="fields.postPeakLoadShaping" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.postPeakLoadShaping}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>  
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>
