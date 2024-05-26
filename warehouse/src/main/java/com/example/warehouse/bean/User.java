// package com.example.warehouse.bean;

// public class User {
//     private Integer id;
//     private String username;
//     private String password;
//     private String email;
//     private String register_time;
//     private String role;

//     public User(int i, String testUser1, String pass1, String role1, String email1, String time1) {
//         this.id = i;
//         this.username = testUser1;
//         this.password = pass1;
//         this.email = email1;
//         this.register_time = time1;
//         this.role = role1;
//     }

//     public User(){

//     }

//     // Getters and setters
//     public Integer getId() {
//         return id;
//     }
//     public void setId(Integer id) {
//         this.id = id;
//     }
//     public String getUsername() {
//         return username;
//     }
//     public void setUsername(String username) {
//         this.username = username;
//     }
//     public String getPassword() {
//         return password;
//     }
//     public void setPassword(String password) {
//         this.password = password;
//     }
//     public String getRole() {
//         return role;
//     }
//     public void setRole(String role) {
//         this.role = role;
//     }
//     public String getEmail() {
//         return email;
//     }
//     public void setEmail(String email) {
//         this.email = email;
//     }
//     public String getRegister_time() {
//         return register_time;
//     }
//     public void setRegister_time(String register_time) {
//         this.register_time = register_time;
//     }
// }
package com.example.warehouse.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Table("users")
public class User {
    @Id
    private Integer id;
    private String username;
    private String password;
    private String email;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;
    
    private String role;

    public User(int i, String testUser1, String pass1, String role1, String email1, LocalDateTime time1) {
        this.id = i;
        this.username = testUser1;
        this.password = pass1;
        this.email = email1;
        this.registerTime = time1;
        this.role = role1;
    }

    public User(int i, String testUser1, String pass1, String role1, String email1) {
        this.id = i;
        this.username = testUser1;
        this.password = pass1;
        this.email = email1;

        this.role = role1;
    }

    public User(String testUser1, String pass1) {

        this.username = testUser1;
        this.password = pass1;

    }

    public User() {
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public LocalDateTime getRegisterTime() {
        return registerTime;
    }
    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }
}
