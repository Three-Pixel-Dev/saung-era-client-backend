package org.threepixeldev.saungeraclient.features.requestrefund.service;
import org.threepixeldev.saungeraclient.features.requestrefund.dto.request.RefundRequest;
import org.threepixeldev.saungeraclient.features.requestrefund.dto.response.RefundOrderResponse;
import org.threepixeldev.saungeraclient.features.requestrefund.dto.response.RefundResponse;

import java.util.List;

public interface RequestRefundService {
    RefundResponse requestRefund(Long orderId, RefundRequest request);
    List<RefundOrderResponse> getMyRefunds();
}