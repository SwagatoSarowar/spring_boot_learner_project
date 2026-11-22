package com.learning.store.mappers;

import com.learning.store.dtos.OrderDto;
import com.learning.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
