package com.learning.store.controllers;

import com.learning.store.dtos.ProductDto;
import com.learning.store.entities.Product;
import com.learning.store.mappers.ProductMapper;
import com.learning.store.repositories.CategoryRepository;
import com.learning.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<ProductDto> getAllProducts(
            @RequestParam(name = "sort", defaultValue = "", required = false) String sort,
            @RequestParam(name = "categoryId", defaultValue = "", required = false) Byte categoryId
    ) {
        List<Product> products;
        sort = Set.of("id", "name", "price").contains(sort) ? sort : "id";

        if (categoryId != null) products = productRepository.findByCategoryId(
                categoryId, Sort.by(sort));
        else products = productRepository.findAllWithCategory(Sort.by(sort));

        return products.stream().map(productMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable long id) {
        var product = productRepository.findById(id).orElse(null);

        if (product == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductDto productDto,
            UriComponentsBuilder uriBuilder
    ) {
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);

        if (category == null)
            return ResponseEntity.badRequest().build();

        var product = productMapper.toEntity(productDto);
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());

        var uri = uriBuilder.path("/product/{id}").buildAndExpand(product.getId()).toUri();

        return ResponseEntity.created(uri).body(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable(name = "id") Long id,
            @RequestBody ProductDto productDto
    ) {
        var product = productRepository.findById(id).orElse(null);
        var category = categoryRepository.findById(productDto.getCategoryId()).orElse(null);

        if (product == null)
            return ResponseEntity.notFound().build();

        if (category == null)
            return ResponseEntity.badRequest().build();

        productMapper.update(productDto, product);
        product.setCategory(category);
        productRepository.save(product);
        productDto.setId(product.getId());

        return ResponseEntity.ok(productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        var product = productRepository.findById(id).orElse(null);

        if (product == null)
            return ResponseEntity.notFound().build();

        productRepository.delete(product);

        return ResponseEntity.noContent().build();
    }
}
