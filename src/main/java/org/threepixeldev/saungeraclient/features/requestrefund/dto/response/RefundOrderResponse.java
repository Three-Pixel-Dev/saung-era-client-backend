package org.threepixeldev.saungeraclient.features.requestrefund.dto.response;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RefundOrderResponse {
    private Long id;
    private BigDecimal totalPrice;
    private LocalDateTime orderedDate;
    private String status;
    private String promotionCode;
    private int itemCount;
}