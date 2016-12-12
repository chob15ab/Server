package security;

import service.DBWrapper;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class UserSecurityModel {
    public int id;
    public String role;

    public UserSecurityModel(String id) throws Exception {
        Map<String, String> params = new HashMap();
        params.put("id", String.valueOf(id));

        String[] attributes = {"type"};

        ResultSet rs = null;
        try {
            rs = DBWrapper.getRecords("user", attributes, params, null, 0);
            while (rs.next()) {
                this.id = Integer.parseInt(id);
                this.role = rs.getString("type");
            }
            if (this.role == "" || this.role.equals("")) {
                throw new Exception("User was not found!");
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
