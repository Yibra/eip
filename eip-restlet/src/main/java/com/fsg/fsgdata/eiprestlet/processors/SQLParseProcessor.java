package com.fsg.fsgdata.eiprestlet.processors;

import com.fsg.fsgdata.eiprestlet.entities.ResourceSql;
import com.fsg.fsgdata.eiprestlet.exceptions.IllegalSQLException;
import com.fsg.fsgdata.eiprestlet.repositories.ResourceSqlRepository;
import com.fsg.fsgdata.eiprestlet.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component("sqlParseProcessor")
public class SQLParseProcessor {
    private static Logger log = LoggerFactory.getLogger(SQLParseProcessor.class);

    @Autowired
    private ResourceSqlRepository resourceSqlRepository;

    public String makeSQL(String resource, String filterCondition)
            throws IllegalSQLException, IOException {
        String where =
                URLDecoder.decode(filterCondition.substring("filterCondition=".length()),
                        StandardCharsets.UTF_8.name());
        log.debug(where);

        List<ResourceSql> result = resourceSqlRepository.findByResource(resource);
        if (result.size() == 1) {
            ResourceSql resourceSql = result.get(0);
            String baseSql = resourceSql.getSelectStatement();

            Set<String> limits = new HashSet<>();
            if (resourceSql.getLimits() != null) {
                limits = Arrays.stream(resourceSql.getLimits().split(",")).collect(Collectors.toSet());
            }
            String sql = SQLUtils.sqlValidate(baseSql, limits, where);
            return sql;
        } else  {
            throw new IllegalSQLException("resource " + resource + " not found.");
        }
    }


}
