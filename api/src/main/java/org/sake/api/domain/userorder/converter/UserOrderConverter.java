package org.sake.api.domain.userorder.converter;

import org.sake.api.common.annotation.Converter;
import org.sake.api.domain.user.model.User;
import org.sake.api.domain.userorder.controller.model.UserOrderResponse;
import org.sake.db.storemenu.StoreMenuEntity;
import org.sake.db.userorder.UserOrderEntity;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Converter
public class UserOrderConverter {

    public UserOrderEntity toEntity(
            User user,
            @NotNull Long storeId, List<StoreMenuEntity> storeMenuEntityList
    ){
        var totalAmount = storeMenuEntityList.stream()
                .map(it -> it.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return UserOrderEntity.builder()
                .userId(user.getId())
                .storeId(storeId)
                .amount(totalAmount)
                .build()
                ;
    }

    public UserOrderResponse toResponse(
            UserOrderEntity userOrderEntity
    ){
        return UserOrderResponse.builder()
                .id(userOrderEntity.getId())
                .status(userOrderEntity.getStatus())
                .amount(userOrderEntity.getAmount())
                .orderedAt(userOrderEntity.getOrderedAt())
                .acceptedAt(userOrderEntity.getAcceptedAt())
                .deliveryStartedAt(userOrderEntity.getDeliveryStartedAt())
                .receivedAt(userOrderEntity.getReceivedAt())
                .build()
                ;
    }


}
