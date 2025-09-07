package com.example.SampleProject.repository;

import com.example.SampleProject.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permission by name
     */
    Optional<Permission> findByName(String name);

    /**
     * Check if permission exists by name
     */
    boolean existsByName(String name);

    /**
     * Find permission by name (case insensitive)
     */
    Optional<Permission> findByNameIgnoreCase(String name);

    /**
     * Find all permissions by value (true/false)
     */
    List<Permission> findByValue(Boolean value);

    /**
     * Find all active permissions (value = true)
     */
    @Query("SELECT p FROM Permission p WHERE p.value = true")
    List<Permission> findAllActivePermissions();

    /**
     * Find permissions by name containing (case insensitive)
     */
    List<Permission> findByNameContainingIgnoreCase(String namePattern);

    /**
     * Find permissions by multiple names
     */
    @Query("SELECT p FROM Permission p WHERE p.name IN :names")
    List<Permission> findByNameIn(@Param("names") List<String> names);

    /**
     * Count permissions by value
     */
    Long countByValue(Boolean value);
}