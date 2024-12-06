package oasis.task.tech.dao;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import oasis.task.tech.domains.base.Model;
import oasis.task.tech.util.EmService;
import oasis.task.tech.util.Utility;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Accessor {

    public static EntityManager em = EmService.em();

    public static void saveOne(Model model) {
        em.persist(model);
    }

    public static void updateOne(Model model) {
        em.merge(model);
    }

    public static void deleteOne(Model model) {
        em.remove(model);
    }

    public static <T> void deleteOne(T t) {
        em.remove(t);
    }

    public static <T, ID> T findOne(Class<T> clazz, ID id) {
        try {
            return em.find(clazz, id);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T findOne(Class<T> clazz, String key, Object value) {
        try {
            return em.createQuery(
                            "SELECT t FROM " + clazz.getSimpleName() + " t " + "WHERE t." + key + "=:value", clazz)
                    .setParameter("value", value)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T findOne(Class<T> clazz, Filter filter) {
        try {
            String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
            sql += filter.getSql();
            TypedQuery<T> q = em.createQuery(sql, clazz);
            addParam(q, filter);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> findAllBy(Class<T> clazz, String clause) {
        return findAllBy(clazz, clazz.getSimpleName(), clause);
    }

    public static <T> List<T> findAllBy(Class<T> clazz, String tableName, String clause) {
        String sql = String.format("SELECT * FROM %s where %s", tableName, clause);
        Query q = em.createNativeQuery(sql, clazz);
        return Utility.map(q.getResultList(), clazz);
    }

    public static <T,S> List<S> executeSql(Class<T> clazz, String sql, Class<S> mappedTo) {
        Query q = em.createNativeQuery(sql, clazz);
        return Utility.map(q.getResultList(), mappedTo);
    }

    public static <T> List<T> executeSql(Class<T> clazz, String sql) {
        Query q = em.createNativeQuery(sql, clazz);
        return Utility.map(q.getResultList(), clazz);
    }

    public static <T, S> S executeSqlSingle(Class<T> clazz, String sql, Class<S> mappedTo) {
        Query q = em.createNativeQuery(sql, clazz);
        return Utility.map(q.getSingleResult(), mappedTo);
    }

    public static <T> T executeSqlSingle(Class<T> clazz, String sql) {
        Query q = em.createNativeQuery(sql, clazz);
        return Utility.map(q.getSingleResult(), clazz);
    }

    public static <T> List<T> findList(Class<T> clazz, Filter filter) {
        String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
        sql += filter.getSql();
        TypedQuery<T> q = em.createQuery(sql, clazz);
        addParam(q, filter);
        return q.getResultList();
    }

    public static <T> List<T> findList(Class<T> clazz, Filter filter, Param param) {
        String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
        sql += filter.getSql() + getOrder(param);
        TypedQuery<T> q = em.createQuery(sql, clazz);
        addParam(q, filter);
        q.setFirstResult(param.getOffset()).setMaxResults(param.getSize());
        return q.getResultList();
    }

    public static <T> List<T> findList(Class<T> clazz, Filter filter, String sort) {
        String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
        sql += filter.getSql() + " ORDER BY " + sort;
        TypedQuery<T> q = em.createQuery(sql, clazz);
        addParam(q, filter);
        return q.getResultList();
    }

    /**
     * creator a query with limit, without relying on the Param Object
     *
     * @param clazz
     * @param filter
     * @param limit
     * @param <T>
     * @return
     */
    public static <T> List<T> findList(Class<T> clazz, Filter filter, int limit) {
        String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
        sql += filter.getSql();
        TypedQuery<T> q = em.createQuery(sql, clazz);
        addParam(q, filter);
        q.setMaxResults(limit);
        return q.getResultList();
    }

    public static <T> Long count(Class<T> clazz, Filter filter) {
        String sql = "SELECT COUNT(t) FROM " + clazz.getSimpleName() + " t ";
        sql += filter.getSql();
        TypedQuery<Long> q = em.createQuery(sql, Long.class);
        addParam(q, filter);
        return q.getSingleResult();
    }

    private static <T> void addParam(TypedQuery<T> q, Filter filter) {
        filter.getParams().forEach((key, value) -> {
            if (value instanceof Date) {
                q.setParameter(key, value);
            } else {
                q.setParameter(key, value);
            }
        });
    }

    private static String getOrder(Param param) {
        return StringUtils.hasText(param.getSort()) ? " ORDER BY " + param.getSort() : "";
    }

    public static <T> T findLast(Class<T> clazz) {
        try {
            String sql = "SELECT t FROM " + clazz.getSimpleName() + " t order by t.id desc ";
            TypedQuery<T> q = em.createQuery(sql, clazz);
            q.setMaxResults(1);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> Optional<T> findOne(Class<T> clazz, Filter filter, String sort) {

        String sql = "SELECT t FROM " + clazz.getSimpleName() + " t ";
        sql += filter.getSql() + " ORDER BY " + sort;
        TypedQuery<T> q = em.createQuery(sql, clazz);
        addParam(q, filter);
        q.setMaxResults(1);
        List<T> list = q.getResultList();

        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }

        return Optional.ofNullable(list.get(0));
    }

    public static <T> Long lastId(Class<T> clazz) {
        //try{
        String sql = "SELECT max(t.id) FROM " + clazz.getSimpleName() + " t ";
        Query q = em.createQuery(sql);
        Long max = (Long) q.getSingleResult();
        if (max == null) {
            return 0l;
        }
        return max;
    }
}
