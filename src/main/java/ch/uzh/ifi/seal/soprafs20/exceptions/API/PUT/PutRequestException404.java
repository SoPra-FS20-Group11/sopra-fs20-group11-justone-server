package ch.uzh.ifi.seal.soprafs20.exceptions.API.PUT;

import ch.uzh.ifi.seal.soprafs20.exceptions.API.ApiRequestException;
import org.springframework.http.HttpStatus;

public class PutRequestException404 extends ApiRequestException {

    public PutRequestException404(String message, HttpStatus statusCode){
        super(message, statusCode);
    }

    public PutRequestException404(String message, Throwable cause){
        super(message, cause);
    }
}