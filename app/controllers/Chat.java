package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.crypto.Cipher;

import play.mvc.*;
import play.cache.Cache;
import play.data.validation.*;

import models.*;

public class Chat extends Controller {
	public static Vector vector=new Vector();
	public static List<String> list=new ArrayList();
	  
	/***index.html****/
    public static void index() {
    	KeyPair kp=CryptoUtil.generateRSAKey();
    		Cache.set("key", kp);
    	
    		
    	RSAPublicKey pubky=(RSAPublicKey)kp.getPublic();
    render(pubky);
    }
    public static void fuck(){
    	Friend f=Friend.find("byUser", "admin").first();
    	Map<String,User> map=f.getMyfriend();
    
    	System.out.println(map.size());
    
    	render();
    }

   
    /****check user login*****/
    public static void login(String username,String password){
    	KeyPair kp=(KeyPair)Cache.get("key");
    	RSAPrivateKey prikey=(RSAPrivateKey)kp.getPrivate();
    	byte[] encrypted=CryptoUtil.hexToBytes(password);
    	byte[] decrypted=CryptoUtil.RSADecrypt(encrypted, prikey);
    	User user=User.find("byUsername",username).first();
    	
    	if(user!=null){
    		
    		byte[] salt=user.getSalt();
        	ByteBuffer buf=ByteBuffer.allocate(decrypted.length+salt.length);
        	buf.put(decrypted).put(salt);
        	byte[] digest=CryptoUtil.MD5Digest(buf.array());
        	for(int i=0;i<digest.length;i++){
        		if(digest[i]!=user.getPassword()[i]){
        			renderText("密码错误");
        			
        		}
        		else{	
        			session.put("user",username);
            		new Message("notice",session.get("user")+" has join the room").save();
            		vector.add(session.get("user"));
            		renderText("success");
        		}
        	}
        	
    	}
    	else{
    		renderText("用户名不存在");
    	}
    		
    	
    }
    	
    
    
    /**********ajax get user list********/
    public static void onLineUser(final String num){
    	if(Integer.parseInt(num)==vector.size()){
    		suspend("1s");
    	}
    	renderJSON(vector);
    	
    }	
   
    /*********register.html********/
    public static void register(){
    	render();
    }
    /**************register and save user message***********/
   public static void saveUser(@Valid User user,String verifypassword) throws IOException
   {     
			String pass=params.get("password");
	   		validation.required(verifypassword);
	   		validation.equals(verifypassword,pass).message("确认密码错误");
	   		if(validation.hasErrors()){
	    	 render("@register",user,verifypassword);
	   		}
	   		Random r=new Random();
	   		byte[] salt=ByteBuffer.allocate(8).putLong(r.nextLong()).array();
	   		byte[] password=pass.getBytes("utf-8");
	   		ByteBuffer buf=ByteBuffer.allocate(password.length+salt.length);
	   		buf.put(password).put(salt);
	   		byte digest[]=CryptoUtil.MD5Digest(buf.array());
	   		user.setSalt(salt);
	   		user.setPassword(digest);
	   		user.save();
	   		//注册新用户
	   		Friend friend=new Friend();
	   		friend.setUser(user.getUsername());
	   		friend.setMyfriend(new HashMap());
	   		friend.setDate(new Date());
	   		friend.save();
	   		//注册用户好友
	   		session.put("user",user.username);
	   		flash.success("Welcome"+user.username);
	   		vector.add(session.get("user"));
	   		new Message("notice",session.get("user")+" has join the room").save();
	   		chat();
   }
   /******chat.html******/
    public static void chat()
    {   
    	
    	renderArgs.put("num",list.size());
    	renderArgs.put("client",list);
    	render();
    	
    	
    }
    /*********exit and remove session***********/
	public static void out(){
		vector.remove(session.get("user"));
    	 new Message("notice", session.get("user") + " has left the room").save();
    	session.remove("user");
    	index();
    }
	/*******save message to db********/
    public static void postMessage(String message) {
        new Message(session.get("user"), message).save();
        
    }
    /********ajax to get message*********/
    public static void newMessages() {
        List<Message> messages = Message.find("date > ?", request.date).fetch();
        if(messages.isEmpty()){
        	suspend("1s");
        }
        renderJSON(messages);
    }
    /********check whether is registered*******/
    public static void checkName(String message){ 
       User user=User.find("byUsername",message).first();
       String res=null;
    	if(user!=null){
    		renderText("NO");
    	}
    	else{
    		renderText("YES");
    	}
    
    	
    }
    /********User_Message.html*********/
    public static void User_Message(String username){
    	Boolean isfriend=false;
    	User user=User.find("byUsername", username).first();
    	Friend friend=Friend.find("byUser", session.get("user")).first();
    	if(user!=null){
    		if(friend!=null){
    			if(friend.getMyfriend().containsKey(username)){
    				System.out.println(username);
    				isfriend=true;
    			}
    		}
    		renderArgs.put("isfriend",isfriend);
    		render(user);
    	}
    	else{
    		renderText("error");
    	}
    		
    	
    	
    }
    public static void SavePrivateMessage(){
    	String to=params.get("to");
    	String from=session.get("user");
    	String message=params.get("message");
    	PrivateMessage msg=new PrivateMessage();
    	msg.setTowho(to);
    	msg.setFromwho(from);
    	msg.setMessage(message);
    	msg.setDate(new Date());
    	msg.setChecked(0);
    	msg.save();
    	PrivateMessage();
    	
    	
    }
    public static void PrivateMessage(){
    	render();
    }
    public static void GetPrivateMessage(){
    	List<PrivateMessage> list=PrivateMessage.find("Towho = ? and checked = ? order by date desc","asd",0).fetch();
    	render(list);
    	
    }
    public static void SaveFriend(){
    	String user=session.get("user");
    	String friend=params.get("friend");
    	User u=User.find("byUsername",friend).first();
    	Friend fr=Friend.find("byUser",user).first();
    	Map<String,User> map=fr.getMyfriend();
    	map.put(friend,u);
    	fr.setMyfriend(map);
    	fr.save();
    	User_Message(friend);
    }
    

}
