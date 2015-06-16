package com.schabiyo.pivdemo.autoscaling;


import java.security.Security;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.app.ApplicationInstanceInfo;

@Controller
@EnableAutoConfiguration
public class PivdemoApplication {

	private int requestsServed;
	public static String cryptKey = "qkjl5@2md5Q@Fqf6";
	
	@RequestMapping("/")
    String home( Model model, HttpServletRequest request) {
		CloudFactory cf = new CloudFactory();
		Cloud cloud = cf.getCloud();
		requestsServed++;
		ApplicationInstanceInfo ai = cloud.getApplicationInstanceInfo();
		model.addAttribute("instance", ai.getAppId());
		Map<String,Object> props = ai.getProperties();
		model.addAttribute("applicationName", props.get("application_name"));
		model.addAttribute("instance", props.get("instance_index"));
		model.addAttribute("ipAddress", request.getLocalAddr());
		model.addAttribute("port", props.get("port"));
		model.addAttribute("requestsServed", requestsServed);
		Map<String, Object> limits = (Map<String, Object>) props.get("limits");
		model.addAttribute("limits", limits);
		for(Map.Entry<String, Object> entry : limits.entrySet())
		{
			model.addAttribute(entry.getKey(), entry.getValue());
		}

        return "home";
    }
	
	@RequestMapping("/load")
    String load(Model model){
		 int heavyLoad = 1000;
		 while(heavyLoad > 1){
			 try {
				 encrypt();
				heavyLoad--;
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		 return "home";
	}
	
	
	@RequestMapping("/hyperload")
    String hyperload(Model model){
		 int heavyLoad = 100000;
		 while(heavyLoad > 1){
			 try {
				 encrypt();
				heavyLoad--;
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		 return "home";
	}
	
	
	public  void encrypt() throws Exception {
	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());        
	    byte[] input = " www.java2s.com ".getBytes();
	    byte[] keyBytes = PivdemoApplication.cryptKey.getBytes();
	  
	    SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
	    Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
	    System.out.println("input text : " + new String(input));

	    // encryption pass

	    byte[] cipherText = new byte[input.length];
	    cipher.init(Cipher.ENCRYPT_MODE, key);
	    int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
	    ctLength += cipher.doFinal(cipherText, ctLength);
	    System.out.println("cipher text: " + new String(cipherText) + " bytes: " + ctLength);

	    // decryption pass

	    byte[] plainText = new byte[ctLength];
	    cipher.init(Cipher.DECRYPT_MODE, key);
	    int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
	    ptLength += cipher.doFinal(plainText, ptLength);
	    System.out.println("plain text : " + new String(plainText) + " bytes: " + ptLength);
	  }
    public static void main(String[] args) {
        SpringApplication.run(PivdemoApplication.class, args);
    }
}
