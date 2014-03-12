package models;

import play.*;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="user")
public class User extends GenericModel {
	@Id
	@GeneratedValue
	public Integer userid;
	@Required
	@MinSize(3)
	@Column(length=40)
	public String username;
    @MinSize(3)	
	public byte[] password;
	
	public byte[] salt;
	
	public String sex;
	
	public String mysign;
	
	public String age;
	
	public String favor;
	
	public String province;
	
	public String city;
	
	public String mail;

	public String context;
	
	
	
	
	public String getMysign() {
		return mysign;
	}
	public void setMysign(String mysign) {
		this.mysign = mysign;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public byte[] getPassword() {
		return password;
	}
	public void setPassword(byte[] password) {
		this.password = password;
	}
	public byte[] getSalt() {
		return salt;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getFavor() {
		return favor;
	}

	public void setFavor(String favor) {
		this.favor = favor;
	}
    
}