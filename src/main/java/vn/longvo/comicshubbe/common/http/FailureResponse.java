package vn.longvo.comicshubbe.common.http;

import java.util.Iterator;
import org.springframework.http.HttpStatus;
import vn.longvo.comicshubbe.common.constant.ErrorCodeEnum;

public class FailureResponse extends BaseResponse {

  public FailureResponse() {
    super(HttpStatus.BAD_REQUEST);
    super.setSuccess(false);
  }

  public BaseResponse setErrorMessage(Throwable throwable) {
    super.addPayloadKey("message", throwable.getMessage());
    return this;
  }

  public BaseResponse setErrorCode(ErrorCodeEnum errorCodeEnum) {
    super.addPayloadKey("errorCode", errorCodeEnum);
    return this;
  }

  public BaseResponse setErrors(Iterator<Object> objects) {
    super.addPayloadKey("errors", objects);
    return this;
  }

}
