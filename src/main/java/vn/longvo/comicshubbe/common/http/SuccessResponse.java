package vn.longvo.comicshubbe.common.http;

import org.springframework.http.HttpStatus;

public class SuccessResponse extends BaseResponse {

  public SuccessResponse() {
    super(HttpStatus.OK);
    super.setSuccess(true);
  }

  public BaseResponse setData(Object data) {
    super.addPayloadKey("data", data);
    return this;
  }

}
