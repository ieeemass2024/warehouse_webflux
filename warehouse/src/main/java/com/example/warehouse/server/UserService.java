package com.example.warehouse.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.example.warehouse.bean.User;
import com.example.warehouse.mapper.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository; // 注入UserRepository

    // 查询所有用户
    public Flux<User> userList() {
        return userRepository.findAll(); // 使用Reactive Repository方法
    }

    // 增加保存用户
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    // 更改用户信息
    public Mono<User> update(User user) {
        return userRepository.save(user); // R2DBC使用save方法更新
    }

    // 根据id查找用户
    public Mono<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    // 根据id删除用户
    public Mono<Void> delete(Integer id) {
        return userRepository.deleteById(id);
    }

    // 用户认证
    public Mono<User> authenticate(String username, String password) {

        return userRepository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password));
    }
}
