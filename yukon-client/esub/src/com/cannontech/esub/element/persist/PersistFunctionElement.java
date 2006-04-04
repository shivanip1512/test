package com.cannontech.esub.element.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.element.*;
import com.loox.jloox.LxSaveUtils;

/**
 * @author aaron
 */
public class PersistFunctionElement extends BasePersistElement 
{

    // Only create one of these
    private static PersistElement instance = null;
    
    public static synchronized PersistElement getInstance() 
    {
        if(instance == null) 
        {
            instance = new PersistFunctionElement();
        }
        return instance;
    }
    
    /**
     * @see com.cannontech.esub.element.persist.PersistElement#readFromJLX(DrawingElement, InputStream)
     */
    public void readFromJLX(DrawingElement drawingElem, InputStream in, int version) throws IOException {
            
            FunctionElement elem = (FunctionElement) drawingElem;
            
            switch(version) {
                
                case 1: 
                {
                  
                    elem.setFunctionID(LxSaveUtils.readInt(in));
                    elem.setImage(elem.getImageIcon().getImage());
                    ArrayList argList = new ArrayList();
                    int argCount = LxSaveUtils.readInt(in);
                    for (int i =0; i < argCount; i++)
                    {
                        argList.add(LxSaveUtils.readString(in));
                    }
                    elem.setArgList(argList);
                    break;
                }
               
                default: 
                {
                    throw new IOException("Unknown version: " + version + " in " + elem.getClass().getName());
                }
            }
    }

    /**
     * @see com.cannontech.esub.element.persist.PersistElement#saveAsJLX(DrawingElement, OutputStream)
     */
    public void saveAsJLX(DrawingElement drawingElem, OutputStream out, int version) throws IOException {    
            FunctionElement elem = (FunctionElement) drawingElem;
            LxSaveUtils.writeInt(out, elem.getFunctionID());
            LxSaveUtils.writeInt(out, elem.getArgList().size());
            ArrayList argList = elem.getArgList();
            for(int i = 0; i < argList.size(); i++)
            {
                LxSaveUtils.writeString(out, (String)argList.get(i));
            }
        }   
}