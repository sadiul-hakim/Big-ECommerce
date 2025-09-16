package org.shopme.site.mailToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.MailToken;
import org.shopme.common.enumeration.MailTokenType;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailTokenService {

    private final MailTokenRepo repository;

    @Transactional
    public void save(long customerId, String token, MailTokenType type, int expireInMins) {

        MailToken oldToken = findByCustomerIdAndType(customerId, type);
        if (oldToken == null) {
            MailToken tokenM = new MailToken(0, customerId, token, type, LocalDateTime.now().plusMinutes(expireInMins));
            repository.save(tokenM);
        } else {
            oldToken.setToken(token);
            oldToken.setExpiryTime(LocalDateTime.now().plusMinutes(expireInMins));
        }
    }

    public MailToken findByCustomerIdAndType(long customerId, MailTokenType type) {
        return repository.findByCustomerIdAndType(customerId, type)
                .orElse(null);
    }

    @Transactional
    public JpaResult verify(long customerId, MailTokenType type, String token) {
        MailToken tokenM = findByCustomerIdAndType(customerId, type);
        if (tokenM == null) {
            return new JpaResult(JpaResultType.FAILED, "Invalid Token");
        }

        // check token match
        if (!tokenM.getToken().equals(token)) {
            return new JpaResult(JpaResultType.FAILED, "Invalid Token");
        }

        // check expiry
        if (tokenM.getExpiryTime().isBefore(LocalDateTime.now())) {
            return new JpaResult(JpaResultType.FAILED, "Token expired!");
        }

        // One-time use: remove or invalidate
        tokenM.setToken(null);

        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully verified the Token");
    }
}
