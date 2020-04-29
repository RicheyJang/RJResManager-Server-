package ToolFunc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.*;

public class SubFunc {
	public static boolean checkRex(String s,String rex)
	{
		Pattern pattern=Pattern.compile(rex);
		Matcher matcher=pattern.matcher(s);
		return matcher.matches();
	}
	public static boolean checkUsername(String name)
	{
		String regExp = "^(\\w){3,20}$";
		return name.matches(regExp);
	}
	private static String byte2Hex(byte[] bytes){
		StringBuffer stringBuffer = new StringBuffer();
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
