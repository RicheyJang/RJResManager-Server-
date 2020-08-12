package Servlets;

import ToolFunc.Config;
import ToolFunc.DealServlet;
import ToolFunc.HibernateFactory;
import ToolFunc.SubFunc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import pojo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet(name = "UserManageServlet")
public class UserManageServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json= DealServlet.getRequestJsonObject(request);
		if(!(json.containsKey("applicantInformation") && json.containsKey("userInformation")))
		{
			System.out.println("未知用户发起用户修改申请");
			response.setStatus(401);
			return ;
		}
		JSONObject userInf=json.getJSONObject("userInformation");
		User user=DealServlet.getUser(userInf.getString("username"),userInf.getString("password"));
		if(user==null)
		{
			System.out.println("未知用户发起用户修改申请");
			response.setStatus(401);
			return ;
		}
		System.out.println("someone is changing some users! id="+user.getId());
		String forWhat=request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
		try {
			Method method=getClass().getDeclaredMethod(forWhat, JSONObject.class, User.class);
			Integer res=(Integer)method.invoke(this,json,user);
			if(res==0)
			{
				System.out.println("该用户进行用户修改申请失败");
				response.setStatus(401);
			}
			else
			{
				JSONObject resJson=new JSONObject();
				resJson.put("successNum",res);
				response.setStatus(200);
				response.getWriter().println(resJson.toJSONString());
			}
		} catch (Exception e) {
			System.out.println("no such method named : "+forWhat);
			response.setStatus(401);
		}
	}

	private Integer AddNew(JSONObject json,User user)
	{
		if(user.getIdentity().compareTo("admin")!=0)
			return 0;
		JSONArray applyArray=json.getJSONArray("applicantInformation");//批量添加

		int sNum=0;
		for (Object o : applyArray) {
			JSONObject applyOne=(JSONObject) o;
			if(applyOne!=null)
			{
				sNum+=addNewUser(applyOne);
			}
		}
		return sNum;
	}

	private Integer addNewUser(JSONObject applyOne)
	{
		User theOne=new User();
		String s=applyOne.getString("identity");
		if(s==null) return 0;
		boolean check=false;
		for(String iDen : Config.getConfig().identityList)
		{
			if(s.compareTo(iDen)==0)
			{
				check=true;
				break;
			}
		}
		if(!check) return 0;
		theOne.setIdentity(s);
		theOne.setIsUseful(applyOne.getByte("isUseful"));
		theOne.setPassword(SubFunc.getSHA256(Config.getConfig().userInitPassword));
		theOne.setWorkshop(applyOne.getString("workshop"));
		s=applyOne.getString("truename");
		if(!SubFunc.checkTruename(s,0)) return 0;
		theOne.setTruename(s);
		s=applyOne.getString("username");
		if(!SubFunc.checkUsername(s)) return 0;
		if(SubFunc.hasUsername(s,0)) return 0;
		theOne.setUsername(s);
		Session session=HibernateFactory.getSession();
		session.beginTransaction();
		session.save(theOne);
		session.getTransaction().commit();
		session.close();
		return 1;
	}

	private Integer Change(JSONObject json,User user)
	{
		JSONObject applyOne=json.getJSONObject("applicantInformation");//单个修改
		Integer ID=applyOne.getInteger("id");
		if(user.getIdentity().compareTo("admin")!=0 && ID!=user.getId())
			return 0;
		Session session=HibernateFactory.getSession();
		session.beginTransaction();
		User theOne=session.get(User.class,ID);
		if(theOne==null)
			return 0;
		String s;
		if(applyOne.containsKey("truename"))
		{
			s=applyOne.getString("truename");
			if(!SubFunc.checkTruename(s,ID))
			{
				session.close();
				return 0;
			}
			theOne.setTruename(s);
		}
		if(applyOne.containsKey("workshop"))
		{
			theOne.setWorkshop(applyOne.getString("workshop"));
		}
		if(applyOne.containsKey("password"))
		{
			s=applyOne.getString("password");
			if(!SubFunc.checkSHA256(s))
			{
				session.close();
				return 0;
			}
			theOne.setPassword(s);
		}
		if(applyOne.containsKey("isUseful"))
		{
			theOne.setIsUseful(applyOne.getByte("isUseful"));
		}
		if(applyOne.containsKey("identity"))
		{
			s=applyOne.getString("identity");
			boolean check=false;
			for(String iDen : Config.getConfig().identityList)
			{
				if(s.compareTo(iDen)==0)
				{
					check=true;
					break;
				}
			}
			if(!check)
			{
				session.close();
				return 0;
			}
			theOne.setIdentity(s);
		}
		session.update(theOne);
		session.getTransaction().commit();
		session.close();
		return 1;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
