package com.example.SampleProject.service;

import com.example.SampleProject.dto.PermissionDto;

import java.util.List;

public interface PermissionService {
    List<PermissionDto> getAll();
    PermissionDto findById(Long id);
}
