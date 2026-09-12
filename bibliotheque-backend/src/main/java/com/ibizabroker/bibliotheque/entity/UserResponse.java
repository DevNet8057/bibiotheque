package com.ibizabroker.bibliotheque.entity;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Integer userId;
    private String username;
    private String name;
    private Set<Role> role;
}
