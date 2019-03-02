package com.shinetvbox.vod.db;

import android.os.Parcel;
import android.os.Parcelable;


public class Singer implements Parcelable {
	public Singer(){
		reset();
	}
	public Singer(Parcel in) {
		readFromParcel(in);
	}

	public Singer(String singer_id, String singer_name, String singer_sex, String singer_region_new, String local_click_rank){
		this.singer_id = singer_id;
		this.singer_name = singer_name;
		this.singer_sex = singer_sex;
		this.singer_region_new = singer_region_new;
		this.local_click_rank = local_click_rank;
		//this.singer_id = singer_id;
	}

	/* shine element*/
	public String singer_id;
	public String singer_name;
	public String singer_sex;
	public String singer_region_new;
	public String local_click_rank;
	//public String singer_id;

	public String toString(){
		return "";
	}

	public void reset(){
		this.singer_id = null;
		this.singer_name = null;
		this.singer_sex = null;
		this.singer_region_new = null;
		this.local_click_rank = null;
		//this.singer_id = null;
	}

	public static final Creator<Singer> CREATOR = new Creator<Singer>() {

		@Override
		public Singer createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Singer(source);
		}

		@Override
		public Singer[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Singer[size];
		}
		
	};
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(this.singer_id==null?"":this.singer_id);
		dest.writeString(this.singer_name==null?"":this.singer_name);
		dest.writeString(this.singer_sex==null?"":this.singer_sex);
		dest.writeString(this.singer_region_new==null?"":this.singer_region_new);
		dest.writeString(this.local_click_rank==null?"":this.local_click_rank);
		//dest.writeString(this.singer_id==null?"":this.singer_id);
	}
	
	public void readFromParcel(Parcel dest){
		this.singer_id = dest.readString();
		this.singer_name = dest.readString();
		this.singer_sex = dest.readString();
		this.singer_region_new = dest.readString();
		this.local_click_rank = dest.readString();
		//this.singer_id = dest.readString();
	}
}
