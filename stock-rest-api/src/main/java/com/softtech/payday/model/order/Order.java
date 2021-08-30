package com.softtech.payday.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.softtech.payday.model.audit.UserDateAudit;
import com.softtech.payday.model.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "placeorder")
public class Order extends UserDateAudit {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="[symbol]")
    @NotBlank
    private String symbol;

    @Column(name = "order_type")
    @NotBlank
    private String orderType;

    @Column(name = "target_amount")
    private BigDecimal targetAmount;

    @Column(name="[count]")
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    public User getUser() {
        return user;
    }
}
