package ToolFunc;

import org.hibernate.Session;
import org.hibernate.query.Query;
import pojo.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.*;

public class SubFunc {
	public static boolean checkRex(String s,String rex)
	{
		Pattern pattern=Pattern.compile(rex);
		Matcher matcher=pattern.matcher(s);
		return matcher.matches();
	}
	public static boolean hasUsername(String name,int exceptID)
	{
		Session session=HibernateFactory.getSession();
		String hql="from User where username=:hisname and id!=:hisID";
		Query<User> query=session.createQuery(hql);
		query.setParameter("hisname",name);
		query.setParameter("hisID",exceptID);
		List<User> list=query.list();
		if(list.size()==0 || list.get(0)==null)
		{
			session.close();
			return false;
		}
		session.close();
		return true;
	}
	public static boolean checkUsername(String name)
	{
		if(name==null) return false;
		if(name.length()<3 || name.length()>20)
			return false;
		String regExp = "^([a-zA-Z0-9_\u4e00-\u9fa5]){3,40}$";
		return name.matches(regExp);
	}
	public static boolean checkTruename(String name,int checkOneID)
	{
		if(name==null) return false;
		if(name.length()<1 || name.length()>11)
			return false;
		String regExp = "^([a-zA-Z0-9_\u4e00-\u9fa5]){1,22}$";
		if(!name.matches(regExp))
			return false;
		Session session=HibernateFactory.getSession();
		String hql="from User where truename=:hisname and id!=:hisID";
		Query<User> query=session.createQuery(hql);
		query.setParameter("hisname",name);
		query.setParameter("hisID",checkOneID);
		List<User> list=query.list();
		if(list.size()==0 || list.get(0)==null)
		{
			session.close();
			return true;
		}
		session.close();
		return false;
	}
	private static String byte2Hex(byte[] bytes){
		StringBuilder stringBuffer = new StringBuilder();
		String temp = null;
		for (int i=0;i<bytes.length;i++){
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length()==1){
				//1得到一位的进行补0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}
	public static String getSHA256(String s)
	{
		String res="";
		try {
			MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
			messageDigest.update(s.getBytes(StandardCharsets.UTF_8));
			res=byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return res;
	}
}
