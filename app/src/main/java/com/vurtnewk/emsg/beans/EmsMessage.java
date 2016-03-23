
package com.vurtnewk.emsg.beans;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;


/*	
 CREATE TABLE message (
 _id INTEGER PRIMARY KEY, 
 mid TEXT unique,
 jid_from TEXT,
 jid_to TEXT,
 gid TEXT, 
 type INTEGER, 
 ct INTEGER, 
 content_type TEXT,
 content_length TEXT,
 content TEXT	
 )
 */
public class EmsMessage implements Parcelable {

    /**
	 * 
	 */

    private long id;
    private String mid = null;
    private String mAccFrom = null;
    public String getmAccFrom() {
        return mAccFrom;
    }

    public void setmAccFrom(String mAccFrom) {
        this.mAccFrom = mAccFrom;
    }

    public String getmAccTo() {
        return mAccTo;
    }

    public void setmAccTo(String mAccTo) {
        this.mAccTo = mAccTo;
    }

    private String mAccTo = null;
    private String gid = null;
    private int type = 0;
    private long ct = 0;
    private String contentType = null;
    private String contentLength = null;
    private String content = null;
    private Uri mFileUri;
    
    private HashMap<String,String> mExtendsMap;

    public HashMap<String, String> getmExtendsMap() {
		return mExtendsMap;
	}

	public void setmExtendsMap(HashMap<String, String> mExtendsMap) {
		this.mExtendsMap = mExtendsMap;
	}

    public Uri getmFileUri() {
        return mFileUri;
    }

    public void setmFileUri(Uri mFileUri) {
        this.mFileUri = mFileUri;
    }

    public EmsMessage() {
        super();
    }

    public EmsMessage(String mid, String mAccFrom, String mAccTo, String gid, int type, long ct) {
        super();
        this.mid = mid;
        this.mAccFrom = mAccFrom;
        this.mAccTo = mAccTo;
        this.gid = gid;
        this.type = type;
        this.ct = ct;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCt() {
        return ct;
    }

    public void setCt(long ct) {
        this.ct = ct;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentLength() {
        return contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel mParcel, int arg1) {
        mParcel.writeString(mid);
        mParcel.writeString(mAccFrom);
        mParcel.writeString(mAccTo);
        mParcel.writeString(gid);
        mParcel.writeString(contentType);
        mParcel.writeString(content);
        mParcel.writeString(contentLength);
        mParcel.writeInt(type);
        mParcel.writeLong(ct);
        mParcel.writeMap(mExtendsMap);
        
    }

    public static final Creator<EmsMessage> CREATOR = new Creator<EmsMessage>() {
        public EmsMessage createFromParcel(Parcel source) {
            EmsMessage message = new EmsMessage();
            message.mid = source.readString();
            message.mAccFrom = source.readString();
            message.mAccTo = source.readString();
            message.gid = source.readString();
            message.contentType = source.readString();
            message.content = source.readString();
            message.contentLength = source.readString();
            message.type = source.readInt();
            message.ct = source.readLong();
            message.mExtendsMap=source.readHashMap(HashMap.class.getClassLoader());
            return message;
        }

        public EmsMessage[] newArray(int size) {
            return new EmsMessage[size];
        }
    };
}
