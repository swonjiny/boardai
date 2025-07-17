package org.zerock.board.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Custom P6Spy SQL formatter that formats SQL queries with line breaks and indentation
 * for better readability in logs.
 */
public class P6SpyPrettySqlFormatter implements MessageFormattingStrategy {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String DOUBLE_SPACE = "  ";

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category,
                               String prepared, String sql, String url) {
        StringBuilder sb = new StringBuilder();

        // Format timestamp
        sb.append(formatDateTime(new Date()))
          .append(" | ")
          .append(elapsed)
          .append("ms | ")
          .append("Connection ID: ")
          .append(connectionId);

        if (category.equals(Category.STATEMENT.getName())) {
            sb.append(" | ");

            // Format the SQL with line breaks and indentation
            String formattedSql = formatSql(sql);
            sb.append(NEW_LINE).append(formattedSql);
        } else {
            sb.append(" | ").append(category);
            if (StringUtils.hasText(sql)) {
                sb.append(" | ").append(sql);
            }
        }

        return sb.toString();
    }

    private String formatSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }

        // Format different types of SQL statements
        String trimmedSQL = sql.trim().toLowerCase(Locale.ROOT);

        if (trimmedSQL.startsWith("select") ||
            trimmedSQL.startsWith("update") ||
            trimmedSQL.startsWith("insert") ||
            trimmedSQL.startsWith("delete")) {

            // Apply custom formatting based on SQL type
            String formattedSql = sql.trim();

            // Apply formatting with line breaks and indentation
            formattedSql = formatSqlWithLineBreaks(formattedSql);

            return formattedSql;
        }

        return sql;
    }

    private String formatSqlWithLineBreaks(String sql) {
        // Convert SQL keywords to uppercase for better readability
        String result = sql;

        // Add line breaks and indentation for better readability
        result = addLineBreaksForClauses(result);

        // Format specific SQL parts
        if (result.toUpperCase(Locale.ROOT).startsWith("SELECT")) {
            result = formatSelectStatement(result);
        } else if (result.toUpperCase(Locale.ROOT).startsWith("INSERT")) {
            result = formatInsertStatement(result);
        } else if (result.toUpperCase(Locale.ROOT).startsWith("UPDATE")) {
            result = formatUpdateStatement(result);
        } else if (result.toUpperCase(Locale.ROOT).startsWith("DELETE")) {
            result = formatDeleteStatement(result);
        }

        return result;
    }

    private String addLineBreaksForClauses(String sql) {
        // Replace SQL clauses with line breaks and indentation
        return sql.replaceAll("(?i)\\s+FROM\\s+", NEW_LINE + DOUBLE_SPACE + "FROM ")
                  .replaceAll("(?i)\\s+WHERE\\s+", NEW_LINE + DOUBLE_SPACE + "WHERE ")
                  .replaceAll("(?i)\\s+LEFT\\s+JOIN\\s+", NEW_LINE + DOUBLE_SPACE + "LEFT JOIN ")
                  .replaceAll("(?i)\\s+RIGHT\\s+JOIN\\s+", NEW_LINE + DOUBLE_SPACE + "RIGHT JOIN ")
                  .replaceAll("(?i)\\s+INNER\\s+JOIN\\s+", NEW_LINE + DOUBLE_SPACE + "INNER JOIN ")
                  .replaceAll("(?i)\\s+JOIN\\s+", NEW_LINE + DOUBLE_SPACE + "JOIN ")
                  .replaceAll("(?i)\\s+GROUP\\s+BY\\s+", NEW_LINE + DOUBLE_SPACE + "GROUP BY ")
                  .replaceAll("(?i)\\s+HAVING\\s+", NEW_LINE + DOUBLE_SPACE + "HAVING ")
                  .replaceAll("(?i)\\s+ORDER\\s+BY\\s+", NEW_LINE + DOUBLE_SPACE + "ORDER BY ")
                  .replaceAll("(?i)\\s+LIMIT\\s+", NEW_LINE + DOUBLE_SPACE + "LIMIT ")
                  .replaceAll("(?i)\\s+ON\\s+", NEW_LINE + DOUBLE_SPACE + DOUBLE_SPACE + "ON ")
                  .replaceAll("(?i)\\s+AND\\s+", NEW_LINE + DOUBLE_SPACE + DOUBLE_SPACE + "AND ")
                  .replaceAll("(?i)\\s+OR\\s+", NEW_LINE + DOUBLE_SPACE + DOUBLE_SPACE + "OR ");
    }

    private String formatSelectStatement(String sql) {
        // Format SELECT statements with line breaks for column lists
        Pattern pattern = Pattern.compile("(?i)(SELECT\\s+)(.+?)(\\s+FROM)");
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            String selectClause = matcher.group(2);
            // Add line breaks after commas in the SELECT clause
            String formattedSelectClause = selectClause.replaceAll(",\\s*", "," + NEW_LINE + DOUBLE_SPACE);

            return sql.substring(0, matcher.start(2)) +
                   NEW_LINE + DOUBLE_SPACE + formattedSelectClause +
                   sql.substring(matcher.end(2));
        }

        return sql;
    }

    private String formatInsertStatement(String sql) {
        // Format INSERT statements
        sql = sql.replaceAll("(?i)(INSERT\\s+INTO\\s+[^\\s(]+)(\\s*\\()", "$1" + NEW_LINE + DOUBLE_SPACE + "(");
        sql = sql.replaceAll("(?i)\\)\\s*VALUES\\s*\\(", ")" + NEW_LINE + DOUBLE_SPACE + "VALUES (");

        // Format column lists and values
        sql = sql.replaceAll(",\\s*", "," + NEW_LINE + DOUBLE_SPACE);

        return sql;
    }

    private String formatUpdateStatement(String sql) {
        // Format UPDATE statements
        sql = sql.replaceAll("(?i)(UPDATE\\s+[^\\s]+\\s+)(SET\\s+)", "$1" + NEW_LINE + DOUBLE_SPACE + "$2");

        // Format SET clause with line breaks
        Pattern pattern = Pattern.compile("(?i)(SET\\s+)(.+?)(\\s+WHERE|$)");
        Matcher matcher = pattern.matcher(sql);

        if (matcher.find()) {
            String setClause = matcher.group(2);
            // Add line breaks after commas in the SET clause
            String formattedSetClause = setClause.replaceAll(",\\s*", "," + NEW_LINE + DOUBLE_SPACE + DOUBLE_SPACE);

            return sql.substring(0, matcher.start(2)) +
                   formattedSetClause +
                   sql.substring(matcher.end(2));
        }

        return sql;
    }

    private String formatDeleteStatement(String sql) {
        // Format DELETE statements (similar to SELECT but simpler)
        return sql.replaceAll("(?i)(DELETE\\s+FROM\\s+[^\\s]+)(\\s+WHERE|$)", "$1" + NEW_LINE + DOUBLE_SPACE + "$2");
    }

    private String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(date);
    }
}
