import groovy.io.GroovyPrintStream;

// Running from commandline inside the commons project, and these are correct.
String commonsBaseDir = "../../../";
String outputDir = "./";
// OR Specify these in order to run the script from anywhere - as with groovyConsole
//String commonsBaseDir = "C:/Users/MB8438/Desktop/Yukon6_sstsWorkspace20130916_restart/common/";
//String outputDir = commonsBaseDir +"dev/scripts/pointAnalysis/";

String input_rfn = commonsBaseDir +"src/com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml"
String input_pao = commonsBaseDir +"src/com/cannontech/common/pao/definition/dao/paoDefinition.xml"


boolean COMPARE_OTHER_VERSIONS_OF_INPUTS = false;  // requires paoInputFile47957, etc.
boolean COMPARE_PAO_ATTRIBUTES = false;            // requires the above is run first - WARNING: Also has own list of model symbols NEEDS UPDATES

def paoInputFile   = new File(input_pao)
def paoOutputFile  = new File(outputDir +"paoAnalysis.txt")

def rfnInputFile   = new File(input_rfn)
def rfnOutputFile   = new File(outputDir +"rfnAnalysis.txt")

// after rfn vs pao, now compare against the original XML file
/*
String oldPaoFilesBase = "/Yukon/5.6/paoFiles/"
def paoInputFile47957   = new File(oldPaoFilesBase +"/paoDefinition/paoDefinition_v47957.xml")
def rfnInputFile46349   = new File(oldPaoFilesBase +"/rfnPointMapping/rfnPointMapping_v46349.xml")
String analysisOutputBase = "/paoAnalysis/"
def paoOutputFile47957  = new File(analysisOutputBase +"/paoAnalysis_newest_vs_v47957.txt")
def rfnOutputFile46349  = new File(analysisOutputBase +"/rfnAnalysis_newest_vs_v46349.txt")
*/

def paoVrfnOutputFile = new File(outputDir +"paoVSrfnAnalysis.txt")
def paoVrfnOutputFile_ORIGINAL = new File(outputDir +"paoVSrfnAnalysis_ORIGINAL.txt")

def _printPaosWhileReading          = false
def _printInheritanceWhileReading   = false
boolean _skipEvents                 = true        // skips everything (eg. events) except... analog points.




// NEW 20130925W pm.
def __MODIFIERS_TO_ABBRV = ['Quadrant 1':'Q1', 'Quadrant 2':'Q2', 'Quadrant 3':'Q3', 'Quadrant 4':'Q4',
                            'TOU Rate A':'Ra', 'TOU Rate B':'Rb', 'TOU Rate C':'Rc', 'TOU Rate D':'Rd', 'TOU Rate E':'Re',
                            'Phase A':'Pa',    'Phase B':'Pb',    'Phase C':'Pc',    'Primary':'1^',    'Secondary':'2^',
                            'Avg':'Av',        'Max':'mx',        'Min':'mn',        'Net Flow':'Nf',   'Cumulative':'Cu', 'Harmonic':'Hc']
def __UOM_TO_ABBRV = ['Counts':'#', 'Seconds':'s', 'Volts':'V', 'V degree':'Vdgr', 'A degree':'Adgr', 'PF degree':'PFdg',
                        'Outage Count':'Out#', 'Restore Count':'Rst#', 'Outage Blink Count':'OBk#', 'Restore Blink Count':'RBk#', 'Outage Log':'Olog']


// TODO: change the mapping outputs to multiple columns so this listing is shorter

String OUTPUT_KEY = """::Yukon Analysis::
paoDefinition.xml vs rfnPointMapping.xml
    offset: the offset declared in paoDefinition.xml for the given point
    Model Symbols:
        CL2    RFN420CL  (Centron)          CD2    RFN420CD  (Centron)
        FL2    RFN420FL  (Focus)            A3D    RFN430A3D (Elster)
        FX2    RFN420FX  (Focus)            A3T    RFN430A3T (Elster)
        FD2    RFN420FD  (Focus)            A3K    RFN430A3K (Elster)
        FL1    RFN410FL  (Focus)            A3R    RFN430A3R (Elster)
        FX1    RFN410FX  (Focus)            KV     RFN430KV  (Elster)
        FD1    RFN410FD  (Focus)
        FRX    RFN420FRX
        FRD    RFN420FRD
        21T    RFN440_2131T                 21D    RFN440_2131TD
        22T    RFN440_2132T                 22D    RFN440_2132TD
        23T    RFN440_2133T                 23D    RFN440_2133TD
        SL0    RFN430SL0                    SL1    RFN430SL1
        SL2    RFN430SL2                    SL3    RFN430SL3
        SL4    RFN430SL4

    UoM : Unit of Measure Abbreviations
"""+
    __MODIFIERS_TO_ABBRV.collect{ key,abbr -> "        "+ abbr +" = "+ key }.join("\n") +
