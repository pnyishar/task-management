package oasis.task.tech.domains.actors;

import lombok.*;
import oasis.task.tech.constants.Priority;
import oasis.task.tech.domains.base.StringIdentifierModel;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:2:13PM
 * Project:task-management
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task extends StringIdentifierModel {
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
