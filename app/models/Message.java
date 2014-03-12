package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Message extends Model {
	public String user;
	public String text;
	public Date date;
	public Message(String user,String text)
	{
		this.user=user;
		this.text=text;
		this.date=new Date();
	}
    
}