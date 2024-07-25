package Tasaheel.paymentManagement;

import java.math.BigDecimal;

public class PaymentResponse {
    private String message;
    private BigDecimal amount;

    public PaymentResponse(String message, BigDecimal amount) {
        this.message = message;
        this.amount = amount;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
