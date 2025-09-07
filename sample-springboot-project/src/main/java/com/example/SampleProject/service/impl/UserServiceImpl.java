package com.example.SampleProject.service.impl;

import com.example.SampleProject.dto.RoleDto;
import com.example.SampleProject.dto.UserDto;
import com.example.SampleProject.exception.RecordNotFoundException;
import com.example.SampleProject.model.Role;
import com.example.SampleProject.model.User;
import com.example.SampleProject.repository.RoleRepository;
import com.example.SampleProject.repository.UserRepository;
import com.example.SampleProject.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDto registerUser(UserDto userdto) {
        User user = toEntity(userdto);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        Set<Role> roleList = new HashSet<>();
        for(Role role: user.getRoles()){
            Role foundRole = roleRepository.findById(role.getId())
                    .orElseThrow(()-> new RecordNotFoundException("Role not found"));
            roleList.add(foundRole);
        }
        user.setRoles(roleList);
        User savedUser = userRepository.save(user);
        return toDto(savedUser);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("User not found for id => %d", id)));
        return toDto(user);
    }

    // Convert User entity to UserDto
    public UserDto toDto(User user) {
        Set<RoleDto> roleDtos = user.getRoles().stream()
                .map(this::roleToDto)
                .collect(Collectors.toSet());

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .password(user.getPassword()) // Consider not exposing password in DTO
                .roles(roleDtos)
                .build();
    }

    // Convert UserDto to User entity
    public User toEntity(UserDto userDto) {
        Set<Role> roles = userDto.getRoles().stream()
                .map(this::roleDtoToEntity)
                .collect(Collectors.toSet());

        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .password(userDto.getPassword())
                .roles(roles)
                .build();
    }

    // Helper method to convert Role entity to RoleDto
    private RoleDto roleToDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    // Helper method to convert RoleDto to Role entity
    private Role roleDtoToEntity(RoleDto roleDto) {
        return Role.builder()
                .id(roleDto.getId())
                .name(roleDto.getName())
                .build();
    }
}