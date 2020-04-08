package com.fsg.fsgdata.eiprestlet.utils;

import com.fsg.fsgdata.eiprestlet.exceptions.IllegalSQLException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class SQLUtils {
    private static Logger log = LoggerFactory.getLogger(SQLUtils.class);

    public static boolean sqlParse(String sql, Set<String> checkClumns) throws JSQLParserException {
        if (checkClumns.size() == 0) {
            return true;
        }
        Statement stmt = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) stmt;

        SelectBody selectBody = selectStatement.getSelectBody();
        PlainSelect plainSelect = (PlainSelect) selectBody;

        Expression where = plainSelect.getWhere();

        Set<String> result = parseExpression(where, checkClumns, 0);

        return result.size() < checkClumns.size();
    }

    private static Set<String> parseExpression(Expression exp, Set<String> checkColumns, int depth) {
        if (exp instanceof AndExpression) {
            AndExpression andExp = (AndExpression) exp;
            Set<String> rsLeft = parseExpression(andExp.getLeftExpression(), checkColumns, depth + 1);
            Set<String> rsRight = parseExpression(andExp.getRightExpression(), checkColumns, depth + 1);
            return rsLeft.size() > rsRight.size() ? rsRight : rsLeft;
        } else if (exp instanceof OrExpression) {
            OrExpression orExp = (OrExpression) exp;
            Set<String> rsLeft = parseExpression(orExp.getLeftExpression(), checkColumns, depth + 1);
            Set<String> rsRight = parseExpression(orExp.getRightExpression(), checkColumns, depth + 1);
            return rsLeft.size() > rsRight.size() ? rsLeft : rsRight;
        } else if (exp instanceof NotExpression) {
            NotExpression notExp = (NotExpression) exp;
            return parseExpression(notExp.getExpression(), checkColumns, depth + 1);
        } else if (exp instanceof BinaryExpression) {
            BinaryExpression bExp = (BinaryExpression) exp;
            Set<String> rsLeft = parseExpression(bExp.getLeftExpression(), checkColumns, depth + 1);
            Set<String> rsRight = parseExpression(bExp.getRightExpression(), checkColumns, depth + 1);
            return rsLeft.size() > rsRight.size() ? rsRight : rsLeft;
        } else if (exp instanceof Column) {
            Column col = (Column) exp;
            return checkColumns.stream().filter(c -> !c.equalsIgnoreCase(col.getColumnName())).collect(Collectors.toSet());
        } else if (exp instanceof Parenthesis) {
            Parenthesis par = (Parenthesis) exp;
            return parseExpression(par.getExpression(), checkColumns, depth + 1);
        } else if (exp instanceof Between) {
            Between between = (Between) exp;
            return parseExpression(between.getLeftExpression(), checkColumns, depth + 1);
        } else if (exp instanceof Function) {
            Function fun = (Function) exp;
            List<Expression> list = fun.getParameters().getExpressions();
            Set<String> checkItems = new HashSet<>(checkColumns);
            for (Expression aList : list) {
                checkItems = parseExpression(aList, checkItems, depth + 1);
            }
            return checkItems;
        }

        log.debug(exp.getClass().getName());
        return checkColumns;
    }

    public static String sqlValidate(String sql, Set<String> limits, String whereSql) throws IllegalSQLException {
        try {
            Statement stmt = CCJSqlParserUtil.parse(sql);
            Select selectStatement = (Select) stmt;
            SelectBody selectBody = selectStatement.getSelectBody();
            PlainSelect plainSelect = (PlainSelect) selectBody;

            Map<String, Column> columns = new HashMap<>();
            TablesNamesFinder finder = new TablesNamesFinder() {
                @Override
                public void visit(Column tableColumn) {
                    columns.put(tableColumn.getColumnName().toUpperCase(), tableColumn);
                    super.visit(tableColumn);
                }
            };
            finder.getTableList(stmt);
            List<SelectItem> items = plainSelect.getSelectItems();
            for (SelectItem item1 : items) {
                SelectExpressionItem item = (SelectExpressionItem) item1;

                Expression col = item.getExpression();
                Expression expr = maskColumn(col, limits);
                if (expr != col && item.getAlias() == null && col instanceof Column) {
                    item.setAlias(new Alias(((Column) col).getColumnName()));
                }
                if (col instanceof Column && item.getAlias() != null) {
                    columns.put(item.getAlias().getName().toUpperCase(), (Column)col);
                }
                item.setExpression(expr);
            }

            if (!StringUtils.isBlank(whereSql)) {
                Expression where = plainSelect.getWhere();
                Expression expr = CCJSqlParserUtil.parseCondExpression(whereSql);

                final boolean[] hasLimitColumn = {false};
                ExpressionVisitor visitor = new ExpressionVisitorAdapter() {
                    @Override
                    public void visit(Column column) {
                        if (columns.containsKey(column.getColumnName().toUpperCase())) {
                            column.setTable(columns.get(column.getColumnName().toUpperCase()).getTable());
                            column.setColumnName(columns.get(column.getColumnName().toUpperCase()).getColumnName());
                        }
                        if (limits.contains(column.getColumnName().toUpperCase())) {
                            hasLimitColumn[0] = true;
                        }
                        super.visit(column);
                    }
                };
                expr.accept(visitor);
                if (hasLimitColumn[0]) {
                    throw new IllegalSQLException(
                            "There is limit column in the filter condition. limits:" +
                                    StringUtils.join(limits, ", "));
                }
                if (where == null) {
                    plainSelect.setWhere(expr);
                }
                else {
                    plainSelect.setWhere(new AndExpression(where, expr));
                }
            }

            return plainSelect.toString();
        } catch (JSQLParserException ex) {
            throw new IllegalSQLException("Invalidate filter condition format.");
        }
    }

    private static Expression maskColumn(Expression expr, Set<String> limits) {
        if (expr instanceof Column) {
            String columnName = ((Column) expr).getColumnName().toUpperCase();
            if (limits.contains(columnName)) {
                return new StringValue(Consts.MASK_DATA);
            }
        } else if (expr instanceof Function) {
            Function fun = (Function) expr;
            List<Expression> list = fun.getParameters().getExpressions();
            for (int i = 0; i < list.size(); i++) {
                list.set(i, maskColumn(list.get(i), limits));
            }
        } else if (expr instanceof Parenthesis) {
            Parenthesis par = (Parenthesis) expr;
            par.setExpression(maskColumn(par.getExpression(), limits));
        } else if (expr instanceof BinaryExpression) {
            BinaryExpression bExp = (BinaryExpression) expr;
            Expression left = maskColumn(bExp.getLeftExpression(), limits);
            Expression right = maskColumn(bExp.getRightExpression(), limits);
            bExp.setLeftExpression(left);
            bExp.setRightExpression(right);
        }
        return expr;
    }
}
