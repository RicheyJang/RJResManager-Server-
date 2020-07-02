package ToolFunc;

public class Config {
	public String ip;
	public int port;
	public String dataBaseName;
	public String selectName;
	public String selectPassword;
	public String rootName;
	public String rootPassword;
	public String[] statusList;
	private static Config config=null;
	private Config()
	{
		ip="127.0.0.1";
		port=2333;
		dataBaseName="finaltest";
		selectName="ForSelect";
		selectPassword="MyWeb008";
		rootName="root";
		rootPassword="admin";
	}
	public static Config getConfig() {
		if(config==null)
			config=new Config();
		return config;
	}
}