""" 
   
    Modifiers' Abbreviations
"""+
   __UOM_TO_ABBRV.collect{ key,abbr -> "        "+ abbr +" = "+ key }.join("\n") +
"""

    Symbols appearing after model #:
        >        left file has it, but right doesn't, eg. PAO > RFN
        <        right file has it, but left doesn't, eg. PAO < RFN
        a        an attribute matches the point's name, but the point is defined for that model in the XML while the attribute is NOT
        !        an attribute matches the point's name, but the attribute is defined for that model while the point is NOT

DATE: 2013-09-25-W


"""





// Define constants so they can be easily used later.
def XML         = "xml"
def CHILDREN    = "children"

def POINTS      = "points"
def MODEL_IDS   = "model ids"
def PAO_IDS     = "pao ids"
def EVENTS      = "events"
def FILE        = "file"
def TAGS        = "tags"
def TYPE        = "type"
def ATTRIBUTES  = "attributes"
def ATTRIBUTES_BY_POINT  = "attributes by point"
// RFN file collections - should verify and correlate between PAO and RFN:
def TOPLEVEL    = "toplevel"
def PT_TO_TYPES = "pointToTypes"    // WAS: _pointToTypes
def PAO_TO_PTNAMES = 'pao names-to-point names'    // WAS: _paoTypes



/**
 *  Here we start defining all model numbers and their abbreviations.
 *  These are required for a few reasons:
        * map between them so abbreviations can be listed in the output files
        * read the constants from both paoDefinition.xml and rfnPointMapping.xml
        * so this script knows which models to report on - otherwise it would save ALL models, etc., from paoDefinition.xml
        
 *  HOWEVER this can cause problems for comparison scripts because you need to setup which models are in each,
 *  and how they map (if they do) between versions.
 */
String[] _modelIds
String[] _modelCols

String _modelIds_Centron = "RFN420CL RFN420CD"
String _modelCols_Centron = "CL2 CD2"
String _modelIds_Focus1 = "RFN420FL RFN420FX RFN420FD RFN410FL RFN410FX RFN410FD"
String _modelCols_Focus1 = "FL2 FX2 FD2 FL1 FX1 FD1"
// Added 2 ~ 2013Q2
String _modelIds_Focus2 = "RFN420FL RFN420FX RFN420FD RFN410FL RFN410FX RFN410FD RFN420FRX RFN420FRD"
String _modelCols_Focus2 = "FL2 FX2 FD2 FL1 FX1 FD1 FRX FRD"

String _modelIds_Elster1 = "RFN430A3"
String _modelCols_Elster1 = "A3" // +KV
// Converted "A3" into 4 types, 2013Mar
String _modelIds_Elster2 = "RFN430A3R RFN430KV RFN430A3K RFN430A3T RFN430A3D"
String _modelCols_Elster2 = "A3R KV A3K A3T A3D"

// Added 6 ~ 20130717W
String _modelIds_ELO = "RFN440_2131T RFN440_2131TD RFN440_2132T RFN440_2132TD RFN440_2133T RFN440_2133TD"
String _modelCols_ELO = "21T 21D 22T 22D 23T 23D"

// Adding Itron Sentinel meters
String _modelIds_Sentinel = "RFN430SL0 RFN430SL1 RFN430SL2 RFN430SL3 RFN430SL4"
String _modelCols_Sentinel = "SL0 SL1 SL2 SL3 SL4"

/** ORIG vs NEW for comparison script **/
//String[] _modelIds_ORIG  = "RFN430A3 RFN430KV RFN420CL RFN420CD RFN420FL RFN420FX RFN420FD RFN410FL RFN410FX RFN410FD".split(" ")
//String[] _modelCols_ORIG = "A3 KV CL2 CD2 FL2 FX2 FD2 FL1 FX1 FD1".split(" ")

