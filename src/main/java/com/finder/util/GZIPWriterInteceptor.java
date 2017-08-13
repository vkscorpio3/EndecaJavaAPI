package com.finder.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class GZIPWriterInteceptor implements WriterInterceptor {
	static final Logger LOGGER = LogManager.getLogger(GZIPWriterInteceptor.class.getName());
    private HttpHeaders httpHeaders;

    public GZIPWriterInteceptor(@Context @NotNull HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) 
        throws IOException, WebApplicationException {
    	
    	boolean isDataGZIPPED = false;
    	
    	// get info object for meta data
    	HashMap<String, Object> info = (HashMap<String, Object>) context.getProperty("info");
    	
    	// only gzip if the gzip flag is enabled
    	Boolean gzipData = (Boolean) context.getProperty("gzip");
    	if (gzipData) {
	    	// get accept-encoding header value
	    	List<String> acceptEncoding =  httpHeaders.getRequestHeaders().get(HttpHeaders.ACCEPT_ENCODING);
	    		    	
	        // Compress if client accepts gzip encoding
	    	if (acceptEncoding != null) {
		        for (String s : acceptEncoding) {
		            if(s.contains("gzip")) {
		            	isDataGZIPPED = true;
		            	// set response header
		                context.getHeaders().add(HttpHeaders.CONTENT_ENCODING, "gzip");
		                // gzip response
		                final OutputStream outputStream = context.getOutputStream();
		                context.setOutputStream(new GZIPOutputStream(outputStream));
		
		                break;
		            }
		        }
	    	}
    	} 
    	
    	if (isDataGZIPPED) {
    		info.put("compressed", "Yes, response was gzipped.");
    	} else if (gzipData) {
    		info.put("compressed", "No, request did not include 'Accept-Encoding: gzip' header.");
    	} else {
    		info.put("compressed", "No, request did not include gzip flag in query string.");
    	}
    	context.proceed();
    }
}