package ToolFunc;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;

import java.nio.charset.Charset;

public class Config {
	public String ip;
	public int port;
	public String dataBaseName;
	public String dataBackupPath;
	public String dataBinPath;
	public int dataBasePort;
	public String selectName;
	public String selectPassword;
	public String rootName;
	public String rootPassword;
	public String newClientVersion;
	public String[] statusList; //状态名列表：0~8查authority表，0代表撤销订单
	public String[] itemsList; //物品大类名列表：耗材类、租赁类
	public char[] itemStartWith;
	private static Config config=null;
	private Config()
	{
		Setting setting;
		if(!FileUtil.isFile("config.setting"))
		{
			setting=new Setting();
			setting.setCharset(CharsetUtil.CHARSET_UTF_8);
			setting.set("server","ip","127.0.0.1");
			setting.set("server","port","2333");
			setting.set("database","name","finaltest");
			setting.set("database","binPath","D:\\Mysql\\mysql-5.7.29-win32\\bin\\");
			setting.set("database","backupPath","./");
			setting.set("database","port","3306");
			setting.set("database","selectName","ForSelect");
			setting.set("database","selectPassword","MyWeb008");
			setting.set("database","rootName","root");
			setting.set("database","rootPassword","admin");
			setting.set("client","version","0.0.1");
			setting.set("items","outName","耗材类");
			setting.set("items","rentName","租赁类");
			setting.set("items","outStartWith","1");
			setting.set("items","rentStartWith","2");
			setting.store("config.setting");
		} else {
			System.out.println("read config.setting out!");
			setting=new Setting("config.setting", CharsetUtil.CHARSET_UTF_8,true);
		}
		ip=setting.get("server","ip");
		port=Integer.parseInt(setting.get("server","port"));
		dataBaseName=setting.get("database","name");
		dataBackupPath=setting.get("database","backupPath");
		dataBinPath=setting.get("database","binPath");
		dataBasePort=Integer.parseInt(setting.get("database","port"));;
		selectName=setting.get("database","selectName");
		selectPassword=setting.get("database","selectPassword");
		rootName=setting.get("database","rootName");
		rootPassword=setting.get("database","rootPassword");
		newClientVersion=setting.get("client","version");;
		itemsList=new String[2];
		itemsList[0]=setting.get("items","outName");
		itemsList[1]=setting.get("items","rentName");
		itemStartWith=new char[2];
		itemStartWith[0]=setting.get("items","outStartWith").charAt(0);
		itemStartWith[1]=setting.get("items","rentStartWith").charAt(0);
	}
	public static Config getConfig() {
		if(config==null)
			config=new Config();
		return config;
	}
}
