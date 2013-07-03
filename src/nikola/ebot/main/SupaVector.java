
package nikola.ebot.main;
/*
Copyright 2002 Steve Jolly

Released under GPLV2 Licence
*/

import java.io.*;
import java.util.*;

public class SupaVector extends Vector {

	public SupaVector() {
	}

	public SupaVector (List elements) {
		this.addAll(elements);
	}

	public String toString() {
		int i=0;
		StringBuffer outstring = new StringBuffer(1024);
		for(i=0;i<this.size();i++) {
			if (i!=0) outstring.append(" ");
			outstring.append(this.elementAt(i));
		}
		return outstring.toString();
	}

	public boolean contains(String test) {
		int i=0;
		if (test.indexOf(" ")!=-1) return this.contains(test.split(" "));
		for(i=0;i<this.size();i++) {
			if (test.equalsIgnoreCase((String)this.elementAt(i))) return true;
		}
		return false;
	}

	public boolean contains(String[] test) {
		int i=0;
		int j=0;
	
		j = this.indexOf(test[0]);
		if (j==-1) {
			return false;
		}
		if (test.length>(this.size()-j)) return false;
		j++;
		for (i=1;i<test.length;i++,j++) {
			if (!test[i].equalsIgnoreCase((String)this.elementAt(j))) {
				return false;
			}
		}
		return true;
	}

	public void addAll(String line) {
		this.addAll(line.split("[\\s]+"));
	}

	public void addAll(String[] list) {
		int i;
		for (i=0;i<list.length;i++) {
			this.add(list[i]);
		}
	}

	public boolean read(String lfilename) {
		SupaVector tempcopy;
	try {
		File lfile = new File(lfilename);
		FileInputStream listfile = new FileInputStream(lfile);
		ObjectInputStream s = new ObjectInputStream(listfile);
		tempcopy=(SupaVector)s.readObject();
		s.close();
		listfile.close();
		this.addAll(tempcopy);
		return true;
	}
	catch (IOException e){
	}
	catch (ClassNotFoundException e){ //can't happen, but java's stupid
	}
		return false;
	}

	public boolean write(String lfilename) {
//		UserList tempcopy=new UserList();
	try {
		File lfile = new File(lfilename);
		lfile.createNewFile();
		FileOutputStream listfile = new FileOutputStream(lfile);
		ObjectOutputStream s = new ObjectOutputStream(listfile);
//		tempcopy.addAll(this);
		s.writeObject(this);
		s.flush();
		s.close();
		listfile.close();
		return true;
	}
	catch (IOException e){
	}
		return false;
	}

}
