package com.learning.store.mappers;

import com.learning.store.dtos.CartDto;
import com.learning.store.dtos.CartItemDto;
import com.learning.store.entities.Cart;
import com.learning.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);


}
