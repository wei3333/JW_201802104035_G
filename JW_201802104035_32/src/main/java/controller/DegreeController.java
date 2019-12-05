package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import domain.Degree;
import helper.JSONUtil;
import service.DegreeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/degree.ctl")
public class DegreeController extends HttpServlet {
    //请使用以下JSON测试增加功能（id为空）
    //{"description":"id为null的新学位","no":"05","remarks":""}
    //请使用以下JSON测试修改功能
    //{"description":"修改id=1的学位","id":1,"no":"05","remarks":""}

    /**
     * POST, http://172.81.254.198:8080/bysj/degree.ctl, 增加学位
     * 增加一个学位对象：将来自前端请求的JSON对象，增加到数据库表中
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
        String degree_json = JSONUtil.getJSON(request);

        //将JSON字串解析为Degree对象
        Degree degreeToAdd = JSON.parseObject(degree_json, Degree.class);
        //前台没有为id赋值，此处模拟自动生成id。如果Dao能真正完成数据库操作，删除下一行。
        //degreeToAdd.setId(4 + (int)(Math.random()*100));

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //在数据库表中增加Degree对象
        try {
            DegreeService.getInstance().add(degreeToAdd);
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
     * DELETE, http://172.81.254.198:8080/bysj/degree.ctl?id=1, 删除id=1的学位
     * 删除一个学位对象：根据来自前端请求的id，删除数据库表中id的对应记录
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

        //到数据库表中删除对应的学位
        try {
            boolean deleted = DegreeService.getInstance().delete(id);
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
     * PUT, http://172.81.254.198:8080/bysj/degree.ctl, 修改学位
     *
     * 修改一个学位对象：将来自前端请求的JSON对象，更新数据库表中相同id的记录
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
        String degree_json = JSONUtil.getJSON(request);
        //将JSON字串解析为Degree对象
        Degree degreeToUpdate = JSON.parseObject(degree_json, Degree.class);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //到数据库表修改Degree对象对应的记录
        try {
            boolean updated = DegreeService.getInstance().update(degreeToUpdate);
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
     * GET, http://172.81.254.198:8080/bysj/degree.ctl?id=1, 查询id=1的学位
     * GET, http://172.81.254.198:8080/bysj/degree.ctl, 查询所有的学位
     * 把一个或所有学位对象响应到前端
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

        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        try {
            //如果id = null, 表示响应所有学位对象，否则响应id指定的学位对象
            if (id_str == null) {
                responseDegrees(response);
            } else {
                int id = Integer.parseInt(id_str);
                responseDegree(id, response);
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
    //响应一个学位对象
    private void responseDegree(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找学位
        Degree degree = DegreeService.getInstance().find(id);
        String degree_json = JSON.toJSONString(degree);

        /**
         *         //创建JSON对象message，以便往前端响应信息
         *         JSONObject message = new JSONObject();
         *         //加入数据信息
         *         message.put("statusCode", "200");
         *         message.put("message", "查找成功");
         *         message.put("data", degree_json);
         *
         *         //响应message到前端
         *         response.getWriter().println(message);*/

        //响应degree_json到前端
        response.getWriter().println(degree_json);
    }
    //响应所有学位对象
    private void responseDegrees(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有学位
        Collection<Degree> degrees = DegreeService.getInstance().findAll();
        String degrees_json = JSON.toJSONString(degrees, SerializerFeature.DisableCircularReferenceDetect);
        /**
         *         //创建JSON对象message，以便往前端响应信息
         *         JSONObject message = new JSONObject();
         *         //加入数据信息
         *         message.put("statusCode", "200");
         *         message.put("message", "列表成功");
         *         message.put("data", degrees_json);
         *
         *         //响应message到前端
         *         response.getWriter().println(message);*/

        //响应degrees_json到前端
        response.getWriter().println(degrees_json);
    }
}
