package com.softtech.kafka.stock.tick.consumer.avro.repository;

import com.softtech.kafka.stock.tick.consumer.avro.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findById(@NotBlank Long userId);

	Optional<User> findByUsername(@NotBlank String username);


}
