package com.cannontech.web.layout.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ValueExpression;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.el.ExpressionFactoryImpl;
import org.apache.jasper.el.ELContextImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.web.layout.HttpExpressionLanguageResolver;
import com.google.common.collect.Lists;

public class HttpExpressionLanguageResolverImpl implements HttpExpressionLanguageResolver {

    @Autowired private ObjectFormattingService objectFormattingService;

    private static final CompositeELResolver compositeElResolver = new CompositeELResolver();
    private static final ExpressionFactoryImpl factory = new ExpressionFactoryImpl();
    static {
        compositeElResolver.add(new HttpRequestElResolver()); // our custom resolver
        compositeElResolver.add(new MapELResolver()); // this and the following are standard
        compositeElResolver.add(new ListELResolver());
        compositeElResolver.add(new ArrayELResolver());
        compositeElResolver.add(new BeanELResolver());
    }

    @Override
    public List<String> resolveElExpressions(List<String> labelArgumentExpressions, HttpServletRequest request) {
        List<String> result = Lists.newArrayListWithExpectedSize(labelArgumentExpressions.size());
        for (String expression : labelArgumentExpressions) {
            String text = resolveElExpression(expression, request);
            result.add(text);
        }
        return result;
    }

    @Override
    public String resolveElExpression(String expression, HttpServletRequest request) {
        if (StringUtils.isBlank(expression)) {
            return null;
        }
        ELContextImpl context = new ELContextImpl(compositeElResolver);
        context.putContext(ServletRequest.class, request);
        context.setFunctionMapper(new LocalFunctionMapper());

        ValueExpression valueExpression = factory.createValueExpression(context, expression, Object.class);
        Object value = valueExpression.getValue(context);
        String result =
            objectFormattingService.formatObjectAsString(value, YukonUserContextUtils.getYukonUserContext(request));
        return result;
    }

    private static final class LocalFunctionMapper extends FunctionMapper {
        @Override
        public Method resolveFunction(String prefix, String localName) {
            if (!StringUtils.equals(prefix, "local")) {
                return null;
            }

            // look for a method on this class
            Method[] methods = HttpExpressionLanguageResolverImpl.class.getMethods();
            for (Method method : methods) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    continue;
                }

                if (StringUtils.equals(localName, method.getName())) {
                    return method;
                }
            }

            return null;
        }
    }
}
