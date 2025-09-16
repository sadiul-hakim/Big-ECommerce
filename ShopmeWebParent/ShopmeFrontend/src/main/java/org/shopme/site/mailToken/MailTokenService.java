package org.shopme.site.mailToken;

import jakarta.persistence.EntityNotFoundException;
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
    public void save(int customerId, String token, MailTokenType type, int expireInMins) {

        try {
            MailToken oldToken = findByCustomerIdAndType(customerId, type);

            // Existing token → update it with new values
            oldToken.setToken(token);
            oldToken.setExpiryTime(LocalDateTime.now().plusMinutes(expireInMins));
            oldToken.setUsed(false); // reset usage since this is a fresh token
        } catch (EntityNotFoundException ex) {

            // No existing token → create a new one
            MailToken tokenM = new MailToken(
                    0,
                    customerId,
                    token,
                    type,
                    LocalDateTime.now().plusMinutes(expireInMins),
                    false // not used
            );
            repository.save(tokenM);
        }
    }

    public MailToken findByCustomerIdAndType(long customerId, MailTokenType type) {
        return repository.findByCustomerIdAndType(customerId, type)
                .orElseThrow(() -> new EntityNotFoundException("Token not found of customer " + customerId));
    }

    public MailToken findByToken(String token) {
        return repository.findByToken(token)
                .orElse(null);
    }

    @Transactional
    public JpaResult verify(long customerId, MailTokenType type, String token) {
        MailToken tokenM = findByCustomerIdAndType(customerId, type);
        if (tokenM == null) {
            return new JpaResult(JpaResultType.FAILED, "Invalid Token");
        }

        // check already used
        if (tokenM.isUsed()) {
            return new JpaResult(JpaResultType.FAILED, "Token already used!");
        }

        // check token match
        if (!tokenM.getToken().equals(token)) {
            return new JpaResult(JpaResultType.FAILED, "Invalid Token");
        }

        // check expiry
        if (tokenM.getExpiryTime().isBefore(LocalDateTime.now())) {
            return new JpaResult(JpaResultType.FAILED, "Token expired!");
        }

        tokenM.setUsed(true);

        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully verified the Token");
    }

    @Transactional
    public JpaResult verify(String token, boolean clearToken) {

        MailToken tokenM = findByToken(token);
        if (tokenM == null) {
            return new JpaResult(JpaResultType.FAILED, "Invalid Token");
        }

        // check already used
        if (tokenM.isUsed()) {
            return new JpaResult(JpaResultType.FAILED, "Token already used!");
        }

        // check expiry
        if (tokenM.getExpiryTime().isBefore(LocalDateTime.now())) {
            return new JpaResult(JpaResultType.FAILED, "Token expired!");
        }

        // One-time use
        if (clearToken) {
            tokenM.setUsed(true);
        }

        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully verified the Token", tokenM);
    }
}
