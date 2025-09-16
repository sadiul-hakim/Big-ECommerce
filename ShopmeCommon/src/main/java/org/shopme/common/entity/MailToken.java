package org.shopme.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.shopme.common.enumeration.MailTokenType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customerId", "type"}),
        @UniqueConstraint(columnNames = {"token"})
})
public class MailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int customerId;

    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MailTokenType type;

    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private boolean used = false;
}