//String[] _modelIds_NEW   = "RFN430A3R RFN430KV RFN430A3K RFN430A3T RFN430A3D RFN420CL RFN420CD RFN420FL RFN420FX RFN420FD RFN410FL RFN410FX RFN410FD".split(" ")
//String[] _modelCols_NEW  = "A3R KV A3K A3T A3D CL2 CD2 FL2 FX2 FD2 FL1 FX1 FD1".split(" ")
//String[] _modelIds_NEW   = (_modelIds_Elster2 +" "+ _modelIds_Centron +" "+ _modelIds_Focus2).split(" ")
//String[] _modelCols_NEW  = (_modelCols_Elster2 +" "+ _modelCols_Centron +" "+ _modelCols_Focus2).split(" ")
String[] _modelIds_NEW   = (_modelIds_Elster2 +" "+ _modelIds_Centron +" "+ _modelIds_Focus2 +" "+ _modelIds_ELO +" "+ _modelIds_Sentinel).split(" ")
String[] _modelCols_NEW  = (_modelCols_Elster2 +" "+ _modelCols_Centron +" "+ _modelCols_Focus2 +" "+ _modelCols_ELO +" "+ _modelCols_Sentinel).split(" ")

//String[] _modelIds_COMBO = "RFN430A3R RFN430KV RFN430A3K RFN430A3T RFN430A3D RFN430A3 RFN420CL RFN420CD RFN420FL RFN420FX RFN420FD RFN410FL RFN410FX RFN410FD".split(" ")
//String[] _modelCols_COMBO= "A3R KV A3K A3T A3D A3 CL2 CD2 FL2 FX2 FD2 FL1 FX1 FD1".split(" ")
String[] _modelIds_COMBO = (_modelIds_Elster2 +" "+ _modelIds_Elster1 +" "+ _modelIds_Centron +" "+ _modelIds_Focus2 +" "+ _modelIds_ELO +" "+ _modelIds_Sentinel).split(" ")
String[] _modelCols_COMBO= (_modelCols_Elster2 +" "+ _modelCols_Elster1 +" "+ _modelCols_Centron +" "+ _modelCols_Focus2 +" "+ _modelCols_ELO +" "+ _modelCols_Sentinel).split(" ")

_modelIds  = _modelIds_NEW
_modelCols = _modelCols_NEW

//def _pao = [:]    // id -> [:]  >> xml -> XML object, children -> [strings]
//def _toplevel = []

String MODELS  = "models"
// These are used for PAOs
//def _points     = [:]    // id -> [xml:node, models:[ids]]
//def _tags       = [:]    // id -> [xml:node, models:[ids]]
//def _attributes = [:]    // id -> [xml:node, models:[ids]]





/**
    This is how this datastructure is used.  This could be formalized by making it into a class/es:

     __data[fileId][:]
                 TOPLEVEL-> []        // Top-level
                 TYPE    -> Type.X
                 modelId  -> [xml:Node, children:[...]
                 POINTS  -> [:]        point name -> [xml:node, models:[modelId,...]]
                 PT_TO_TYPES -> [:]    point name -> []
*/
def __data = [:]



/** Start a data structure to remember each file as used, and initialize that structure **/
enum Type {RFN, PAO}
def typeCounter = [(Type.RFN):0, (Type.PAO):0]
//def addFile = { File input, Type type ->
def addFile = { input, type ->
    if( !(input instanceof File ))    throw new IllegalArgumentException( "Got first parameter: not a File!" )    //  $input"
    if( !(type instanceof Type ))     throw new IllegalArgumentException( "Got 2nd parameter: not a Type!" )      //    TYPE"
    String id = "${type}_${++typeCounter[type]}"
    __data[id] = [(TYPE):type, (FILE):input, (POINTS):[:], (EVENTS):[:], (PAO_IDS):[], (MODEL_IDS):[],
        (TAGS):[:], (ATTRIBUTES):[:], (ATTRIBUTES_BY_POINT):[:], (TOPLEVEL):[], (PT_TO_TYPES):[:], (PAO_TO_PTNAMES):[:]]
    id
}



/***** readPaoFile() *****/
def readPaoFile = { xmlObjects, fileId ->        // paoDefinitions
    def root = __data[fileId]
    xmlObjects.pao.each { pp ->
        if(_printInheritanceWhileReading)    println "${pp.attribute('id')}\t\t->${pp.attribute('inherits')}"
        def modelId = pp.attribute('id')
        def inherits = pp.attribute('inherits')
        if( root[modelId] == null )                // Save the PAO's data
            root[modelId] = [:]
        root[modelId][XML] = pp
        if( root[modelId][CHILDREN] == null )
            root[modelId][CHILDREN] = []
        if(inherits == null) {
            root[TOPLEVEL] << modelId
            if(_printPaosWhileReading) println "${id}\t\t-> N/A"
        } else {    // assign to parents
            def suprs = inherits.split(', ')
             if(_printPaosWhileReading) println "${id}\t\t-> ${suprs}"
            
            suprs.each{ supr ->
                supr = supr.trim()
                if( root[supr] == null )
                    root[supr] = [:]
                if( root[supr][CHILDREN] == null )
                    root[supr][CHILDREN] = []
                root[supr][CHILDREN] << modelId
            }
        }
    }
}


