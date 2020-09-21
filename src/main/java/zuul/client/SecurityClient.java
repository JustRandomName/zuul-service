package zuul.client;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author n.zhuchkevich
 * @since 09/21/2020
 * Sends requests to security service
 * */
@FeignClient("security")
public interface SecurityClient {

    @RequestMapping(value = "/isValid", method = POST)
    boolean isValid(String token);


    @RequestMapping(value = "/isAdmin", method = POST)
    boolean isAdmin(String token);

    @RequestMapping(value = "/getRoles", method = POST)
    List getRoles(@RequestHeader("Authorization") String token, String tokena);
}
