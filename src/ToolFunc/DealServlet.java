package ToolFunc;

import com.alibaba.fastjson.JSONObject;
import org.hibernate.Session;
import org.hibernate.query.Query;
import pojo.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class DealServlet {
	public static JSONObject getRequestJsonObject(HttpServletRequest request){
		//获取 request 中 json 字符串的内容
		String json = null;
		try {
			json = getRequestJsonString(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JSONObject.parseObject(json);
	}
	public static String getRequestJsonString(HttpServletRequest request)
			throws IOException {
		//描述:获取 post 请求的 byte[] 数组
		String submitMehtod = request.getMethod();
		// GET
		if (submitMehtod.equals("GET")) {
			return new String(request.getQueryString().getBytes("iso-8859-1"),"utf-8").replaceAll("%22", "\"");
			// POST
		} else {
			return getRequestPostStr(request);
		}
	}
	public static byte[] getRequestPostBytes(HttpServletRequest request)
			throws IOException {
		//描述:获取 post 请求内容
		int contentLength = request.getContentLength();
		if(contentLength<0){
			return null;
		}
		byte buffer[] = new byte[contentLength];
		for (int i = 0; i < contentLength;) {

			int readlen = request.getInputStream().read(buffer, i,
					contentLength - i);
			if (readlen == -1) {
				break;
			}
			i += readlen;
		}
		return buffer;
	}
	public static String getRequestPostStr(HttpServletRequest request)
			throws IOException {
		byte buffer[] = getRequestPostBytes(request);
		String charEncoding = request.getCharacterEncoding();
		if (charEncoding == null) {
			charEncoding = "UTF-8";
		}
		return new String(buffer, charEncoding);
	}
	public static User getUser(String username,String password)
	{
		if(username==null || password==null || !SubFunc.checkUsername(username))
		{
			return null;
		}
		Session session= HibernateFactory.getSession();
		String hql="from User where username=:hisname";
		Query<User> query=session.createQuery(hql);
		query.setParameter("hisname",username);
		List<User> list=query.list();
		if(list.size()!=1)
		{
			session.close();
			return null;
		}
		User user=list.get(0);
		if(!user.getPassword().equals(password))
		{
			session.close();
			return null;
		}
		if(user.getIsUseful()==0)
		{
			session.close();
			return null;
		}
		session.close();
		return user;
	}
}
