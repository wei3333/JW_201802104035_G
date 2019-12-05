package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import domain.Department;
import helper.JSONUtil;
import service.DepartmentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/department.ctl")
public class DepartmentController extends HttpServlet {
    //请使用以下JSON测试增加功能（id为空）
    //{"description":"id为null的新系","no":"05","remarks":""}
    //请使用以下JSON测试修改功能
    //{"description":"修改id=1的系","id":1,"no":"05","remarks":""}

    /**
     * POST, http://172.81.254.198:8080/bysj/department.ctl, 增加系
     * 增加一个系对象：将来自前端请求的JSON对象，增加到数据库表中
     * @param request 请求对象
     * @param response 响应对象
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        //根据request对象，获得代表参数的JSON字串
        String department_json = JSONUtil.getJSON(request);

        //将JSON字串解析为Department对象
        Department departmentToAdd = JSON.parseObject(department_json, Department.class);
        //前台没有为id赋值，此处模拟自动生成id。如果Dao能真正完成数据库操作，删除下一行。
        //departmentToAdd.setId(4 + (int)(Math.random()*100));

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //在数据库表中增加Department对象
        try {
            DepartmentService.getInstance().add(departmentToAdd);
            message.put("message", "增加成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * DELETE, http://172.81.254.198:8080/bysj/department.ctl?id=1, 删除id=1的系
     * 删除一个系对象：根据来自前端请求的id，删除数据库表中id的对应记录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //读取参数id
        String id_str = request.getParameter("id");
        int id = Integer.parseInt(id_str);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();

        //到数据库表中删除对应的系
        try {
            boolean deleted = DepartmentService.getInstance().delete(id);
            if(deleted){
                message.put("message", "删除成功");
            }else
                message.put("message","未能删除");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }


    /**
     * PUT, http://172.81.254.198:8080/bysj/department.ctl, 修改系
     *
     * 修改一个系对象：将来自前端请求的JSON对象，更新数据库表中相同id的记录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        String department_json = JSONUtil.getJSON(request);
        //将JSON字串解析为Department对象
        Department departmentToUpdate = JSON.parseObject(department_json, Department.class);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //到数据库表修改Department对象对应的记录
        try {
            boolean updated = DepartmentService.getInstance().update(departmentToUpdate);
            if(updated){
                message.put("message", "修改成功");
            }else
                message.put("message","未能修改");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * GET, http://172.81.254.198:8080/bysj/department.ctl?id=1, 查询id=1的系
     * GET, http://172.81.254.198:8080/bysj/department.ctl, 查询所有的系
     * 把一个或所有系对象响应到前端
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //读取参数id
        String id_str = request.getParameter("id");
        //读取请求类型
        String school = request.getParameter("paraType");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        try {
            //如果id = null, 表示响应所有系对象，否则响应id指定的系对象
            if (id_str == null) {
                responseDepartments(response);
            } else if(school == null){
                int id = Integer.parseInt(id_str);
                responseDepartment(id, response);
            }else {
                int id = Integer.parseInt(id_str);
                responseDepartmentsBySchool(id,response);
            }
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            //响应message到前端
            response.getWriter().println(message);
        }catch(Exception e){
            message.put("message", "网络异常");
            //响应message到前端
            response.getWriter().println(message);
        }
    }
    //响应一个系对象
    private void responseDepartment(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找系
        Department department = DepartmentService.getInstance().find(id);
        String department_json = JSON.toJSONString(department);

        /**
         *         //创建JSON对象message，以便往前端响应信息
         *         JSONObject message = new JSONObject();
         *         //加入数据信息
         *         message.put("statusCode", "200");
         *         message.put("message", "查找成功");
         *         message.put("data", department_json);
         *
         *         //响应message到前端
         *         response.getWriter().println(message);*/

        //响应department_json到前端
        response.getWriter().println(department_json);
    }
    //响应所有系对象
    private void responseDepartments(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有系
        Collection<Department> departments = DepartmentService.getInstance().findAll();
        String departments_json = JSON.toJSONString(departments, SerializerFeature.DisableCircularReferenceDetect);

        /**
         *         //创建JSON对象message，以便往前端响应信息
         *         JSONObject message = new JSONObject();
         *         //加入数据信息
         *         message.put("statusCode", "200");
         *         message.put("message", "查找成功");
         *         message.put("data", departments_json);
         *
         *         //响应message到前端
         *         response.getWriter().println(message);*/

        //响应departments_json到前端
        response.getWriter().println(departments_json);
    }

    //相应由学院ID获取的对应系对象集合
    private void responseDepartmentsBySchool(int schoolId, HttpServletResponse response) throws ServletException, IOException, SQLException{
        Collection<Department>departments = DepartmentService.getInstance().findAllBySchool(schoolId);
        String departments_json = JSON.toJSONString(departments, SerializerFeature.DisableCircularReferenceDetect);
        /**
         *         //创建JSON对象message，以便往前端响应信息
         *         JSONObject message = new JSONObject();
         *         //加入数据信息
         *         message.put("statusCode", "200");
         *         message.put("message", "查找成功");
         *         message.put("data", departments_json);
         *
         *         //响应message到前端
         *         response.getWriter().println(message);*/
        response.getWriter().println(departments_json);
    }
}
