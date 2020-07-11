package Servlets;

import ToolFunc.DealServlet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ToolFunc.*;
import org.hibernate.Session;
import pojo.*;

@WebServlet(name = "NewOrderServlet")
public class NewOrderServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json= DealServlet.getRequestJsonObject(request);
		if(!(json.containsKey("userInformation") && json.containsKey("orderInformation") && json.containsKey("itemsInformation")))
		{
			System.out.println("未知用户发起订单");
			response.setStatus(401);
			return ;
		}
		JSONObject userInf=json.getJSONObject("userInformation");
		User user=DealServlet.getUser(userInf.getString("username"),userInf.getString("password"));
		if(user==null)
		{
			System.out.println("未知用户发起订单");
			response.setStatus(401);
			return ;
		}
		String forWhat=request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		try {
			Method method=getClass().getDeclaredMethod(forWhat,JSONObject.class,User.class);
			method.invoke(this,json,user);
		} catch (Exception e) {
			System.out.println("no such method named : "+forWhat);
			response.setStatus(401);
		}
	}

	private void forOrder(JSONObject json,User user)
	{
		JSONObject orderInf=json.getJSONObject("orderInformation");
		JSONArray itemsInf=json.getJSONArray("itemsInformation");

		Orders order = new Orders();
		order.setUseclass(orderInf.getString("useclass"));
		order.setStarttime(orderInf.getSqlDate("starttime"));
		order.setMore(orderInf.getString("more"));
		order.setWorkshop(orderInf.getString("workshop"));
		order.setStatus(Config.getConfig().statusList[1]);
		order.setTeacher(user.getTruename());
		order.setHeader("");
		order.setAdmin("");
		order.setKeeper("");
		order.setAccountant("");

		Set <Orderitems> items=new HashSet<>();
		System.out.println("a new order come!");
		for (Object o : itemsInf) {
			JSONObject itemInf = (JSONObject) o;
			if (itemInf != null) {
				Orderitems item = new Orderitems();
				item.setPid(itemInf.getInteger("pid"));
				item.setCnt(itemInf.getDouble("cnt"));
				item.setMore(itemInf.getString("more"));
				item.setStatus("待审核");
				item.setOrder(order);
				System.out.println("items count:" + item.getCnt());
				items.add(item);
			}
		}
		order.setItems(items);
		Session session=HibernateFactory.getSession();
		session.beginTransaction();
		session.save(order);
		session.getTransaction().commit();
		session.close();
	}

	private void forItemOrder(JSONObject json,User user)
	{}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//nothing
	}
}
