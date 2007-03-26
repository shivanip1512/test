package com.cannontech.web.editor;

import com.cannontech.web.util.JSFParamUtil;

/**
 * Entry point for any editor or wizard. The corresponding JSP file wraps the content
 * of the specific type of editor give.
 * 
 * @author ryan
 */
public class EditorForm extends DBEditorForm
{
	private String editorType = "CBCEditor";


	public EditorForm() {
		super();
		initItem();
	}

	public void update() {
	}
	
	public void initItem() {
		
		String[] o = JSFParamUtil.getReqParamsVar("value");		
		System.out.println(o);
	}

	public boolean isCBCEditor() {
		return "CBCEditor".equalsIgnoreCase(getEditorType());
	}

	/**
	 * @return
	 */
	public String getEditorType() {
		return editorType;
	}

	/**
	 * @param string
	 */
	public void setEditorType(String string) {
		editorType = string;
	}


    protected void checkForErrors() throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void resetForm() {
        
    }

}