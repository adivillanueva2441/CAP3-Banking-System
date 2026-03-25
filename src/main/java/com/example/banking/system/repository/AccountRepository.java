package com.example.banking.system.repository;

import com.example.banking.system.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

    //Retrieves all account types bound to the user.
    List<Account> findByUserId(Long id);
}
