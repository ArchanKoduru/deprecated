package com.gourmetoven.gourmetapp.repository;

import com.gourmetoven.gourmetapp.entity.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getUsernameForUserId(Integer userId) {
        String sql = "SELECT name_of_user from users where user_id =  ?";
        return jdbcTemplate.queryForObject(sql, new UserRowMapper(), userId);
    }

    public Integer getUserIdForUserName (String username) {
        String sql = "SELECT user_id from users where user_name =  ?";
        return jdbcTemplate.queryForObject(sql, new UserRowMapper2(), username);
    }

    private static class UserRowMapper implements RowMapper<String> {
        @Override
        public String mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getString("name_of_user");
        }
    }

    private static class UserRowMapper2 implements RowMapper<Integer> {
        @Override
        public Integer mapRow(ResultSet rs, int i) throws SQLException {
            return rs.getInt("user_id");
        }
    }
}
