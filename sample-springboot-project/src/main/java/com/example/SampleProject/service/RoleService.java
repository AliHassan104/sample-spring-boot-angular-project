package com.example.SampleProject.service;

import com.example.SampleProject.dto.RoleDto;

import java.util.List;

public interface RoleService {

    RoleDto addRole(RoleDto roleDto);
    List<RoleDto> getAll();
    RoleDto findById(Long id);
    RoleDto updateRole(Long id, RoleDto roleDto);

}
