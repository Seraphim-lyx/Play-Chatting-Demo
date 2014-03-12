package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Friend extends Model {
	@Column(length=30)
    public String user;
	@OneToMany
    public Map<String,User> myfriend;
	
	Date date;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	

	


	

	public Map<String, User> getMyfriend() {
		return myfriend;
	}

	public void setMyfriend(Map<String, User> myfriend) {
		this.myfriend = myfriend;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
	
}