/******* extractPaoData *******/
def createOrAddModel = { node, intoCollection, typeName, containerId, modelId ->
        def name = node.attribute('name')
        if( name == null || name.length() <= 0 ) throw new IllegalStateException( "blank name for TYPEName [$it] within ID=[$containerId]" )
        if( intoCollection[name] == null )
            intoCollection[name] = [XML:node, (MODELS):[modelId]]           // Initial definition
        else
            intoCollection[name][MODELS] << modelId                     // add the new model
        // TODO: potentially validate the definitions are the same!  >>>> most worth while between file versions!!
}

def createOrAddModelFromSubElement = { node, intoCollection, typeName, containerId, modelId, attrName ->
    def ptName = node.basicLookup[0].attribute(attrName)
    if( ptName == null || ptName.length() <= 0 ) throw new IllegalStateException( "blank name for TYPEName [$it] within ID=[$containerId]" )
    if( intoCollection[ptName] == null )
        intoCollection[ptName] = [XML:node, (MODELS):[modelId]]           // Initial definition
    else
        intoCollection[ptName][MODELS] << modelId                     // add the new model
    // TODO: potentially validate the definitions are the same!  >>>> most worth while between file versions!!
}

def addTagToPao = { node, intoCollection, containerId ->
    def name = node.attribute('name')
    if( name == null || name.length() <= 0 ) throw new IllegalStateException( "blank name for TYPEName [$it] within ID=[$containerId]" )
    if( intoCollection[containerId][TAGS] == null )
        intoCollection[containerId][TAGS] = []
    else
        intoCollection[containerId][TAGS] << name
}



def extractPaoData
extractPaoData = { fileId, paoTagIterId, modelId ->    // fileId, id, modelId - doesn't seem accurate... paoTagId?
    paoTagIterId = paoTagIterId.trim()
    def root = __data[fileId]
    
    def model = root[paoTagIterId]
    if( model == null ) {
        println( "[$paoTagIterId] has no model @ $fileId" )
        return
    }
    
    def pts = model.xml.points.point
    def atts= model.xml.attributes.attribute
    def tags= model.xml.tags.tag
    pts.each{ 
        def storedPts = root[POINTS]
        if( _skipEvents &&  it?.attribute('type') != 'Analog' ) {    // SAVE ANALOGs as Points, others as Events
            storedPts = root[EVENTS]
        }
        def name = it?.name[0]?.value()[0]
        if( name == null || name.length() <= 0 ) throw new IllegalStateException( "blank name for point [$it] within ID=[$paoTagIterId]" )
        if( storedPts[name] == null )
            storedPts[name] = [xml:it, (MODELS):[modelId]]           // Initial definition
        else
            storedPts[name][MODELS] << modelId                     // add the new model
        // TODO: potentially validate the definitions are the same!  >>>> most worth while between file versions!!
    }
    atts.each{ 
        createOrAddModel(it, root[ATTRIBUTES], 'attribute', paoTagIterId, modelId)    // Does the generic collection have much of a point anymore?
        createOrAddModelFromSubElement(it, root[ATTRIBUTES_BY_POINT], 'attribute', paoTagIterId, modelId, 'point')
    }
    tags.each{
        createOrAddModel(it, root[TAGS], 'tags', paoTagIterId, modelId)        // still useful?
        addTagToPao(it, root, paoTagIterId)
    }
    
    String[] inherits = model.xml.attribute('inherits')?.split(",")
    inherits.each{ id2 ->
        extractPaoData(fileId, id2, modelId)
    }
}


/*************   printPao  ************/
//String PAO_HEADER_FULL    = '%48s  offset     <<<<models>>>>    offset     <<<<models>>>>     offset  ** Point Name **\n'
String PAO_HEADER_FULL    = '* UoM| Modifiers  | %48s offset             <<<<models>>>>%103soffset\n'
String PAO_HEADER_MIN    = '%48s  offset             <<<<models>>>>\n'
String PAO_HEADER        = PAO_HEADER_MIN

//String ROW_PREFIX_FULL    = '%48s  |%03d| '
// Adding UoM and Modifiers fields...: 5 chars for UoM (kVArh) and 12 for Modifiers (6 displayable)
//String ROW_PREFIX_FULL   = '%48s  |%03d|%5s|%12s| '
String ROW_PREFIX_FULL   = '%5s|%12s| %48s |%03d| '
String ROW_PREFIX        = ROW_PREFIX_FULL
String PAO_ROW_PREFIX    = '%48s  |%03d| '            // NEW

