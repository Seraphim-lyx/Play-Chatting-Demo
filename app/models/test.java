package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class test extends Model {
    @Column(length=30)
    private String username;
    @Column(length=24)
    private byte[] pass;
    
    private byte[] salt;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public byte[] getPass() {
		return pass;
	}
	public void setPass(byte[] pass) {
		this.pass = pass;
	}
	public byte[] getSalt() {
		return salt;
	}
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
    
    
    
}
