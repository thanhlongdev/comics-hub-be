package vn.longvo.comicshubbe.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security.crfs")
public class CRFSProperties {

  private boolean enabled = false;

}