String ROW_SUFFIX_FULL   = '  |%03d|\n'        // Meant to show the offset and maybe name again at the far-right to give wide-scrolling screens more context.
String ROW_SUFFIX_MIN    = '\n'
//String ROW_SUFFIX        = ROW_SUFFIX_MIN
String ROW_SUFFIX        = ROW_SUFFIX_FULL

boolean SHOW_MIDLINE_OFFSET = false


def printPao = { fileId, localOutFile ->
    def localOut            = new GroovyPrintStream(localOutFile)
    localOut.printf PAO_HEADER, "** Point Name **", ' '
    
    def paoPoints    = __data[fileId][POINTS]
    def ptKeys       = paoPoints.keySet()    //.sort()
    ptKeys.eachWithIndex{ ptName, iPt -> 
        def paoMap = paoPoints[ptName]
        int ii = Integer.parseInt(paoMap[XML].attribute('offset'))
        def str = PAO_ROW_PREFIX
        _modelCols.eachWithIndex{ mid, index -> 
            str += ' '
            if( paoMap[MODELS].contains(_modelIds[index]))
                str += mid
            else
                str += ' '* mid.length()
            if( SHOW_MIDLINE_OFFSET && index == 5 )
                str += ' |%03d|'
        }
        str += ROW_SUFFIX
        if( iPt != 0 && (iPt % 3) == 0 )
            localOut.println ""
        localOut.printf(str, [ptName, ii, ii, ii, ptName])
    }
    localOut.printf PAO_HEADER, "** Point Name **", ' '
}


/*************   printRfn  ************/
String RFN_FORMAT_1 = '%48s  '    // '%48s  |%03d| '
String RFN_FORMAT_2 = '\n'        // '  |%03d|  %s\n'
String RFN_HEADER_FULL = '%48s   idx       <<<<models>>>>     idx       <<<<models>>>>       idx   ** Point Name **\n'
String RFN_HEADER_MIN  = '%48s        <<<<models>>>>\n'
boolean showMidlineIndex = false

String RFN_HEADER_PRINT = RFN_HEADER_MIN

def printRfn = { fileId, outputFile ->
    def localOut            = new GroovyPrintStream(outputFile)
    localOut.printf RFN_HEADER_PRINT, "** Point Name **"
    
    def points       = __data[fileId][POINTS]
    def keys         = points.keySet().sort()
    keys.eachWithIndex{ k, iPt -> 
        def v = points[k]
        int ii = iPt
        def str = RFN_FORMAT_1
        _modelCols.eachWithIndex{ mid, index -> 
            str += ' '
            if( v[MODELS].contains(_modelIds[index]))
                str += mid
            else
                str += ' '* mid.length()
            if( showMidlineIndex && index == 5 )
                str += ' |%03d|'
        }
        str += RFN_FORMAT_2
        if( ii % 3 == 0 )
            localOut.println ""
        localOut.printf(str, [k])
    }
    localOut.printf RFN_HEADER_PRINT, "** Point Name **"
}


/*********  CURRENTLY UNUSED  ************/
def recurseChildrenToPrintPOINTHierarchy
recurseChildrenToPrintPOINTHierarchy = {id, indent ->
    def model = _pao[id.trim()]
    def allPointNames     = model.xml.points.point.collect{it.name[0].value()[0]}
    def allAttributeNames = model.xml.attributes.attribute.collect{it.attribute('name')}
    def allTagNames       = model.xml.tags.tag.collect{it.attribute('name')}

    println "${'\t' * indent}$id:"
    if( allTagNames.size() > 0 )          println "${'\t' * (indent+1)}tags: ${allTagNames.sort().join('\t')}"
    if( allAttributeNames.size() > 0 )    println "${'\t' * (indent+1)}attr: ${allAttributeNames.sort().join('\t')}"
    if( allPointNames.size() > 0 )        println "${'\t' * (indent+1)}pts:  ${allPointNames.sort().join('\t')}"

    println ""
    String[] inherits = model.xml.attribute('inherits')?.split(",")
    inherits.each{ id2 ->
        recurseChildrenToPrintPOINTHierarchy(id2, indent)
    }
}


def recurseChildrenToPrintHierarchyFromRoots    /*****************  OLD  *******************/
recurseChildrenToPrintHierarchyFromRoots = {id, level ->
    print "\t" * level
    println id
    _pao[id][children].each{ cc ->
        recurseChildrenToPrintHierarchyFromRoots(cc, level+1)
    }
}



