package com.novarise.webase.servlet;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.novarise.webase.util.SmsSender;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.config.Configuration;
import com.ripple.datasource.config.MyDataSource;

public class InitDataSource extends HttpServlet {
	private static Logger logger = LogManager.getLogger(InitDataSource.class);

	public void init(ServletConfig cfg) {
		String default_ds = cfg.getInitParameter("default");
		String cfg_file = cfg.getInitParameter("config");
		String sms = cfg.getInitParameter("sms");
		if (sms == null)
			sms = "";
		String sms_url = cfg.getInitParameter("sms_url");
		if (sms_url == null)
			sms_url = "";
		String sms_user = cfg.getInitParameter("sms_user");
		if (sms_user == null)
			sms_user = "";
		String sms_pwd = cfg.getInitParameter("sms_pwd");
		if (sms_pwd == null)
			sms_pwd = "";
		String sms_type = cfg.getInitParameter("sms_type");
		if (sms_type == null)
			sms_type = "";

		Configuration ds_cfg = new Configuration();
		List dss = null;
		try {
			dss = ds_cfg.readFileConfig(cfg_file);
		} catch (ConfigurationException e) {
			// e.printStackTrace();
			logger.info("读取数据源配置文件出错: " + e.getMessage());
		}
		for (int i = 0; i < dss.size(); i++) {
			MyDataSource dso = (MyDataSource) dss.get(i);
			try {
				DSManager.initDataSource(dso);
			} catch (SQLException sqle) {
				logger.info(sqle.getMessage());
			}
		}
		// 设置默认的数据源
		DSManager.setDefaultDS(default_ds);

		// 启动短信网关
		if (sms.equals("1")) {
			try {
				SmsSender smws = SmsSender.newInstance(sms_url, sms_user,
						sms_pwd, sms_type);
				System.out.println("短信服务已启动!");
			} catch (Exception smse) {
				// logger.info(smse.getMessage());
				smse.printStackTrace();
			}
		}

	}
}
