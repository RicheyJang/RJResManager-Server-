package Servlets;

import ToolFunc.Config;
import ToolFunc.DealServlet;
import ToolFunc.HibernateFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import pojo.Orderitems;
import pojo.Orders;
import pojo.User;

import org.hibernate.query.Query;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@WebServlet(name = "ChangeOrderServlet")
public class ChangeOrderServlet extends HttpServlet {
	private boolean userCheck(User user,Orders order)
	{
		if(user==null) return false;
		if(user.getIdentity().compareTo("keeper")==0 || user.getIdentity().compareTo("accountant")==0)
			return false;
		if(order.getStatus().compareTo(Config.getConfig().statusList[2])==0)
			return true;
		Session session= HibernateFactory.getSession();
		String hql="select "+user.getIdentity()+" from Authority where status=:ss";
		Query<Integer> query=session.createQuery(hql);
		query.setParameter("ss",order.getStatus());
		List<Integer> ls=query.list();
		if(ls.size()<1)
			return false;
		Integer ide=ls.get(0);
		session.close();
		return (ide==1 || ide==3);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json= DealServlet.getRequestJsonObject(request);
		if(!(json.containsKey("userInformation") && json.containsKey("orderInformation") && json.containsKey("itemsInformation")))
		{
			System.out.println("未知用户修改订单");
			response.setStatus(401);
			return;
		}
		JSONObject userInf=json.getJSONObject("userInformation");
		JSONObject orderInf=json.getJSONObject("orderInformation");
		JSONArray itemsInf=json.getJSONArray("itemsInformation");
		User user=DealServlet.getUser(userInf.getString("username"),userInf.getString("password"));
		if(user==null)
		{
			System.out.println("未知用户修改订单");
			response.setStatus(401);
			return;
		}
		Session session= HibernateFactory.getSession();
		session.beginTransaction();

		int id=orderInf.getInteger("id");
		Orders order =(Orders) session.get(Orders.class, id);
		if(order==null)
		{
			System.out.println("订单不存在");
			response.setStatus(403);
			session.close();
			return;
		}
		System.out.println("orderid : "+order.getId());

		if(!userCheck(user,order)) //用户身份检查
		{
			System.out.println("该用户无权修改本订单");
			response.setStatus(402);
			session.close();
			return;
		}

		order.setUseclass(orderInf.getString("useclass"));
		order.setMore(orderInf.getString("more"));
		Set <Orderitems> items=order.getItems();
		for(Orderitems orderItem : items)
			session.remove(orderItem);
		items.clear();

		System.out.println("someone is changing an order!");
		for (Iterator<Object> iterator = itemsInf.iterator(); iterator.hasNext(); ) {
			JSONObject itemInf = (JSONObject) iterator.next();
			if(itemInf!=null)
			{
				Orderitems item=new Orderitems();
				item.setPid(itemInf.getInteger("pid"));
				item.setCnt(itemInf.getDouble("cnt"));
				item.setMore(itemInf.getString("more"));
				item.setStatus("待审核");
				item.setOrder(order);
				items.add(item);
			}
		}
		order.setItems(items);
		session.update(order);

		session.getTransaction().commit();
		session.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//nothing
	}
}