def addMutualPaoAndModel = { paoId, modelId, rootMap ->
    if( rootMap[paoId][MODEL_IDS] == null ) rootMap[paoId][MODEL_IDS] = []
    rootMap[paoId][MODEL_IDS] << modelId
    if(rootMap[modelId][PAO_IDS] == null ) rootMap[modelId][PAO_IDS] = []
    rootMap[modelId][PAO_IDS] << paoId
}
/******************
    This method goes through and adds leaf children (models) as root[paoId][MODEL_IDS]
 ******************/
def recurseAddingPaoChildren
recurseAddingPaoChildren = { childId, paoId, root, index -> 
    def children = root[childId][CHILDREN]
    if( ! children.isEmpty() ) {
        if( childId.startsWith("RFN"))
            println "${'\t'*index}$childId"
        children.each{ recurseAddingPaoChildren(it, paoId, root, index+1) }      // recurse into non-leaf children
        
        // Safety: SPECIFIC TO RFN - if it is a model, add it even if inherited!
        if( _modelIds.contains(childId) )
            addMutualPaoAndModel(paoId, childId, root)
    } else {
        addMutualPaoAndModel(paoId, childId, root)
    }
}

def processPaoFile = { inputFile, outputFile ->
    def paoFIS         = new FileInputStream(inputFile)
    
    try {
        def fileId = addFile(inputFile, Type.PAO)
        def paoDefinitions = new XmlParser().parse(paoFIS)

        readPaoFile(paoDefinitions, fileId)
    
        def root = __data[fileId]
        root[TOPLEVEL].each{root[PAO_TO_PTNAMES][it] =[]}            // START the PAOs
        root[PAO_TO_PTNAMES].each{ paoId, arr ->

            root[paoId][CHILDREN].each{ childId ->
                recurseAddingPaoChildren(childId, paoId, root, 1)
            }
            if( paoId.startsWith("RFN_"))
                printf "%30s: ${root[paoId][MODEL_IDS]}\n", [paoId]
        }

        /******  Because paoDefinition.xml includes MANY we don't care about, we need to start at our models and work backwards  ******/
        _modelIds.each{ id ->
            extractPaoData( fileId, id, id )
        }
    
        printPao(fileId, outputFile)
        return fileId;    // This seems to negate the whole stack-dump of the XML file in the console...
    } finally {
        paoFIS.close()
    }
}


/**
    rfnPointMapping.xml structure:
        <pointGroup>
            <paoType />++
            <point> ... </point>++
        </pointGroup>
**/
def processRfnFile = { inputFile, outputFile -> 
    def rfnFIS         = new FileInputStream(inputFile)
    try {
        def fileId = addFile(inputFile, Type.RFN)
        def maps = new XmlParser().parse(rfnFIS)
    
        maps.pointGroup.each { group ->
        
            // Save and process all paoTypes
            def types = [:]
            group.paoType.each{ 
                String modelId = it.attribute('value')
                types[modelId] = it
            }
        
            boolean hasOnlyELO = true
            def localModelIds = []
            types.keySet().each{ 
                if( !(it.contains("ELO") && it.contains("WMETER")))
                    localModelIds << it
            }
            if( localModelIds.isEmpty() ) return
        
            def root = __data[fileId]

            // Remember points in 2 collections,
            // and attach them to every paoType in this group        
            group.point.each{                        // 'it' is the assumed point's data structure
                def ptName = it.attribute('name')    //        println "\t${ptName}"
//                println "\t$ptName"
                if(root[POINTS].get(ptName) == null) {
                    root[POINTS][ptName] = [(XML):it,(MODELS):localModelIds]            // REVISED
                    root[PT_TO_TYPES][ptName] = []
                }
                group.paoType.each{ rfnPaoType ->
                    def modelName = rfnPaoType.attribute('value')
                    root[PT_TO_TYPES][ptName]     << modelName
                    if( root[PAO_TO_PTNAMES][modelName] == null )
                        root[PAO_TO_PTNAMES][modelName] = []
                    root[PAO_TO_PTNAMES][modelName] << ptName
                }
            }
        }
        
        printRfn(fileId, outputFile)
        return fileId
    } finally {
        rfnFIS.close()
    }
}  





/****    print the comparison between RFN and PAO files   ****/
int CHARS_WIDE = 211;
def printComparisonHeader = { out -> 
    out << ('-'*CHARS_WIDE) << "\n"
    out.printf PAO_HEADER_FULL, "** Point Name **", ' '
    out << ('-'*CHARS_WIDE) << "\n"
}

