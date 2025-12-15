package com.nestuity.service.repository;

import com.nestuity.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Methods inherited from JpaRepository:
    // - List<User> findAll()
    // - List<User> findAllById(Iterable<Long> ids)
    // - Optional<User> findById(Long id)
    // - <S extends User> S save(S entity)
    // - <S extends User> List<S> saveAll(Iterable<S> entities)
    // - void deleteById(Long id)
    // - void delete(User entity)
    // - void deleteAll(Iterable<? extends User> entities)
    // - void deleteAll()
    // - boolean existsById(Long id)
    // - long count()
    // - void flush()
    // - <S extends User> S saveAndFlush(S entity)
    // - void deleteInBatch(Iterable<User> entities)
    // - void deleteAllInBatch()
    // - User getOne(Long id)

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}