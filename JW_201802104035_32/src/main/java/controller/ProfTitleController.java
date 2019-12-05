package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import domain.ProfTitle;
import helper.JSONUtil;
import service.ProfTitleService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/profTitle.ctl")
public class ProfTitleController extends HttpServlet {
    //请使用以下JSON测试增加功能（id为空）
    //{"description":"id为null的新职称","no":"05","remarks":""}
    //请使用以下JSON测试修改功能
    //{"description":"修改id=1的职称","id":1,"no":"05","remarks":""}

    /**
     * POST, http://172.81.254.198:8080/bysj/profTitle.ctl, 增加职称
     * 增加一个职称对象：将来自前端请求的JSON对象，增加到数据库表中
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
        String profTitle_json = JSONUtil.getJSON(request);

        //将JSON字串解析为ProfTitle对象
        ProfTitle profTitleToAdd = JSON.parseObject(profTitle_json, ProfTitle.class);
        //前台没有为id赋值，此处模拟自动生成id。如果Dao能真正完成数据库操作，删除下一行。
        //profTitleToAdd.setId(4 + (int)(Math.random()*100));

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //在数据库表中增加ProfTitle对象
        try {
            ProfTitleService.getInstance().add(profTitleToAdd);
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
     * DELETE, http://172.81.254.198:8080/bysj/profTitle.ctl?id=1, 删除id=1的职称
     * 删除一个职称对象：根据来自前端请求的id，删除数据库表中id的对应记录
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

        //到数据库表中删除对应的职称
        try {
            boolean deleted = ProfTitleService.getInstance().delete(id);
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
     * PUT, http://172.81.254.198:8080/bysj/profTitle.ctl, 修改职称
     *
     * 修改一个职称对象：将来自前端请求的JSON对象，更新数据库表中相同id的记录
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
        String profTitle_json = JSONUtil.getJSON(request);
        //将JSON字串解析为ProfTitle对象
        ProfTitle profTitleToUpdate = JSON.parseObject(profTitle_json, ProfTitle.class);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //到数据库表修改ProfTitle对象对应的记录
        try {
            boolean updated = ProfTitleService.getInstance().update(profTitleToUpdate);
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
     * GET, http://172.81.254.198:8080/bysj/profTitle.ctl?id=1, 查询id=1的职称
     * GET, http://172.81.254.198:8080/bysj/profTitle.ctl, 查询所有的职称
     * 把一个或所有职称对象响应到前端
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
            //如果id = null, 表示响应所有职称对象，否则响应id指定的职称对象
            if (id_str == null) {
                responseProfTitles(response);
            } else {
                int id = Integer.parseInt(id_str);
                responseProfTitle(id, response);
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
    //响应一个职称对象
    private void responseProfTitle(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找职称
        ProfTitle profTitle = ProfTitleService.getInstance().find(id);
        String profTitle_json = JSON.toJSONString(profTitle);

        /**
         *         //创建JSON对象message，以便往前端响应信息
         *         JSONObject message = new JSONObject();
         *         //加入数据信息
         *         message.put("statusCode", "200");
         *         message.put("message", "查找成功");
         *         message.put("data", profTitle_json);
         *
         *         //响应message到前端
         *         response.getWriter().println(message);*/

        //响应profTitle_json到前端
        response.getWriter().println(profTitle_json);
    }
    //响应所有职称对象
    private void responseProfTitles(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有职称
        Collection<ProfTitle> profTitles = ProfTitleService.getInstance().findAll();
        String profTitles_json = JSON.toJSONString(profTitles, SerializerFeature.DisableCircularReferenceDetect);

        /**
         *         //创建JSON对象message，以便往前端响应信息
         *         JSONObject message = new JSONObject();
         *         //加入数据信息
         *         message.put("statusCode", "200");
         *         message.put("message", "查找成功");
         *         message.put("data", profTitles_json);
         *
         *         //响应message到前端
         *         response.getWriter().println(message);*/

        //响应profTitles_json到前端
        response.getWriter().println(profTitles_json);
    }
}
