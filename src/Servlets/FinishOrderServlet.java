package Servlets;

import ToolFunc.Config;
import ToolFunc.DealServlet;
import ToolFunc.HibernateFactory;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pojo.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@WebServlet(name = "FinishOrderServlet")
public class FinishOrderServlet extends HttpServlet {
	private boolean userCheck(User user, Orders order)
	{
		if(user==null) return false;
		if(user.getIdentity().compareTo("keeper")!=0) return false;
		return true;
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

		Session session= HibernateFactory.getSession();
		session.beginTransaction();
		int id=orderInf.getInteger("id");
		Orders order =(Orders) session.get(Orders.class, id); //获取订单信息
		if(order==null) //订单不存在
		{
			System.out.println("订单不存在");
			response.setStatus(403);
			session.close();
			return;
		}

		if(!userCheck(user,order)) //用户身份检查
		{
			System.out.println("该用户无权完成订单");
			response.setStatus(402);
			return;
		}

		System.out.println("someone is changing this order's status!");
		System.out.println("orderID : "+order.getId());

		String[] lsStatus=Config.getConfig().statusList;
		String[] lsItem=Config.getConfig().itemsList;
		String fromStatus=order.getStatus();
		String toStatus; //开始判定订单下一状态

		Set<Orderitems> items=order.getItems();
		Historyorder horder=null;
		Config configs=Config.getConfig();
		if(fromStatus.compareTo(lsStatus[5])==0)
		{
			boolean hasRent=false;
			for(Orderitems item : items)
			{
				long pid=item.getPid();
				if(Long.toString(pid).charAt(0)==configs.itemStartWith[1])
				{
					hasRent=true;
					break;
				}
			}
			horder = new Historyorder();
			horder.setId(order.getId());
			horder.setUseclass(order.getUseclass());
			horder.setStarttime(order.getStarttime());
			horder.setOuttime(new Timestamp(System.currentTimeMillis()));
			horder.setMore(order.getMore());
			horder.setWorkshop(order.getWorkshop());
			horder.setTeacher(order.getTeacher());
			horder.setHeader(order.getHeader());
			horder.setAdmin(order.getAdmin());
			horder.setAccountant(order.getAccountant());
			Set <Historyitems> hItems=new HashSet<>();
			System.out.println("an order complete!");
			for (Orderitems item : items) {
				long pid=item.getPid();
				Allitems theItem=(Allitems)session.get(Allitems.class, pid);
				if(hasRent && theItem.getRes().compareTo(lsItem[1])==0)
				{
					double cnt=theItem.getCnt();
					cnt=cnt-item.getCnt();
					theItem.setCnt(cnt);
					session.update(theItem);
					continue;
				}
				Historyitems hItem=new Historyitems();
				hItem.setId(item.getId());
				hItem.setPid(item.getPid());
				hItem.setCnt(item.getCnt());
				hItem.setMore(item.getMore());
				hItem.setStatus("已完成");
				hItem.setOrder(horder);
				hItems.add(hItem);
				double cnt=theItem.getCnt();
				cnt=cnt-item.getCnt();
				theItem.setCnt(cnt);
				session.update(theItem);
				//session.remove(item); 不能一边删一边查
			}
			horder.setItems(hItems);
			if(hasRent)
			{
				order.setStatus(lsStatus[7]);
				horder.setStatus(lsStatus[7]);
				order.setOuttime(new Timestamp(System.currentTimeMillis()));
				order.setKeeper(user.getTruename());
				session.update(order);
			}
			else
			{
				horder.setStatus(lsStatus[8]);
				session.delete(order);
			}
			horder.setKeeper(user.getTruename());
			session.save(horder);
		}
		else if(fromStatus.compareTo(lsStatus[7])==0) {
			horder=session.get(Historyorder.class,id);
			if(horder==null) //订单不存在
			{
				System.out.println("订单不存在");
				response.setStatus(403);
				session.close();
				return;
			}
			Set <Historyitems> hItems=horder.getItems();
			System.out.println("an order complete again!");
			for (Orderitems item : items) {
				long pid=item.getPid();
				Allitems theItem=(Allitems)session.get(Allitems.class, pid);
				if(theItem.getRes().compareTo(lsItem[1])==0)
				{
					Historyitems hItem=new Historyitems();
					hItem.setId(item.getId());
					hItem.setPid(item.getPid());
					hItem.setCnt(item.getCnt());
					hItem.setMore(item.getMore());
					hItem.setStatus("已完成");
					hItem.setOrder(horder);
					hItems.add(hItem);
					double cnt=theItem.getCnt();
					cnt=cnt+item.getCnt();
					theItem.setCnt(cnt);
					session.update(theItem);
				}
			}
			horder.setStatus(lsStatus[8]);
			horder.setBacktime(new Timestamp(System.currentTimeMillis()));
			session.update(horder);
			session.delete(order);
		} else {
			System.out.println("错误完成订单");
			response.setStatus(403);
			return;
		}

		session.getTransaction().commit();
		session.close();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//nothing
	}
}
