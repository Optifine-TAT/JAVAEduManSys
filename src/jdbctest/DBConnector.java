package jdbctest;

import com.mysql.cj.protocol.a.NativeConstants;

import java.io.ObjectStreamException;
import java.sql.*;
import java.util.Objects;
import java.util.Vector;

public class DBConnector {
    // MySQL 8.0 以上版本 - JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/educationalmanagementdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "1240863915gg";
    Connection conn = null;
    Statement stmt = null;

    public DBConnector(){
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
        } catch(Exception se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }// 处理 Class.forName 错误


    }

    public boolean login(String mode, int profile, int password) throws SQLException {
        if (Objects.equals(mode, "admin")){
            return profile == 7777777 && password == 7777777;
        }
        String sql;
        ResultSet rs;
        sql = "select "+mode+"_password from "+mode+" where "+mode+"_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setObject(1, profile);
        rs = ps.executeQuery();
        rs.next();
        int pwd;
        if (Objects.equals(mode, "student")) {
            pwd = rs.getInt("student_password");
        }
        else{
            pwd = rs.getInt("teacher_password");
        }
        rs.close();
        return pwd == password;
    }

    public Object[][] search(String mode, int[] int_args, String[] str_args) throws CustomException, SQLException {
        Object[][] result;
        Vector<Vector<Object>> tmp = new Vector<>();
        String sql;
        ResultSet rs;
        switch (mode) {
            case "学生成绩查询":
                if(int_args.length != 1 || str_args.length != 0){
                    throw new CustomException("输入参数个数不正确"+int_args.length+str_args.length);
                }
                sql = "select course.course_order,course.course_name,course.teacher_id,teacher.teacher_name,course.course_time,course.course_credit, score.score\n" +
                        "from score,course,teacher\n" +
                        "where score.course_id=course.course_id and score.student_id='"+int_args[0]+"' and teacher.teacher_id=course.teacher_id;\n";
                rs = stmt.executeQuery(sql);
                while(rs.next())
                {
                    int course_order = rs.getInt("course_order");
                    String course_name = rs.getString("course_name");
                    int teacher_id = rs.getInt("teacher_id");
                    String course_time = rs.getString("course_time");
                    int course_credit = rs.getInt("course_credit");
                    int score = rs.getInt("score");
                    Vector<Object> row = new Vector<>();
                    row.addElement(course_order);
                    row.addElement(course_name);
                    row.addElement(teacher_id);
                    row.addElement(course_time);
                    row.addElement(course_credit);
                    row.addElement(score);
                    tmp.addElement(row);
                }
                rs.close();
            case "课程成绩查询":
                if(int_args.length != 2 || str_args.length != 0){
                    throw new CustomException("输入参数个数不正确"+int_args.length+str_args.length);
                }
                sql = "select course.course_order,course.course_name,course.course_time,course.course_credit,score.student_id,student.student_name,score.score\n" +
                        "from score,course,student\n" +
                        "where course.course_order = '"+int_args[0]+"' and course.teacher_id ='"+int_args[1]+"' and score.course_id=course.course_id and score.student_id=student.student_id\n" +
                        "order by score.score;\n";
                rs = stmt.executeQuery(sql);
                while(rs.next())
                {
                    int course_order = rs.getInt("course_order");
                    String course_name = rs.getString("course_name");
                    String course_time = rs.getString("course_time");
                    int course_credit = rs.getInt("course_credit");
                    int student_id = rs.getInt("student_id");
                    String student_name = rs.getString("student_name");
                    int score = rs.getInt("score");
                    Vector<Object> row = new Vector<>();
                    row.addElement(course_order);
                    row.addElement(course_name);
                    row.addElement(course_time);
                    row.addElement(course_credit);
                    row.addElement(student_id);
                    row.addElement(student_name);
                    row.addElement(score);
                    tmp.addElement(row);
                }
                rs.close();
            default:
                if (tmp.size() == 0){
                    throw new CustomException("查询无结果");
                }
                //asd
                int row = tmp.size();
                int col = tmp.get(0).size();
                result = new Object[row][col];
                //读取数据库

                for(int i=0;i<row;i++)
                {
                    for(int j=0;j<col;j++)
                    {
                        result[i][j] = ((Vector<?>)tmp.get(i)).get(j);
                    }
                }
        }
        return result;
    }

}



