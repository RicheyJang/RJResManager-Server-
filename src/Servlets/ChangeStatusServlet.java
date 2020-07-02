package Servlets;

import ToolFunc.Config;
import ToolFunc.DealServlet;
import ToolFunc.HibernateFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pojo.Orderitems;
import pojo.Orders;
import pojo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@WebServlet(name = "ChangeStatusServlet")
public class ChangeStatusServlet extends HttpServlet {
	private boolean userCheck(User user, String toStatus)
	{
		if(user==null) return false;
		if(toStatus.compareTo(Config.getConfig().statusList[0])==0)
			return true;
		Session session= HibernateFactory.getSession();
		String hql="select "+user.getIdentity()+" from Authority where status=:ss";
		Query<Integer> query=session.createQuery(hql);
		query.setParameter("ss",toStatus);
		List<Integer> ls=query.list();
		if(ls.size()<1)
			return false;
		Integer ide=ls.get(0);
		session.close();
		return (ide==2 || ide==3);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json= DealServlet.getRequestJsonObject(request);
		if(!(json.containsKey("userInformation") && json.containsKey("orderInformation")))
		{
			System.out.println("未知用户修改订单");
			response.setStatus(401);
			return;
		}
		JSONObject userInf=json.getJSONObject("userInformation");
		JSONObject orderInf=json.getJSONObject("orderInformation");
		User user=DealServlet.getUser(userInf.getString("username"),userInf.getString("password"));
		if(user==null)
		{
			System.out.println("未知用户修改订单");
			response.setStatus(401);
			return;
		}
		String toStatus=orderInf.getString("status");
		if(!userCheck(user,toStatus)) //用户身份检查
		{
			System.out.println("该用户无权修改");
			response.setStatus(402);
			return;
		}

		Session session= HibernateFactory.getSession();
		session.beginTransaction();

		int id=orderInf.getInteger("id");
		Orders order =(Orders) session.get(Orders.class, id); //获取订单信息
		String[] stls=Config.getConfig().statusList;
		if(order==null) //订单不存在
		{
			System.out.println("订单不存在");
			response.setStatus(403);
			session.close();
			return;
		}
		System.out.println("someone is changing this order's status!");
		System.out.println("orderid : "+order.getId());
		if(toStatus.compareTo(Config.getConfig().statusList[0])==0) //撤回订单
		{
			if(order.getTeacher().compareTo(user.getTruename())!=0)
			{
				System.out.println("该用户无权撤回该订单");
				response.setStatus(402);
				session.close();
				return;
			}
			session.delete(order);
			session.getTransaction().commit();
			session.close();
			return;
		}
		if(toStatus.compareTo(stls[2])==0 || toStatus.compareTo(stls[3])==0) //主任 车间信息检查
		{
			if(user.getWorkshop().compareTo(order.getWorkshop())!=0)
			{
				System.out.println("该用户无权修改");
				response.setStatus(402);
				session.close();
				return;
			}
		}

		order.setStatus(toStatus);
		if(toStatus.compareTo(Config.getConfig().statusList[1])!=0)
		{
			String s=user.getIdentity(); //反射 修改订单修改者名
			s="set"+s.toUpperCase().charAt(0)+s.substring(1);
			try {
				Method method=order.getClass().getMethod(s,String.class);
				method.invoke(order,user.getTruename());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		session.update(order);
		session.getTransaction().commit();
		session.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//nothing
	}
}
