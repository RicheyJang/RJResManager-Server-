package Servlets;

import javax.servlet.ServletConfig;
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
	public void init(ServletConfig config){
		System.out.println("init of Servlet");
		Config configs=Config.getConfig();
		Session session=HibernateFactory.getSession();
		String hql="select status from Authority";
		Query<String> query=session.createQuery(hql);
		List<String> ls=query.list();
		configs.statusList=new String[ls.size()+1];
		int i=1;
		configs.statusList[0]="0";
		for(String s : ls)
		{
			configs.statusList[i]=s;
			i++;
		}
		session.close();
		System.out.println("init finish");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json=DealServlet.getRequestJsonObject(request);
		String username=json.getString("username");
		String password=json.getString("password");
		if(!SubFunc.checkUsername(username))
		{
			response.setStatus(401);
			return;
		}
		System.out.println("got it!someone is logging in!");
		System.out.println(json.toString());

		Session session= HibernateFactory.getSession();
		String hql="from User where username=:hisname";
		Query<User> query=session.createQuery(hql);
		query.setParameter("hisname",username);
		List<User> list=query.list();
		if(list.size()!=1)
		{
			System.out.println("401 no such user");
			response.setStatus(401);
			session.close();
			return;
		}
		User user=list.get(0);
		//String SHA256inOne=user.getPassword(); TODO 双验证待实现
		//String SHA256inTwice=SubFunc.getSHA256(SHA256inOne);
		if(!user.getPassword().equals(password))//密码错误
		{
			System.out.println("403 wrong password");
			response.setStatus(403);
			session.close();
			return;
		}
		if(user.getIsUseful()==0)
		{
			System.out.println("401 useless account");
			response.setStatus(401);
			session.close();
			return;
		}
		JSONObject resjson=(JSONObject) JSON.toJSON(user);
		Config config=Config.getConfig();
		resjson.put("useName",config.selectName);
		resjson.put("usePassword",config.selectPassword);
		resjson.put("newClientVersion",config.newClientVersion);
		response.setStatus(200);
		response.getWriter().println(resjson.toJSONString());
		System.out.println("success logging id = "+user.getId());
		session.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.getWriter().println("<h1>A Blank Page (almost)</h1>");
			response.getWriter().println(new Date());
			response.getWriter().println("Client Version : v"+Config.getConfig().newClientVersion);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
