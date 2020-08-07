package Tasks;

import ToolFunc.Config;
import cn.hutool.core.date.DateUtil;

import javax.servlet.*;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class NFDFlightDataTimerTask extends TimerTask {
	@Override
	public void run() {
		try {
			//在这里写你要执行的内容
			System.out.println("time task in working mysqldump!");
			System.out.println("task result : "+backupWindow());
		} catch (Exception e) {
			System.out.println("time use in waring!!!!!!!!!!!!");
		}
	}

	private String getBackupName()
	{
		String name=Config.getConfig().dataBaseName;
		String date=DateUtil.format(new Date(),"yyyy-MM-dd");
		return name+"-"+date+".sql";
	}

	private boolean backupWindow(){
		try {
			String bkPath=Config.getConfig().dataBackupPath;
			String sqlPath = bkPath + getBackupName();

			StringBuffer sb = new StringBuffer();
			sb.append(Config.getConfig().dataBinPath);
			sb.append("mysqldump ");
			sb.append("--opt ");
			sb.append("-h ");
			sb.append("127.0.0.1");
			sb.append(" ");
			sb.append("--user=");
			sb.append(Config.getConfig().rootName);
			sb.append(" ");
			sb.append("--password=");
			sb.append(Config.getConfig().rootPassword);
			sb.append(" ");
			sb.append("--lock-all-tables=true ");
			sb.append("--result-file=");
			sb.append(sqlPath);
			sb.append(" ");
			sb.append("--default-character-set=utf8 ");
			sb.append(Config.getConfig().dataBaseName);

			System.out.println(sb.toString());
			Runtime cmd = Runtime.getRuntime();
			try {
				String [] cmdLine={"cmd","/k",sb.toString()};
				Process p = cmd.exec(sb.toString());
				//取得命令结果的输出流
				InputStream fis=p.getInputStream();
				//用一个读输出流类去读
				InputStreamReader isr=new InputStreamReader(fis);
				//用缓冲器读行
				BufferedReader br=new BufferedReader(isr);
				String line=null;
				//直到读完为止
				while((line=br.readLine())!=null)
				{
					System.out.println(line);
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}