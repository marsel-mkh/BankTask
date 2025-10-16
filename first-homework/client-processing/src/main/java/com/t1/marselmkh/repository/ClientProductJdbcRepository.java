package com.t1.marselmkh.repository;

import com.t1.marselmkh.entity.ProductKey;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ClientProductJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public Map<ProductKey, Long> countOpenedByType() {
        String sql = """
                    SELECT p.key AS product_key, COUNT(cp.id) AS cnt
                    FROM client_products cp
                    JOIN products p ON cp.product_id = p.product_id
                    WHERE cp.close_date IS NULL
                    GROUP BY p.key
                """;

        Map<ProductKey, Long> result = new EnumMap<>(ProductKey.class);

        jdbcTemplate.query(sql, rs -> {
            ProductKey key = ProductKey.valueOf(rs.getString("product_key"));
            long count = rs.getLong("cnt");
            result.put(key, count);
        });

        return result;
    }

}
