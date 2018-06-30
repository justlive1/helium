package vip.justlive.helium.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vip.justlive.helium.repository.entity.User;

/**
 * user repository
 *
 * @author wubo
 */
public interface UserRepository extends JpaRepository<User, Long> {

}
