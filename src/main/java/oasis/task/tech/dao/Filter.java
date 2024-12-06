package oasis.task.tech.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Filter {

    private StringBuilder sql = new StringBuilder(" WHERE ");
    private Map<String, Object> params = new HashMap<>();

    public Field field(String name) {
        return new Field(name);
    }

    public Filter field(String name, Object value) {
        return new Field(name).eq(value);
    }

    public Filter or() {
        sql.append(" OR ");
        return this;
    }

    public Filter brS() {
        sql.append(" ( ");
        return this;
    }

    public Filter brE() {
        sql.append(" ) AND ");
        return this;
    }

    public String getSql() {
        String s = sql.toString();
        s = s.trim();
        if(s.endsWith("AND"))
            s = s.substring(0, s.length()-3);
        if(s.endsWith("OR"))
            s = s.substring(0, s.length()-2);
        if(s.endsWith("WHERE"))
            s = s.substring(0, s.length()-5);
        s = s.replaceAll("AND +OR", "OR")
                .replaceAll("AND +\\)", ")")
                .replaceAll("OR +\\)", ")");
        return s;
    }

    public Map<String, Object> getParams() {
        return params;
    }


    private Filter() {}

    private Filter(StringBuilder sql, Map<String, Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public Filter copy() {
        return new Filter(new StringBuilder(sql.toString()), new HashMap<>(params));
    }


    public class Field {
        private String name;
        public Field(String name) {
            this.name = name;
        }

        public Filter eq(Object value) {
            sql.append(name).append(" = :").append(key()).append(" AND ");
            params.put(key(), value);
            return Filter.this;
        }
        public Filter ne(Object value) {
            String key = key() + value.toString().replaceAll("[^A-Za-z]", "");
            sql.append(name).append(" != :").append(key).append(" AND ");
            params.put(key, value);
            return Filter.this;
        }
        public Filter like(String value) {
            sql.append(name).append(" LIKE :").append(key()).append(" AND ");
            params.put(key(), "%"+value+"%");
            return Filter.this;
        }

        public Filter testLike(String value) {
            sql.append(name).append(" LIKE :").append(key()).append(" AND ");
            params.put(key(), "%mysql_real_escape_string("+value+")%");
            return Filter.this;
        }

        public Filter gt(Object value) {
            sql.append(name).append(" > :").append(key()).append(" AND ");
            params.put(key(), value);
            return Filter.this;
        }
        public Filter lt(Object value) {
            sql.append(name).append(" < :").append(key()).append(" AND ");
            params.put(key(), value);
            return Filter.this;
        }
        public Filter gte(Object value) {
            sql.append(name).append(" >= :").append(key()).append(" AND ");
            params.put(key(), value);
            return Filter.this;
        }
        public Filter lte(Object value) {
            sql.append(name).append(" <= :").append(key()).append(" AND ");
            params.put(key(), value);
            return Filter.this;
        }
        public Filter isNull() {
            //sql.append(name).append(" IS NULL AND ");
            sql.append(name).append(" IS NULL AND ");
            return Filter.this;
        }
        public Filter notNull() {
            sql.append(name).append(" IS NOT NULL AND ");
            return Filter.this;
        }
        public Filter contains(Object value) {
            sql.append(" '").append(value).append("' MEMBER OF ").append(name).append(" AND ");
            return Filter.this;
        }
        public Filter between(Object start, Object end) {
            String startKey = key()+"Start";
            String endKey = key()+"End";
            sql.append(name).append(" BETWEEN :").append(startKey).append(" AND :").append(endKey).append(" AND ");
            params.put(startKey, start);
            params.put(endKey, end);
            return Filter.this;
        }
        public Filter in(List<Object> array) {
            String key = key()+"Array";
            sql.append(name).append(" IN :").append(key).append(" AND ");
            params.put(key, array);
            return Filter.this;
        }

        public Filter in(Collection<Object> array) {
            String key = key()+"Array";
            sql.append(name).append(" IN :").append(key).append(" AND ");
            params.put(key, array);
            return Filter.this;
        }

        public Filter nin(List<Object> array) {
            String key = key()+"Array";
            sql.append(name).append(" NOT IN :").append(key).append(" AND ");
            params.put(key, array);
            return Filter.this;
        }
        private String key() {
            return name.replaceAll("[^A-Za-z]", "");
        }
    }

    public static Filter get() {
        return new Filter();
    }
}
