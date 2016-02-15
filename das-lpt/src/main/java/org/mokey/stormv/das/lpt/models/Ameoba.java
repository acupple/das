package org.mokey.stormv.das.lpt.models;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wcyuan on 2015/3/12.
 */
public class Ameoba extends Model {
    private int id;
    private String name;
    private byte[] content;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public static Ameoba parseFromJDBC(ResultSet rs) throws SQLException{
        Ameoba ameoba = new Ameoba();
        ameoba.setId(rs.getInt("id"));
        ameoba.setName(rs.getString("name"));
        ameoba.setContent(rs.getBytes("content"));
        return ameoba;
    }

    @Override
    public int size() {
        return 4 + (name == null ? 0 : name.getBytes().length) + (content == null ? 0 : content.length);
    }
}
