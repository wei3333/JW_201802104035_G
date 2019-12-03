package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import domain.User;
import helper.JSONUtil;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

//login和changePassword放在doPost里面
@WebServlet("/user.ctl")
public class UserController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        //读取参数id
        String id_str = request.getParameter("id");
        String login = request.getParameter("paraType");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        try {
            //如果id = null, 表示响应所有用户对象，否则响应id指定的用户对象
            if (id_str == null) {
                responseUsers(response);
            } else {
                int id = Integer.parseInt(id_str);
                responseUser(id, response);
            }
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            response.getWriter().println(message);
            e.printStackTrace();
        }catch(Exception e){
            message.put("message", "网络异常");
            e.printStackTrace();
            response.getWriter().println(message);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        String user_json = JSONUtil.getJSON(request);
        //将JSON字串解析为User对象
        User userToUpdate = JSON.parseObject(user_json, User.class);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //到数据库表修改User对象对应的记录
        try {
            boolean updated = UserService.getInstance().changePassword(userToUpdate);
            if(updated){
                message.put("message", "修改成功");
            }else
                message.put("message","未能修改");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            e.printStackTrace();
        }catch(Exception e){
            message.put("message", "网络异常");
            e.printStackTrace();
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    private void responseUser(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找用户
        User user = UserService.getInstance().find(id);
        String user_json = JSON.toJSONString(user);

        /**
         *         //创建JSON对象message，以便往前端响应信息
         *         JSONObject message = new JSONObject();
         *         //加入数据信息
         *         message.put("statusCode", "200");
         *         message.put("message", "查找成功");
         *         message.put("data", teacher_json);
         *
         *         //响应message到前端
         *         response.getWriter().println(message);*/

        //响应user_json到前端
        response.getWriter().println(user_json);
    }
    //响应所有用户对象
    private void responseUsers(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有用户
        Collection<User> users = UserService.getInstance().findAll();
        String users_json = JSON.toJSONString(users, SerializerFeature.DisableCircularReferenceDetect);

        /**
         *         //创建JSON对象message，以便往前端响应信息
         *         JSONObject message = new JSONObject();
         *         //加入数据信息
         *         message.put("statusCode", "200");
         *         message.put("message", "列表成功");
         *         message.put("data", teachers_json);
         *
         *         //响应message到前端
         *         response.getWriter().println(message);*/

        //响应users_json到前端
        response.getWriter().println(users_json);
    }
}
