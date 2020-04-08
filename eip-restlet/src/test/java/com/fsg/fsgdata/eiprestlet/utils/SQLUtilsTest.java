package com.fsg.fsgdata.eiprestlet.utils;

import com.fsg.fsgdata.eiprestlet.exceptions.IllegalSQLException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SQLUtilsTest {

    @Test
    void sqlValidate() throws IllegalSQLException {
        String sql = SQLUtils.sqlValidate("SELECT id, name, age FROM user", new HashSet<>(), "id=1");

        assertEquals("SELECT id, name, age FROM user WHERE id = 1", sql);
    }
}