package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.dao.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseExceptionUtil {
    private  static final Logger logger = LoggerFactory.getLogger(ResponseExceptionUtil.class);

    public static ResponseStatusException getResponseStatusExcception(Exception ex){
        if(ex instanceof IllegalArgumentException){
            logger.debug("Invalid put", ex);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } else if (ex instanceof ResourceNotFoundException) {
            logger.debug("Not found", ex);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } else {
            logger.error("Internal Error", ex);
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
