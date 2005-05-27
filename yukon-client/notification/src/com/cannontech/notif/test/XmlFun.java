package com.cannontech.notif.test;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;


public class XmlFun {
    
    static public String transformObject(Object o, InputStream stylesheet) throws XSLTransformException {
        Document doc = makeDocument(o);
        XSLTransformer t = new XSLTransformer(stylesheet);
        List m = t.transform(doc.getContent());
        XMLOutputter out = new XMLOutputter();
        return out.outputString(m);
    }
    
    static public Document makeDocument(Object o) {
        Document result;
        result = new Document();
        try {
            Element root = new Element(o.getClass().getName());
            result.setRootElement(root);
      
            decodeObject(o,root);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void decodeObject(Object o, Element root) throws IllegalArgumentException, IllegalAccessException {
        if (o instanceof Collection) {
            Collection col = (Collection) o;
            Element listEl = new Element("list");
            for (Iterator iter = col.iterator(); iter.hasNext();) {
                Object el = (Object) iter.next();
                Element innerE = new Element("item");
                decodeObject(el,innerE);
                //innerE.setText(el.toString());
                listEl.addContent(innerE);
            }
            root.addContent(listEl);
            
        } else {
            Field fields[] = o.getClass().getFields();
            boolean gotOne = false;
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                
                if (!Modifier.isStatic(f.getModifiers())) {
                    Element e = new Element(f.getName());
                    decodeObject(f.get(o),e);
                    root.addContent(e);
                    gotOne = true;
                }
            }
            if (!gotOne) {
                root.setText(o.toString());
            }
            
        } 
    }
    
    public static void main(String[] args) {
        try {
            TestObject testObject = new TestObject();
            Document doc = XmlFun.makeDocument(testObject);
            XMLOutputter out = new XMLOutputter();
            out.output(doc,System.out);
            System.out.println("--------------");
            InputStream file = new FileInputStream("test.xsl");
            String s = transformObject(testObject, file);
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XSLTransformException e) {
            e.printStackTrace();
        }
    }

}
