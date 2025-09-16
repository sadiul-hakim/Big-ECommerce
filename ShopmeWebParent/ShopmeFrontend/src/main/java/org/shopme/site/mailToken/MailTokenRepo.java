package org.shopme.site.mailToken;

import org.shopme.common.entity.MailToken;
import org.shopme.common.enumeration.MailTokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MailTokenRepo extends JpaRepository<MailToken, Long> {

    Optional<MailToken> findByCustomerIdAndType(long customerId, MailTokenType type);

    Optional<MailToken> findByToken(String token);

    List<MailToken> findAllByCustomerId(long customerId);
}
