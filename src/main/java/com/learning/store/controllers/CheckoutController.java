package com.learning.store.controllers;

import com.learning.store.dtos.CheckoutRequest;
import com.learning.store.dtos.CheckoutResponse;
import com.learning.store.exceptions.CartIsEmptyException;
import com.learning.store.exceptions.CartNotFoundException;
import com.learning.store.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private CheckoutService checkoutService;

    @PostMapping
    public CheckoutResponse checkout(
            @Valid @RequestBody CheckoutRequest request
    ) {
        return checkoutService.checkout(request);
    }

    @ExceptionHandler({CartNotFoundException.class, CartIsEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception exception) {
        return ResponseEntity.badRequest().body(
                new ErrorDto(exception.getMessage())
        );
    }
}
