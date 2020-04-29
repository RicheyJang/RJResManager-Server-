package ToolFunc;

public class Config {
	public String ip;
	public int port;
	public String dataBaseName;
	public String selectName;
	public String selectPassword;
	private static Config config=null;
	private Config()
	{
		ip="127.0.0.1";
		port=2333;
		dataBaseName="finaltest";
		selectName="ForSelect";
		selectPassword="MyWeb008";
	}
	public static Config getConfig() {
		if(config==null)
			config=new Config();
		return config;
	}
}
