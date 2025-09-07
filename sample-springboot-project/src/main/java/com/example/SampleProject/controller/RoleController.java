package com.example.SampleProject.controller;

import com.example.SampleProject.dto.RoleDto;
import com.example.SampleProject.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoleDto> addRole(@RequestBody RoleDto roleDto) {
        RoleDto updatedRole = roleService.addRole(roleDto);
        return ResponseEntity.ok(updatedRole);
    }

    @GetMapping("/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RoleDto>> getAllRole() {
        List<RoleDto> roleDtoList = roleService.getAll();
        return ResponseEntity.ok(roleDtoList);
    }

    @GetMapping("/role/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto roleDto = roleService.findById(id);
        return ResponseEntity.ok(roleDto);
    }

    @PutMapping("/role/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id, @RequestBody RoleDto roleDto) {
        RoleDto updatedRoleDto = roleService.updateRole(id, roleDto);
        return ResponseEntity.ok(updatedRoleDto);
    }
}
