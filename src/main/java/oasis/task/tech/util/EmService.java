package oasis.task.tech.util;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;


@Service
public class EmService {

    @PersistenceContext
    private EntityManager entityManager;

    private static EntityManager em;

    @PostConstruct
    public void init() {
        em = this.entityManager;
    }

    public static EntityManager em() {
        return em;
    }
}
