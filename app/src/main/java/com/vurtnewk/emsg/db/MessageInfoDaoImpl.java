package com.vurtnewk.emsg.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.vurtnewk.emsg.beans.MessageInfoEntity;
import com.vurtnewk.emsg.beans.MsgSessionEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageInfoDaoImpl {

    private EMSGDBHelper dbHelper;
    private SQLiteDatabase database = null;
    private String updatasql;
    private static final int DB_VERSION = 1;

    public MessageInfoDaoImpl(Context context) {
        dbHelper = new EMSGDBHelper(context, DB_VERSION);//此操作只为建表
    }


    public boolean AddMsgInfo(MessageInfoEntity entity) {
        //插入之前先判断是否有重复
        String s = "select * from message_info_table where msg_id = '" + entity.getId() + "'";
        boolean flag = false;
        String sql = "insert into message_info_table(sid ,type,msg_state ,myjid ,jid ,mynickname ,nickname ,myheadurl ,"
                + "headurl,msg_content,msg_type,msg_readorno,msg_time,voice_time,msg_GeoLat,msg_GeoLng,msg_imageUrlId,msg_id,msg_attr) values ('"
                + entity.getSid()
                + "','"
                + entity.getType()
                + "','"
                + entity.getMsg_state()
                + "','"
                + entity.getMyjid()
                + "','"
                + entity.getJid()
                + "','"
                + entity.getMynickname()
                + "','"
                + entity.getNickname()
                + "','"
                + entity.getMyheadurl()
                + "','"
                + entity.getHeadurl()
                + "',?,'"
                + entity.getMsg_type()
                + "','"
                + entity.getMsg_readorno()
                + "','"
                + entity.getMsg_time()
                + "','"
                + entity.getVoice_time()
                + "','"
                + entity.getMsg_GeoLat()
                + "','"
                + entity.getMsg_GeoLng()
                + "','"
                + entity.getMsg_imageUrlId()
                + "','"
                + entity.getId()
                + "','"
                + entity.getAttr()
                + "');";
        try {
            database = dbHelper.getWritableDatabase();
            Cursor cursor = database.rawQuery(s, null);
            if (entity.getId() == null) {//插入自己发的数据时..没ID
                database.execSQL(sql, new Object[]{entity.getMsg_content()});
                flag = true;
            } else {
                if (cursor.getCount() != 0) {
                    flag = false;
                } else {
                    database.execSQL(sql, new Object[]{entity.getMsg_content()});
                    flag = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return flag;
    }

    /**
     * 插入回话数据库
     *
     * @param entity
     * @return
     */
    public boolean AddMsgSession(MsgSessionEntity entity) {
        String sid = "1";
        String selectsql = "select * from message_session_table where sid='"
                + entity.getSid() + "'";
        if (TextUtils.isEmpty(entity.getNickname())) {
            updatasql = "update message_session_table SET mynickname='"
                    + entity.getMynickname()
                    + "',myheadurl='"
                    + entity.getMyheadurl()
                    + "',msg_content=?"
                    + ",msg_type='"
                    + entity.getMsg_type()
                    + "',type='"
                    + entity.getType()
                    + "',msg_lasttime='"
                    + entity.getMsg_lasttime() +
                    "',msg_noread_num='"
                    + entity.getMsg_noread_num() +
                    "',msg_attr='"
                    + entity.getAttr() +
                    "' where sid='"
                    + entity.getSid() + "'";
        } else {
            updatasql = "update message_session_table SET mynickname='"
                    + entity.getMynickname()
                    + "',nickname='"
                    + entity.getNickname()
                    + "',myheadurl='"
                    + entity.getMyheadurl()
                    + "',headurl='"
                    + entity.getHeadurl()
                    + "',msg_content=?"
                    + ",msg_type='"
                    + entity.getMsg_type()
                    + "',type='"
                    + entity.getType()
                    + "',msg_lasttime='"
                    + entity.getMsg_lasttime() +
                    "',msg_noread_num='"
                    + entity.getMsg_noread_num() +
//                    "',sex='"
//                    + entity.getSex() +
//                    "',age='"
//                    + entity.getAge() +
                    "',msg_attr='"
                    + entity.getAttr() +
                    "' where sid='"
                    + entity.getSid() + "'";
        }

        String insertsql = "insert into message_session_table" +
                "(sid ,type ,myjid ,jid ,mynickname ,nickname ,myheadurl ,"
                + "headurl,msg_content,msg_type,msg_lasttime,msg_noread_num,msg_attr ) values ('"
                + entity.getSid()
                + "','"
                + entity.getType()
                + "','"
//                + entity.getMyqiuyouno()
//                + "','"
//                + entity.getQiuyouno()
//                + "','"
                + entity.getMyjid()
                + "','"
                + entity.getJid()
                + "','"
                + entity.getMynickname()
                + "','"
                + entity.getNickname()
                + "','"
                + entity.getMyheadurl()
                + "','"
                + entity.getHeadurl()
                + "',?,'"
                + entity.getMsg_type()
                + "','"
                + entity.getMsg_lasttime()
                + "','"
                + entity.getMsg_noread_num()
                + "','"
//                + entity.getSex()
//                + "','"
//                + entity.getAge()
                + entity.getAttr()
                + "');";

        try {
            database = dbHelper.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectsql, null);
            if (cursor.moveToNext()) {
                sid = cursor.getString(1);
            }
            if (sid.equals("1")) {
                database.execSQL(insertsql, new Object[]{entity.getMsg_content()});
            } else {
                database.execSQL(updatasql, new Object[]{entity.getMsg_content()});
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return false;

    }


    public int getMsgNotReadNum(String sid) {
        int nums = 0;
        String sql = "select count(1) from message_info_table where sid='"
                + sid + "' and msg_readorno='0'";
        System.out.println(sql + " select");
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            nums = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return nums;
    }


    public int getAllMsgNotReadNum(String myqiuyouno) {
        int nums = 0;
        String sql = "select count(1) from message_info_table  where  msg_readorno='0' and " +
                "myqiuyouno =" + myqiuyouno + " and (type ='1' or type = '2' or type = '107')";
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            nums = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        int frnums = getFriendsMsgNotReadNum(myqiuyouno);
        nums = nums + frnums;
        return nums;
    }


    public int getFriendsMsgNotReadNum(String myqiuyouno) {
        int nums = 0;
        String sql = "select count(distinct(sid)) from message_info_table where msg_readorno='0' and " +
                "myqiuyouno =" + myqiuyouno + " and (type ='100' or type = '109' or type = '108'or type='110' )";
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
            nums = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return nums;
    }


    public boolean updateMsgReadOrNo(String sid) {
        boolean flag = false;
        int notread = 0;
        String sql = "update message_info_table  set msg_readorno='1' where  sid='"
                + sid
                + "'";
        String selectsql = "select * from  message_info_table where msg_readorno='0' and sid='"
                + sid
                + "'";
        String updatesql = "update message_session_table  set msg_noread_num='"
                + notread
                + "' where  sid='"
                + sid
                + "'";
        try {
            database = dbHelper.getWritableDatabase();
            database.execSQL(sql);
            Cursor cursor = database.rawQuery(selectsql, null);
            if (cursor.moveToNext()) {
                notread = cursor.getCount();
            }
            database.execSQL(updatesql);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return flag;
    }

    public List<Map<String, String>> listAllMsgSession(String[] selectionArgs, String myqiuyouno) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = "select * from message_session_table where myqiuyouno =" + myqiuyouno + " order by msg_lasttime desc";
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, selectionArgs);
            int col = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < col; i++) {

                    String col_name = cursor.getColumnName(i);
                    String col_value = cursor.getString(cursor
                            .getColumnIndex(col_name));
                    if (col_value == null) {
                        col_value = "";
                    }
                    map.put(col_name, col_value);
                }

                list.add(map);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return list;
    }


    public List<Map<String, String>> listMsgSession(String[] selectionArgs, String myqiuyouno, String type1, String type2) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = "select * from message_session_table where myqiuyouno =" + myqiuyouno + " and type in(" + type1 + "," + type2 + ") order by msg_lasttime desc";
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, selectionArgs);
            int col = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < col; i++) {
                    String col_name = cursor.getColumnName(i);
                    String col_value = cursor.getString(cursor
                            .getColumnIndex(col_name));
                    if (col_value == null) {
                        col_value = "";
                    }
                    map.put(col_name, col_value);
                }

                list.add(map);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return list;
    }

    public List<Map<String, String>> SearchMsgSession(String[] selectionArgs, String myjid, String type) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = "select * from message_session_table where myjid = '" + myjid + "' and type in(" + type + " ) order by msg_lasttime desc";
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, selectionArgs);
            int col = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < col; i++) {
                    String col_name = cursor.getColumnName(i);
                    String col_value = cursor.getString(cursor
                            .getColumnIndex(col_name));
                    if (col_value == null) {
                        col_value = "";
                    }
                    map.put(col_name, col_value);
                }
                list.add(map);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return list;
    }


    public List<Map<String, String>> listMessageInfo(String[] selectionArgs, String sid, String type, int start, int end) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = " select   * from(select   * from message_info_table where sid = '" + sid + "' and type in ( " + type + " ) order by id desc limit " + start + ",10)  order by id";
        System.out.println(sql + " select");
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, selectionArgs);
            int col = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < col; i++) {

                    String col_name = cursor.getColumnName(i);
                    String col_value = cursor.getString(cursor
                            .getColumnIndex(col_name));
                    if (col_value == null) {
                        col_value = "";
                    }
                    map.put(col_name, col_value);
                }

                list.add(map);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return list;
    }

    public List<Map<String, String>> listMessageInfo(String[] selectionArgs, String
            myqiuyouno, int start, int end, String type) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = "select * from message_info_table where myqiuyouno =" + myqiuyouno + " and type in ( " + type + " ) " + " order by id desc " + " limit " + start + " , " + end;
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, selectionArgs);
            int col = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < col; i++) {

                    String col_name = cursor.getColumnName(i);
                    String col_value = cursor.getString(cursor.getColumnIndex(col_name));
                    if (col_value == null) {
                        col_value = "";
                    }
                    map.put(col_name, col_value);
                }

                list.add(map);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return list;
    }

    public List<Map<String, String>> listMessageInfo2(String[] selectionArgs, String qiuyouno, String
            myqiuyouno) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = "select * from message_info_table where myqiuyouno =" + myqiuyouno + " group by " + qiuyouno + " order by id desc";
        System.out.println(sql + " select");
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(sql, selectionArgs);
            int col = cursor.getColumnCount();
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < col; i++) {

                    String col_name = cursor.getColumnName(i);
                    String col_value = cursor.getString(cursor
                            .getColumnIndex(col_name));
                    if (col_value == null) {
                        col_value = "";
                    }
                    map.put(col_name, col_value);
                }

                list.add(map);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        return list;
    }

    public boolean DeleteMsgInfo(String[] selectionArgs, String sid) {
        String sql = "delete from message_info_table  where sid = '" + sid + "'";

        try {
            database = dbHelper.getReadableDatabase();
            database.execSQL(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return false;
    }

    public boolean DeleteMsgInfo2(String[] selectionArgs, String type) {
        String sql = "delete from message_info_table  where type='" + type + "'";

        try {
            database = dbHelper.getReadableDatabase();
            database.execSQL(sql);
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return false;
    }

    public boolean DeleteMsgInfo(String[] selectionArgs, String sid, String type) {
        String sql = "delete from message_info_table  where sid='" + sid + "' and type='" + type + "'";
        try {
            database = dbHelper.getReadableDatabase();
            database.execSQL(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return false;

    }

    public boolean DeleteMsgSession(String[] selectionArgs, String sid, String type) {
        String sql = "delete from message_session_table  where sid='" + sid + "' and type='" + type + "'";
        try {
            database = dbHelper.getReadableDatabase();
            database.execSQL(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return false;

    }

    public boolean DeleteMsgSession(String[] selectionArgs, String sid) {
        String sql = "delete from message_session_table  where sid= '" + sid + "'";

        try {
            database = dbHelper.getReadableDatabase();
            database.execSQL(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }

        return false;

    }

    /**
     * 同意添加好友后，更新消息的类型
     *
     * @param selectionArgs
     * @param sid
     */
    public void updateMsgType(String[] selectionArgs, String type, String msg_noread_num, String sid) {
        String sql = "update message_session_table set type = '" + type + "', msg_noread_num = '" + msg_noread_num + "' where sid = '" + sid + "'";
        try {
            database = dbHelper.getReadableDatabase();
            database.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }


    public void updateMsgAfterAgree(String type, String msg_noread_num, String msg_content, String sid) {
        String sql = "update message_session_table set type = '" + type + "', msg_noread_num = '" + msg_noread_num + "', msg_content = '" + msg_content + "' where sid = '" + sid + "'";
        try {
            database = dbHelper.getReadableDatabase();
            database.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }


    /**
     * 查询指定类型的消息
     *
     * @param isfriend
     * @param sid
     */

    public boolean getMsgSessionByType(String isfriend, String sid) {
        String selectsql = "select * from  message_session_table where isfriend='" + isfriend + "' and sid='"
                + sid + "'";
        int count = -1;
        try {
            database = dbHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery(selectsql, null);
            count = cursor.getCount();

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
        if (count > 0) {
            return true;
        } else {
            return false;
        }

    }

    public void updateMsgWhenDeleted(String isfriend, String sid) {
        String sql = "update message_session_table set isfriend = '" + isfriend + "' where sid = '" + sid + "'";
        try {
            database = dbHelper.getReadableDatabase();
            database.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

}
