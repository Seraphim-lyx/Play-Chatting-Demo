package controllers;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.Friend;
import models.PrivateMessage;
import models.User;
import play.libs.Crypto;
import play.mvc.*;

public class UserManage extends Controller {
	public static String message="";
    public static void index() {
        render();
    }
    public static void ManageMain(String defaultLoad){
    	if(defaultLoad==null) defaultLoad="";
    	System.out.println(defaultLoad);
    	 String msg=message;
    	 message="";
    	render(msg,defaultLoad);
    }
    public static void UserMessage(){
    	User user=User.find("byUsername",session.get("user")).first();
    	render(user);
    }
    public static void UpdateUserMessage(){
    	String mysign=params.get("mysign");
    	String sex=params.get("sex");
    	String age=params.get("age");
    	String province=params.get("province");
    	String city=params.get("city");
    	String mail=params.get("mail");
    	String favor=params.get("favor");
    	String context=params.get("context");
    	//获取参数
    	User user=User.find("byUsername",session.get("user")).first();
    		user.setMysign(mysign);
    		user.setSex(sex);
    		user.setAge(age);
    		user.setProvince(province);
    		user.setCity(city);
    		user.setMail(mail);	
    		user.setFavor(favor);
    		user.setContext(context);
    	user.save();
    	//更新信息
    	ManageMain("UserMessage");
    	
    }
    public static void ChangePassword(){
    	render();
    }
    public static void DoChangePwd(String oldpwd,String newpwd,String validpwd){
    	try{
    		User user=User.find("byUsername",session.get("user")).first();
    		byte []salt=user.getSalt();
    		byte []pass=oldpwd.getBytes("utf-8");
    		ByteBuffer buf=ByteBuffer.allocate(salt.length+pass.length);
    		buf.put(pass).put(salt);
    		byte[] pwd=CryptoUtil.MD5Digest(buf.array());
    		for(int i=0;i<pwd.length;i++){
    			if(pwd[i]!=user.getPassword()[i]){
    				message="旧密码错误！";
    				ManageMain("");
    				return;
    			}
    		}
    		//判断旧密码正确性
    		pass=newpwd.getBytes("utf-8");
    		buf.clear();
    		buf=ByteBuffer.allocate(salt.length+pass.length);
    		buf.put(pass).put(salt);
    		pwd=CryptoUtil.MD5Digest(buf.array());
    		user.setPassword(pwd);
    		user.save();
    		message="修改成功！";
    		ManageMain("");
    		//更改密码
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    public static void FriendList(String page){ 
//    	page=params.get("page");
    	if(page==null) page="1";
    	if(Integer.parseInt(page)<1) page="1";
    	Friend friend=Friend.find("byUser",session.get("user")).first();
    	Map<String,User> map=friend.getMyfriend();
    	Collection<User> c=map.values();
    	Iterator iter=c.iterator();
    	List<User> list=new ArrayList();
    	List<User> user=new ArrayList();
    	while(iter.hasNext()){
    		list.add((User)iter.next());
    	}
    	Integer total=list.size()%10==0?list.size()/10:list.size()/10+1;
    	for(int i=(Integer.parseInt(page)-1)*10;i<10*Integer.parseInt(page)&&i<list.size();i++){
    		user.add(list.get(i));
    	}
    	renderArgs.put("page",Integer.parseInt(page));
    	renderArgs.put("total",total);
    	render(user);
    }
    public static void RemoveFriend(String username,String page){
    	Friend f=Friend.find("byUser", session.get("user")).first();
    	if(f!=null){
    		Map<String,User> map=f.getMyfriend();
    		if(map.containsKey(username)){
    			map.remove(username);
    		}
    		f.setMyfriend(map);
    		f.save();
    	}
    	FriendList(page);
    }
    
    public static void ShowMessageList(){
    	/*
    	 * 返回消息列表
    	 * 
    	 */
    	String check=params.get("check");
    	Integer checked=0;//判断未读信息
    	List<PrivateMessage> list=null;
    	if(Integer.parseInt(check)==0){
    		list=PrivateMessage.find("towho = ? and checked = ? order by date desc",session.get("user"),Integer.parseInt(check)).fetch();
    		//返回未读信息
    	}
    	else{
    		list=PrivateMessage.find("towho=? order by date desc",session.get("user")).fetch();
    		//返回所有信息
    		checked=1;
    	}
    	renderArgs.put("checked",checked);
    	render(list);
    }
    public static void PvMessage(){
    	render();
    }
    public static void ShowMessage(){
    	String id=params.get("id");
    	PrivateMessage pv=PrivateMessage.find("id=?",Integer.parseInt(id)).first();
    	if(pv.getChecked()==0){
    		pv.setChecked(1);
    		pv.save();
    	}
    	render(pv);
    }
    public static void test(){
    	render();
    }
    public static void tt(){
    	String text=params.get("test").replaceAll(" ","&nbsp;").replaceAll("\r","<br/>");
    	renderArgs.put("text",text);
    	render();
    }
    public static void SendMessage(String username){
    	render(username);
    }
    public static void SaveSendMessage(String towho,String title,String message){
    	message=message.replaceAll(" ", "&nbsp;").replaceAll("\r","<br/>");
    	PrivateMessage pv=new PrivateMessage();
    		pv.setFromwho(session.get("user"));
    		pv.setTowho(towho);
    		pv.setTitle(title);
    		pv.setMessage(message);
    		pv.setDate(new Date());
    		pv.setChecked(0);
    		pv.save();
    		//数据库同步发送信息
    	renderText("OK");    
    }
    public static void DeleteMessage(String id){
    	PrivateMessage pv=PrivateMessage.find("id=?", Integer.parseInt(id)).first();
    	if(pv!=null){
    		pv.delete();
    		renderText("删除成功！");
    	}
    	else{
    		renderText("error");
    	}
    }
}
