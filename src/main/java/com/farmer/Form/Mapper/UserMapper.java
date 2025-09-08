package com.farmer.Form.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.farmer.Form.DTO.UserDTO;
import com.farmer.Form.DTO.UserViewDTO;
import com.farmer.Form.Entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Simple mapping without complex custom methods
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "kycStatus", ignore = true)
    @Mapping(target = "assignedEmployeeId", ignore = true)
    @Mapping(target = "forcePasswordChange", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "region", ignore = true)
    User toEntity(UserDTO dto);

    // Simple mapping to view DTO
    UserViewDTO toViewDto(User user);
}
