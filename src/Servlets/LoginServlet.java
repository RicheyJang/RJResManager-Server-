package Servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import ToolFunc.*;
import com.alibaba.fastjson.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pojo.*;

@WebServlet(name = "LoginServlet")
public class LoginServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json=DealServlet.getRequestJsonObject(request);
		String username=json.getString("username");
		String password=json.getString("password");
		if(!SubFunc.checkUsername(username))
		{
			response.setStatus(401);
			return;
		}
		System.out.println("got it");
		System.out.println(json.toString());

		Session session= HibernateFactory.getSession();
		String hql="from User where username=:hisname";
		Query<User> query=session.createQuery(hql);
		query.setParameter("hisname",username);
		List<User> list=query.list();
		if(list.size()!=1)
		{
			System.out.println("401");
			response.setStatus(401);
			session.close();
			return;
		}
		User user=list.get(0);
		if(!user.getPassword().equals(password))//密码错误
		{
			System.out.println("403");
			response.setStatus(403);
			session.close();
			return;
		}
		if(user.getIsUseful()==0)
		{
			System.out.println("401");
			response.setStatus(401);
			session.close();
			return;
		}
		JSONObject resjson=(JSONObject) JSON.toJSON(user);
		Config config=Config.getConfig();
		resjson.put("useName",config.selectName);
		resjson.put("usePassword",config.selectPassword);
		response.setStatus(200);
		response.getWriter().println(resjson.toJSONString());
		System.out.println("success");
		session.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.getWriter().println("<h1>A Blank Page (almost)</h1>");
			response.getWriter().println(new Date());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
