package hr.foi.daspicko.iotmas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OpenFireClientException extends RuntimeException {

    public OpenFireClientException(String s) {
        super(s);
    }

}
