package vn.longvo.comicshubbe.handler;

import java.nio.file.AccessDeniedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.longvo.comicshubbe.common.constant.ErrorCodeEnum;
import vn.longvo.comicshubbe.common.http.BaseResponse;
import vn.longvo.comicshubbe.common.http.FailureResponse;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({Exception.class, RuntimeException.class})
  public ResponseEntity<?> handleAuthenticationException(Throwable exception) {
    log.error("Unexpected error occur with reason: {}", exception.getMessage());
    BaseResponse responseEntity = new FailureResponse()
        .setErrorMessage("The server occur unexpected error")
        .setErrorCode(ErrorCodeEnum.UNEXPECTED_ERROR)
        .setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    return responseEntity.build();
  }

}
