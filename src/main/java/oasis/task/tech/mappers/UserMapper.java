package oasis.task.tech.mappers;

import oasis.task.tech.domains.actors.User;
import oasis.task.tech.dto.PaginatedListDto;
import oasis.task.tech.dto.actors.UserResponse;
import oasis.task.tech.util.Utility;
import org.springframework.data.domain.Page;

/**
 * Author: Paul Nyishar
 * Date:12/9/24
 * Time:1:47AM
 * Project:task-management
 */

public class UserMapper {
    public static PaginatedListDto<UserResponse> mapToUserResponse(
            Page<User> userPage, int page, int limit) {
        PaginatedListDto<UserResponse> paginatedListDto = new PaginatedListDto<>();
        paginatedListDto.setPage(page);
        paginatedListDto.setLimit(limit);
        paginatedListDto.setTotal(userPage.getTotalElements());
        paginatedListDto.setEntities(Utility.map(userPage.getContent(),UserResponse.class));

        return paginatedListDto;
    }
}
