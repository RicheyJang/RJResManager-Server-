package ToolFunc;

public class Config {
	public String ip;
	public int port;
	public String dataBaseName;
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
		ip="127.0.0.1";
		port=2333;
		dataBaseName="finaltest";
		selectName="ForSelect";
		selectPassword="MyWeb008";
		rootName="root";
		rootPassword="admin";
		newClientVersion="0.0.1";
		itemsList=new String[2];
		itemsList[0]="耗材类";
		itemsList[1]="租赁类";
		itemStartWith=new char[2];
		itemStartWith[0]='1';
		itemStartWith[1]='2';
	}
	public static Config getConfig() {
		if(config==null)
			config=new Config();
		return config;
	}
}
