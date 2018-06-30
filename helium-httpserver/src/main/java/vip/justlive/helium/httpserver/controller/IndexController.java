package vip.justlive.helium.httpserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.justlive.helium.repository.entity.User;
import vip.justlive.helium.repository.repository.UserRepository;

/**
 * @author wubo
 */
@RestController
public class IndexController {

  @Autowired
  private UserRepository userRepository;

  @RequestMapping("/login")
  public String login(User user) {

    userRepository.findOne(Example.of(user));

    return user.toString();
  }
}
