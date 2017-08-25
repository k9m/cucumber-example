package org.sytac.cucumber.example.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.sytac.cucumber.example.model.User;

import java.util.List;

public interface UserRepo extends PagingAndSortingRepository<User, Long> {

    List<User> findByEmail(@Param("email") String email);
    List<User> findAllBy();
}
