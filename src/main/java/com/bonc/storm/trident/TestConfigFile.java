//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.bonc.storm.trident;

import com.bonc.storm.util.FileUtil;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class TestConfigFile {
	public TestConfigFile() {
	}

	public static void main(String[] args) throws Exception {
		if(args.length > 0) {
			String filePath = args[0];
			FileInputStream inputStream = new FileInputStream(new File(filePath));
			String fileContent = FileUtil.getStringFromInputStream(inputStream);
			System.out.println("fileContent:" + fileContent);
			Properties pro = FileUtil.getProperteis(filePath);
			String configFilePath = (String)pro.get("fieldMapperFile");
			FileInputStream configInputStream = new FileInputStream(new File(configFilePath));
			String configContent = FileUtil.getStringFromInputStream(configInputStream);
			System.out.println("configContent:" + configContent);
		} else {
			System.err.println("请输入参数。。。");
		}

	}
}
