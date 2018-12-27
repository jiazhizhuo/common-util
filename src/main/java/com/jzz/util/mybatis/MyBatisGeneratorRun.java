package com.jzz.util.mybatis;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class MyBatisGeneratorRun {

	public static void main(String[] args) throws Exception {
		MyBatisGeneratorRun app = new MyBatisGeneratorRun();

		System.out.println(app.getClass().getResource("/").getPath());
		app.generator();
		System.out.println(System.getProperty("user.dir"));
	}

	public void generator() throws Exception {

		List<String> warnings = new ArrayList<String>();
		boolean overwrite = true;
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("com/jzz/util/mybatis/generatorConfig.xml");
		ConfigurationParser cp = new ConfigurationParser(warnings);
		Configuration config = cp.parseConfiguration(resourceAsStream);
		DefaultShellCallback callback = new DefaultShellCallback(overwrite);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);

		for (String warning : warnings) {
			System.out.println(warning);
		}
	}

}
