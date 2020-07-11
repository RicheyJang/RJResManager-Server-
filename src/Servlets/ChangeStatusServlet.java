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
	private boolean checkDelAble(User user,Orders order) //订单能否撤回检测
	{
		if(order.getTeacher().compareTo(user.getTruename())!=0)
			return false;
		String[] ls=Config.getConfig().statusList;
		int i;
		for(i=0;i<ls.length;i++)
		{
			if(order.getStatus().compareTo(ls[i])==0)
				break;
		}
		return i<=4;
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
		User user=DealServlet.getUser(userInf.getString("username"),userInf.getString("password"));
		if(user==null)
		{
			System.out.println("未知用户修改订单");
			response.setStatus(401);
			return;
		}

		JSONObject orderInf=json.getJSONObject("orderInformation");

		JSONArray theOrders=orderInf.getJSONArray("theOrders");
		Boolean isSame=orderInf.getBoolean("isSame");
		String toStatus=orderInf.getString("status");
		String[] stls=Config.getConfig().statusList;

		if(isSame){
			if(!userCheck(user,toStatus)) //用户身份检查
			{
				System.out.println("该用户无权修改");
				response.setStatus(402);
				return;
			}
		}

		Session session= HibernateFactory.getSession();
		session.beginTransaction();

		for (Object o : theOrders) {
			JSONObject aOrder=(JSONObject) o;
			int id=aOrder.getInteger("id");
			if(!isSame){
				toStatus=aOrder.getString("status");
				if(!userCheck(user,toStatus)) { //用户身份检查
					System.out.println("该用户(id= "+user.getId()+")无权修改order id="+id);
					response.setStatus(402);
					continue;
				}
			}

			Orders order =(Orders) session.get(Orders.class, id); //获取订单信息
			if(order==null) //订单不存在
			{
				System.out.println("订单不存在 id="+id);
				response.setStatus(403);
				continue;
			}

			System.out.println("someone is changing this order's status!");
			System.out.println("orderid : "+order.getId());
			if(toStatus.compareTo(Config.getConfig().statusList[0])==0) //撤回订单
			{
				if(!checkDelAble(user,order))
				{
					System.out.println("该用户(id= "+user.getId()+")无权撤回order id="+id);
					response.setStatus(402);
					continue;
				}
				session.delete(order);
				continue;
			}
			if(toStatus.compareTo(stls[2])==0 || toStatus.compareTo(stls[3])==0) //主任 车间信息检查
			{
				if(user.getWorkshop().compareTo(order.getWorkshop())!=0)
				{
					System.out.println("该用户(id= "+user.getId()+")无权修改order id="+id);
					response.setStatus(402);
					continue;
				}
			}

			order.setStatus(toStatus);
			if(toStatus.compareTo(stls[1])!=0)
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
		}

		session.getTransaction().commit();
		session.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//nothing
	}
}
