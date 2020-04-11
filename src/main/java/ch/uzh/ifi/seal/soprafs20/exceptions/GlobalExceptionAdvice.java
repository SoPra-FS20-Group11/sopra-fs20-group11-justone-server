package ch.uzh.ifi.seal.soprafs20.exceptions;

import ch.uzh.ifi.seal.soprafs20.exceptions.API.GET.GetException;
import ch.uzh.ifi.seal.soprafs20.exceptions.API.GET.GetRequestException404;
import ch.uzh.ifi.seal.soprafs20.exceptions.API.POST.PostException;
import ch.uzh.ifi.seal.soprafs20.exceptions.API.POST.PostRequestException409;
import ch.uzh.ifi.seal.soprafs20.exceptions.API.PUT.PutException;
import ch.uzh.ifi.seal.soprafs20.exceptions.API.PUT.PutRequestException204;
import ch.uzh.ifi.seal.soprafs20.exceptions.API.PUT.PutRequestException401;
import ch.uzh.ifi.seal.soprafs20.exceptions.API.PUT.PutRequestException404;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(SopraServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleBadRequestException(SopraServiceException ex) {
        log.error(String.format("SopraServiceException raised:%s", ex));
        return new ResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity handleTransactionSystemException(Exception ex, HttpServletRequest request) {
        log.error(String.format("Request: %s raised %s", request.getRequestURL(), ex));
        return new ResponseEntity(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Keep this one disable for all testing purposes -> it shows more detail with this one disabled
    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity handleException(Exception ex) {
        log.error(String.format("Exception raised:%s", ex));
        return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = PostRequestException409.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handlePostException409(PostRequestException409 ex){
        PostException postException = new PostException(ex.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(postException, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = PutRequestException204.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> handlePutException204(PutRequestException204 ex){
        PutException putException = new PutException(ex.getMessage(), HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(putException, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(value = PutRequestException401.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handlePutException401(PutRequestException204 ex){
        PutException putException = new PutException(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(putException, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = PutRequestException404.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handlePutException404(PutRequestException204 ex){
        PutException putException = new PutException(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(putException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = GetRequestException404.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleGetException404(GetRequestException404 ex){
        GetException getException = new GetException(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(getException, HttpStatus.NOT_FOUND);
    }
}