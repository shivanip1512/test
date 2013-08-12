package com.cannontech.web.menu.renderer;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.menu.ModuleBase;
import com.cannontech.web.menu.option.producer.SearchFormData;
import com.cannontech.web.menu.option.producer.SearchProducer;
import com.cannontech.web.menu.option.producer.SearchType;

public class SearchRenderer {
    
    @Autowired private YukonUserContextMessageSourceResolver resolver;

    public void render(ModuleBase module,HttpServletRequest req, Writer writer) throws IOException {
        Elements elems = new Elements();
        YukonUserContext context = YukonUserContextUtils.getYukonUserContext(req);
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        
        SearchProducer searchProducer = module.getSearchProducer();
        SearchFormData formData = null;
        if (searchProducer != null) {
            formData = searchProducer.getSearchProducer(context);
        }
        if (formData != null) {
            Attributes attrs = new Attributes();
            attrs.put("accept-charset", "ISO-8859-1");
            attrs.put("enctype", "application/x-www-form-urlencoded");
            attrs.put("method", formData.getFormMethod());
            attrs.put("action", formData.getFormAction());
            Element form = new Element(Tag.valueOf("form"), "", attrs);
            form.addClass("yukon-search-form");
            // search types?
            if (formData.getTypeOptions() != null) {
                attrs = new Attributes();
                attrs.put("onchange", "jQuery('.f-search-field').val('')");
                attrs.put("name", formData.getTypeName());
                Element select = new Element(Tag.valueOf("select"), "", attrs);
                
                for (SearchType searchType : formData.getTypeOptions()) {
                    attrs = new Attributes();
                    attrs.put("value", searchType.getSearchTypeValue());
                    Element option = new Element(Tag.valueOf("option"), "", attrs);
                    option.appendText(accessor.getMessage(searchType.getDisplayKey()));
                    
                    select.appendChild(option);
                }
                
                form.appendChild(select);
            }
            
            // search box
            attrs = new Attributes();
            attrs.put("type", "text");
            attrs.put("placeholder", accessor.getMessage("yukon.common.search.placeholder"));
            attrs.put("role", "search");
            attrs.put("name", formData.getFieldName());
            Element searchField = new Element(Tag.valueOf("input"), "", attrs);
            searchField.addClass("search-field");
            form.appendChild(searchField);
            
            elems.add(form);
        }
        
        String outerHtml = elems.outerHtml();
        writer.write(outerHtml);
    }
    
}
