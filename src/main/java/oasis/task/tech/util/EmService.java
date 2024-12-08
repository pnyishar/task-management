package oasis.task.tech.util;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


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
