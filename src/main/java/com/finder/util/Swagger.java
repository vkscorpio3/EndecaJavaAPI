package com.finder.util;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.finder.resources.RecordResource;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * The Class Swagger.
 */
public class Swagger extends HttpServlet {
    
    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setBasePath("/finder");
        beanConfig.setResourcePackage(RecordResource.class.getPackage().getName());
        beanConfig.setTitle("Endeca Java Web Service REST Api");
        beanConfig.setDescription("This project is for making web service calls to endeca to get records, filters and results.");
        beanConfig.setScan(true);
    }
}