def printComparison = { paoFileId, rfnFileId, localOutFile ->
    def localOut            = new GroovyPrintStream(localOutFile)
    localOut << OUTPUT_KEY
    printComparisonHeader(localOut)

    def paoIds        = __data[paoFileId][PAO_TO_PTNAMES].keySet()
    paoIds.sort {left, right -> 
        final boolean ltCalc = left.contains("RFN_PTCALC__")
        final boolean rtCalc = right.contains("RFN_PTCALC__")
        if( (ltCalc && rtCalc) || !(ltCalc && rtCalc))
            return left <=> right
        if( ltCalc )    return 1
        return -1
    }

    def paoPoints    = __data[paoFileId][POINTS]
    def rfnPoints    = __data[rfnFileId][POINTS]
    def ptKeys       = paoPoints.keySet()    //.sort()
    def paoEvents    = __data[paoFileId][EVENTS]

    def paoAttrByPt = [:]
    __data[paoFileId][ATTRIBUTES_BY_POINT].each{k,v -> paoAttrByPt[k] = v}    // dup so we don't modify the original map

    ptKeys.eachWithIndex{ ptName, ii -> 
        def paoMap = paoPoints[ptName]
        def rfnMap = rfnPoints[ptName]
        int pointOffset = Integer.parseInt(paoMap[XML].attribute('offset'))
        def outputStr = ROW_PREFIX

        def rawUom = paoMap.xml.unitofmeasure[0].attribute('value')
        String uom = __UOM_TO_ABBRV[rawUom] ?: rawUom
        String modifiers = "-----"
        if( rfnMap != null ) {
            uom = rfnMap['xml']['uom'].text()
            uom = __UOM_TO_ABBRV[uom] ?: uom
            // Make the XML representation into a single, brief string
            modifiers = rfnMap.xml.modifiers.modifier.size() == 0 ? '(none)' :
                            rfnMap.xml.modifiers.modifier.collect({it -> __MODIFIERS_TO_ABBRV[it.text()] ?: it.text() }).join('');
        }

        def attr = null
        if( paoAttrByPt.keySet().contains(ptName) ) {
            attr = paoAttrByPt[ptName]
            paoAttrByPt.remove( ptName )            // Found, but will the models match?
        }

        _modelCols.eachWithIndex{ modelTinyId, modelIndex -> 
            final String modelIdFull = _modelIds[modelIndex];
            outputStr += ' '
            final boolean inPao = paoMap[MODELS].contains(modelIdFull)
            final boolean inRfn = rfnMap != null && rfnMap[MODELS].contains(modelIdFull)
            if( inPao || inRfn )
                outputStr += modelTinyId
            else
                outputStr += ' '* modelTinyId.length()

            // Show the modifier if it exists in one file/version but not the other
            if( inPao == inRfn )
                outputStr += ' '
            else if( inPao )
                outputStr += '>'
            else if( inRfn )
                outputStr += '<'

            if(attr != null && (inPao || inRfn) && !attr[MODELS].contains(modelIdFull))
                outputStr += 'a'
            else if(attr != null && !inPao && !inRfn && attr[MODELS].contains(modelIdFull))
                outputStr += '!'
            if( SHOW_MIDLINE_OFFSET && index == 10 )
                outputStr += ' |%03d|'
        }
        outputStr += ROW_SUFFIX
        if( ii != 0 && (ii % 21) == 0 ) {
            printComparisonHeader(localOut)
        } else if( ii != 0 && (ii % 3) == 0 ) // throw a blank line between every 3 to make it easier to read
            localOut.println ""
        localOut.printf(outputStr, [uom, modifiers, ptName, pointOffset,pointOffset, pointOffset, ptName])
    }
    printComparisonHeader(localOut)

    localOut.println "\n\nEvents ('+' prefix means there is a related attribute) [${paoEvents.size()}]:"
    paoEvents.each{ k, v -> 
        if( paoAttrByPt.keySet().contains(k) ) {
            paoAttrByPt.remove( k )
            localOut.println "\t+ ${k}"
        } else
            localOut.println "\t  $k"
    }
    localOut.println "\n\nUnmatched Attributes (${paoAttrByPt.keySet().size()}):"
    paoAttrByPt.keySet().each{ key -> localOut.println "\t$key" }

    def paoTags    = __data[paoFileId][TAGS].sort()
    localOut.println "\n\nTags:"
    paoTags.eachWithIndex{ tagId, tagMap, ii -> 

        def outputStr = '%35s  '
        _modelCols.eachWithIndex{ modelTinyId, modelIndex -> 
            final String modelIdFull = _modelIds[modelIndex];
            outputStr += ' '
            final boolean inPao = tagMap[MODELS].contains(modelIdFull)
            if( inPao )
                outputStr += modelTinyId
            else
                outputStr += ' '* modelTinyId.length()
        }
        outputStr += ROW_SUFFIX_MIN
        if( ii != 0 && (ii % 3) == 0 )
            localOut.println ""
        localOut.printf(outputStr, [tagId, 0, '-'])    // I didn't expect this to grow as well... 20130925Wpm
    }
    
    return null
}


