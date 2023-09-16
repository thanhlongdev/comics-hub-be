package vn.longvo.comicshubbe.common.http;

import java.util.Iterator;
import org.springframework.http.HttpStatus;
import vn.longvo.comicshubbe.common.constant.ErrorCodeEnum;

public class FailureResponse extends BaseResponse {

  public FailureResponse() {
    super(HttpStatus.BAD_REQUEST);
    super.setSuccess(false);
  }

  public FailureResponse setErrorMessage(Throwable throwable) {
    super.addPayloadKey("errorMessage", throwable.getMessage());
    return this;
  }

  public FailureResponse setErrorMessage(String message) {
    super.addPayloadKey("message", message);
    return this;
  }

  public FailureResponse setErrorCode(ErrorCodeEnum errorCodeEnum) {
    super.addPayloadKey("errorCode", errorCodeEnum);
    return this;
  }

  public FailureResponse setErrors(Iterator<Object> objects) {
    super.addPayloadKey("errors", objects);
    return this;
  }

}
