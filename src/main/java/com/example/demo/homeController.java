package com.example.demo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
public class homeController {
	
	@Autowired
	UserRepo userrepo;
	@RequestMapping("/")
	public ModelAndView home() {
	ModelAndView mv1 = new ModelAndView("home.html");	
	return mv1;
	}
	@RequestMapping(value="/addUser", method=RequestMethod.POST)
	public ModelAndView addUser(@RequestParam("file") MultipartFile file, User user) {
		
		ModelAndView mv1 = new ModelAndView();	
		int wc=0;
		String err="";
		String username="";
		File convertFile = new File("/var/tmp/Files/"+file.getOriginalFilename());
		String filepath="/var/tmp/Files/"+file.getOriginalFilename();
	      try {
	    	  convertFile.createNewFile();
			  FileOutputStream fout = new FileOutputStream(convertFile);
			  fout.write(file.getBytes());
			  fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      try {
			File f1=new File("/var/tmp/Files/"+file.getOriginalFilename()); 
			  String[] words=null; 
			       
			  FileReader fr = new FileReader(f1); 
			  BufferedReader br = new BufferedReader(fr);
			  String s;
			  while((s=br.readLine())!=null) 
			  {
			     words=s.split(" ");
			     wc=wc+words.length; 
			  }
			  fr.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user.setWc(wc);
		user.setFilepath(filepath);
		username=user.getUsername().trim();
		user.setUsername(username);
		userrepo.save(user);
		mv1.addObject("obj", user);
		mv1.setViewName("land.html");
		return mv1;
		
	}
	@RequestMapping(value="/fileDownload", method=RequestMethod.GET)
	public ResponseEntity<Object> fileDownload(@RequestParam("filepath") String filepath) throws IOException{
		String filename = filepath;
	      File file = new File(filename); 
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			  HttpHeaders headers = new HttpHeaders();	  
			  headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
			  headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			  headers.add("Pragma", "no-cache");
			  headers.add("Expires", "0");
			  ResponseEntity<Object> responseEntity = ResponseEntity.ok().headers(headers).contentLength(
			     file.length()).contentType(MediaType.parseMediaType("application/txt")).body(resource);
			  return responseEntity;
	   }
	
	@RequestMapping(value="/getUser",method = RequestMethod.GET)
	public ModelAndView getUser(User user) {
		ModelAndView mv2 = new ModelAndView();
		String s="",s2="";
		User user2 = new User();
		String username="";
		username=user.getUsername().trim();
		try {
			user2 = userrepo.findByUsername(username);
			mv2.addObject("obj2",user2);
			s=user.getPassword();
			s2=user2.getPassword();
			int k= s.compareTo(s2);
			if(k==0) {
				mv2.addObject("obj2",user2);
				mv2.setViewName("fetch.html");
			}
			else {
				mv2.addObject("err", "Invalid User Name/Password OR Make sure UserName and Passowrd does not have extra spaces");
				mv2.setViewName("home.html");
			}
		
		}catch(NullPointerException e){
			mv2.addObject("err", "Invalid User Name/Password OR Make sure UserName and Passowrd does not have extra spaces");
			mv2.setViewName("home.html");
			return mv2;
		}
		return mv2;
	}
	
	

}
