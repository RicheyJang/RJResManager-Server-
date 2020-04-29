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
		if(!(json.containsKey("userInformation") && json.containsKey("userInformation") && json.containsKey("userInformation")))
		{
			System.out.println("未知用户发起订单");
			response.setStatus(401);
			return;
		}
		JSONObject userInf=json.getJSONObject("userInformation");
		JSONObject orderInf=json.getJSONObject("orderInformation");
		JSONArray itemsInf=json.getJSONArray("itemsInformation");
		User user=DealServlet.getUser(userInf.getString("username"),userInf.getString("password"));
		if(user==null)
		{
			System.out.println("未知用户发起订单");
			response.setStatus(401);
			return;
		}
		Orders order = new Orders();
		order.setWorkshop(user.getWorkshop());
		order.setUseclass(orderInf.getString("useclass"));
		order.setStarttime(orderInf.getSqlDate("starttime"));
		order.setMore(orderInf.getString("more"));
		order.setTeacher(user.getTruename());
		order.setHeader("");
		order.setAdmin("");
		order.setKeeper("");
		order.setAccountant("");
		order.setStatus("待一级审核");

		Set <Orderitems> items=new HashSet<>();
		System.out.println("here!!!!!");
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
				System.out.println(item.getCnt());
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//nothing
	}
}