def _paoFileId = processPaoFile( paoInputFile, paoOutputFile )
def _rfnFileId = processRfnFile( rfnInputFile, rfnOutputFile )
printComparison(_paoFileId, _rfnFileId, paoVrfnOutputFile)


// added 20130320W 10:57CT
if( COMPARE_OTHER_VERSIONS_OF_INPUTS ) {
    _modelIds  = _modelIds_ORIG
    _modelCols = _modelCols_ORIG
    def _paoFileId_ORIGINAL = processPaoFile( paoInputFile47957, paoOutputFile47957 )
    def _rfnFileId_ORIGINAL = processRfnFile( rfnInputFile46349, rfnOutputFile46349 )
    printComparison(_paoFileId_ORIGINAL, _rfnFileId_ORIGINAL, paoVrfnOutputFile_ORIGINAL)
} else
    println "Code exists for comparing versions of the files - not currently triggered. 99921"




/*************************  Correlate Attributes from different PAO files  ***********************/
if(! COMPARE_PAO_ATTRIBUTES) {
    println "Code exists for comparing PAO attributes - not currently triggered. 99922"
    return;
}

String[] _allACols = "A3R A3K A3T A3D A3".split(" ")
String[] _nonARfns = "CL2 CD2 FL2 FX2 FD2 FL1 FX1 FD1".split(" ")


_modelIds  = _modelIds_COMBO
_modelCols = _modelCols_COMBO

def localOut = System.out
def paoAttributes_NEW  = __data[_paoFileId][ATTRIBUTES]
def paoAttributes_ORIG  = __data[_paoFileId_ORIGINAL][ATTRIBUTES]

def attributeNames = paoAttributes_NEW.collect{k,v -> k}
paoAttributes_ORIG.each{ k,v -> if(! attributeNames.contains(k)) attributeNames << k}

localOut.println "\n\nAttributes[${attributeNames.size()}]:    NEW FILE <>  OLD FILE"

attributeNames.sort().eachWithIndex{ attrId, ii -> 
        def aAs = [null, null, null, null, null]
        def cfs = []    // (0..<(_nonARfns.size())).collect{null}

        def outputStr = '%48s  '
        _modelCols.eachWithIndex{ modelTinyId, modelIndex -> 
            final String modelIdFull = _modelIds[modelIndex];
            outputStr             += ' '
            def pao1Attr             = __data[_paoFileId][ATTRIBUTES][attrId]
            final boolean inPao1     = pao1Attr != null && pao1Attr[MODELS].contains(modelIdFull)
            def pao2Attr             = __data[_paoFileId_ORIGINAL][ATTRIBUTES][attrId]
            final boolean inPao2     = pao2Attr != null && pao2Attr[MODELS].contains(modelIdFull)
            if( inPao1 == inPao2 )
                outputStr         += ' '
            else if( inPao1 ) {
                outputStr         += '+'
                (0..4).each{jj -> if (_allACols[jj] == modelTinyId) { aAs[jj] = true; return}}
                (0..<(_nonARfns.size())).each{jj -> if (_nonARfns[jj] == modelTinyId) { cfs << true; return }}    //{ cfs[jj] = true; return}}
                
            } else if( inPao2 ) {
                outputStr         += '-'
                (0..4).each{jj -> if (_allACols[jj] == modelTinyId) { aAs[jj] = false; return}}
                (0..<(_nonARfns.size())).each{jj -> if (_nonARfns[jj] == modelTinyId) { cfs << false; return }}    //{ cfs[jj] = false; return}}
            }
            if( inPao1 || inPao2 )
                outputStr         += modelTinyId
            else
                outputStr         += ' '* modelTinyId.length()
            
        }
        if( aAs != [null, null, null, null, null] && aAs != [true, true, true, true, false] )
            outputStr += " ##"
        if( ! cfs.isEmpty() )
            outputStr += " ??"
        outputStr += ROW_SUFFIX
        if( ii != 0 && (ii % 3) == 0 )
            localOut.println ""
        localOut.printf(outputStr, [attrId])
    }
return null