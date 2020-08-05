package Servlets;

import ToolFunc.Config;
import ToolFunc.DealServlet;
import ToolFunc.HibernateFactory;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.type.IntegerType;
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
		return user.getIdentity().compareTo("keeper") == 0 || user.getIdentity().compareTo("admin") == 0;
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

		System.out.println("someone is finishing this order's status!");
		System.out.println("orderID : "+order.getId());

		String[] lsStatus=Config.getConfig().statusList;
		String fromStatus=order.getStatus();

		if(fromStatus.compareTo(lsStatus[5])==0)
		{
			if(!finishOneOrder(id,user))
			{
				System.out.println("订单无法完成");
				response.setStatus(403);
				session.close();
				return;
			}
		}
		else if(fromStatus.compareTo(lsStatus[7])==0) {
			if(!finishRentOrder(id)) //订单不存在
			{
				System.out.println("订单不存在");
				response.setStatus(403);
				session.close();
				return;
			}
		}
		else if(fromStatus.compareTo(lsStatus[9])==0) {
			if(!finishResOrder(id,user)) //订单不存在
			{
				System.out.println("订单无法完成");
				response.setStatus(403);
				session.close();
				return;
			}
		}
		else {
			System.out.println("错误完成订单");
			response.setStatus(403);
			session.close();
			return;
		}

		session.close();
	}

	private Boolean finishOneOrder(int id,User user)
	{
		Session session= HibernateFactory.getSession();
		session.beginTransaction();
		String[] lsStatus=Config.getConfig().statusList;
		String[] lsItem=Config.getConfig().itemsList;
		Orders order =(Orders) session.get(Orders.class, id); //获取订单信息
		Set<Orderitems> items=order.getItems();
		boolean hasRent=false;
		Config configs=Config.getConfig();
		for(Orderitems item : items)
		{
			long pid=item.getPid();
			if(Long.toString(pid).charAt(0)==configs.itemStartWith[1])
			{
				hasRent=true;
				break;
			}
		}
		Historyorder horder = new Historyorder();
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
		session.getTransaction().commit();
		session.close();
		return true;
	}

	private Boolean finishRentOrder(int id)
	{
		Session session= HibernateFactory.getSession();
		session.beginTransaction();
		String[] lsStatus=Config.getConfig().statusList;
		String[] lsItem=Config.getConfig().itemsList;
		Historyorder horder=session.get(Historyorder.class,id);
		Orders order =(Orders) session.get(Orders.class, id); //获取订单信息
		Set<Orderitems> items=order.getItems();
		if(horder==null) //订单不存在
		{
			session.close();
			return false;
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
		session.getTransaction().commit();
		session.close();
		return true;
	}

	private Boolean finishResOrder(int id,User user)
	{
		Session session= HibernateFactory.getSession();
		session.beginTransaction();
		String[] lsStatus=Config.getConfig().statusList;
		String[] lsItem=Config.getConfig().itemsList;
		Orders order =(Orders) session.get(Orders.class, id); //获取订单信息
		Set<Newitems> items=order.getNewItems();
		Historyorder horder = new Historyorder();
		horder.setId(order.getId());
		horder.setStarttime(order.getStarttime());
		horder.setOuttime(new Timestamp(System.currentTimeMillis()));
		horder.setMore(order.getMore());
		horder.setKeeper(order.getKeeper());
		Set <Historyitems> hItems=new HashSet<>();
		System.out.println("a res order complete!");
		for (Newitems item : items) {
			int pid=addNewItem(item);
			if(pid==0) continue;
			Historyitems hItem=new Historyitems();
			hItem.setId(item.getId());
			hItem.setPid(pid);
			hItem.setCnt(item.getCnt());
			hItem.setMore(item.getMore());
			hItem.setStatus("已完成");
			hItem.setOrder(horder);
			hItems.add(hItem);
		}
		horder.setItems(hItems);
		horder.setStatus(lsStatus[8]);
		session.delete(order);
		horder.setAdmin(user.getTruename());
		session.save(horder);
		session.getTransaction().commit();
		session.close();
		return true;
	}

	private int addNewItem(Newitems item)
	{
		Session session=HibernateFactory.getSession();
		Transaction ts=session.beginTransaction();
		int pid=getNewItemPid(item);
		if(pid==0) return 0;
		Allitems theItem=(Allitems)session.get(Allitems.class, pid);
		if(theItem!=null)
		{
			double cnt=theItem.getCnt();
			cnt=cnt+item.getCnt();
			theItem.setCnt(cnt);
			session.update(theItem);
		}
		else
		{
			theItem=new Allitems();
			theItem.setPid(pid);
			theItem.setCnt(item.getCnt());
			theItem.setRes(item.getRes());
			theItem.setName(item.getName());
			theItem.setType(item.getType());
			theItem.setUnits(item.getUnits());
			session.save(theItem);
		}
		ts.commit();
		session.close();
		return pid;
	}

	private Integer getNewItemPid(Newitems item)
	{
		if(item.getPid()>0)
			return item.getPid();
		int sti=0,st,ed,cnt=Config.getConfig().itemsList.length;
		for(int i=0;i<cnt;i++)
		{
			if(Config.getConfig().itemsList[i].equals(item.getRes()))
			{
				sti=Config.getConfig().itemStartWith[i]-'0';
			}
		}
		if(sti==0)
			return 0;
		ed=(sti+1)*1000000;
		st=sti*1000000;

		Session session=HibernateFactory.getSession();
		String hql="select max(pid) from Allitems where pid>:MinL and pid<:MaxL and res=:Res and name=:Name";
		Query query=session.createQuery(hql);
		query.setParameter("MinL",st);
		query.setParameter("MaxL",ed);
		query.setParameter("Res",item.getRes());
		query.setParameter("Name",item.getName());
		//System.out.println("here: "+(int)query.getSingleResult());
		List<Integer> ls=query.list();

		if(ls.size()>=1 && ls.get(0)!=null)
		{
			hql="select max(pid) from Allitems where pid>:MinL and pid<:MaxL and res=:Res and name=:Name and type=:Type";
			query=session.createQuery(hql);
			query.setParameter("MinL",st);
			query.setParameter("MaxL",ed);
			query.setParameter("Res",item.getRes());
			query.setParameter("Name",item.getName());
			query.setParameter("Type",item.getType());
			List<Integer> ls2=query.list();
			session.close();
			if(ls2.size()>=1 && ls2.get(0)!=null)
				return ls2.get(0);
			return ls.get(0)+1;
		}
		else
		{
			hql="select max(pid) from Allitems where pid>:MinL and pid<:MaxL";
			query=session.createQuery(hql);
			query.setParameter("MinL",st);
			query.setParameter("MaxL",ed);
			List<Integer> ls2=query.list();
			Integer tmp=0,ans=st;
			session.close();
			if(ls2.size()>=1 && ls2.get(0)!=null)
			{
				tmp=ls2.get(0);
				tmp=tmp-tmp%1000+1001;
				return tmp;
			}
			return st+1001;
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//nothing
	}
}
