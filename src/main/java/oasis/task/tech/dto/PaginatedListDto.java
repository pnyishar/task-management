package oasis.task.tech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:4:21PM
 * Project:task-management
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedListDto<T> {
    private int page;

    private int limit;

    private Long total;

    private List<T> entities;
}
