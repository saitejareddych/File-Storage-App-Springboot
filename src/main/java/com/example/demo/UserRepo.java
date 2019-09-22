package com.example.demo;
import org.springframework.data.repository.CrudRepository;


public interface UserRepo extends CrudRepository<User,String> {
	
	User findByUsername(String username);

}
