package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    List<Product> findByCountryId(Long countryId);
    List<Product> findByNameContainingIgnoreCase(String name);
}
