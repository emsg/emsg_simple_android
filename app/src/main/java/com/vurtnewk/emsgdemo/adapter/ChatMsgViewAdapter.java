
package com.vurtnewk.emsgdemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vurtnewk.emsg.beans.MessageInfoEntity;
import com.vurtnewk.emsg.util.ImageBase64;
import com.vurtnewk.emsg.util.SmileUtils;
import com.vurtnewk.emsg.util.ThumbExtractor;
import com.vurtnewk.emsgdemo.R;
import com.vurtnewk.emsgdemo.ui.RoundAngleImageView;
import com.vurtnewk.emsgdemo.utils.DateUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 修改by  dxn，修改：时间戳
 */
public class ChatMsgViewAdapter extends BaseAdapter {

    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }

    protected ImageLoader mImgeLoader;
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private List<MessageInfoEntity> coll;
    private String completeflag = "no";
    private Context ctx;

    private LayoutInflater mInflater;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private AnimationDrawable animationDrawable;
    Long l1;
    Long l2;

    public ChatMsgViewAdapter(Context context, List<MessageInfoEntity> coll) {
        ctx = context;
        this.coll = coll;
        mInflater = LayoutInflater.from(context);
        mImgeLoader = ImageLoader.getInstance();//准备ImageLoader

    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        MessageInfoEntity entity = coll.get(position);

        if (entity.getMsg_state()) {
            return IMsgViewType.IMVT_COM_MSG;
        } else {
            return IMsgViewType.IMVT_TO_MSG;
        }

    }

    public int getViewTypeCount() {
        return 2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final MessageInfoEntity entity = coll.get(position);
        final boolean isComMsg = entity.getMsg_state();
        ViewHolder viewHolder = null;
        if (convertView == null) {
            if (isComMsg) {
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_left, null);
            } else {
                convertView = mInflater.inflate(
                        R.layout.chatting_item_msg_right, null);
            }
            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView
                    .findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName = (TextView) convertView
                    .findViewById(R.id.tv_username);
            viewHolder.tvText = (TextView) convertView
                    .findViewById(R.id.tv_chat_text);
            viewHolder.tvImage = (ImageView) convertView
                    .findViewById(R.id.tv_chat_image);
            viewHolder.tvTime = (TextView) convertView
                    .findViewById(R.id.tv_time);
            viewHolder.iv_userhead = (RoundAngleImageView) convertView.findViewById(R.id.iv_userhead);
//            viewHolder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
//            viewHolder.row_rec_location = (LinearLayout) convertView.findViewById(R.id.row_rec_location);
            viewHolder.tv_chatcontent = (LinearLayout) convertView.findViewById(R.id.tv_chatcontent);
            viewHolder.isComMsg = isComMsg;

//            if (isComMsg) {
//                viewHolder.mLlNotify = convertView.findViewById(R.id.mLlNotify);
//                viewHolder.mRlView = convertView.findViewById(R.id.mRlView);
//                viewHolder.mTvTitle = (TextView) convertView.findViewById(R.id.mTvTitle);
//                viewHolder.mTvDate = (TextView) convertView.findViewById(R.id.mTvDate);
//                viewHolder.mTvName = (TextView) convertView.findViewById(R.id.mTvName);
//            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            viewHolder.tvSendTime.setVisibility(View.VISIBLE);
            viewHolder.tvSendTime.setText(entity.getMsg_time());
        } else {
            //当时间小于5分钟，不显示
            String time = DateUtils.getTwoDay(coll.get(position).getMsg_time(), coll.get(position - 1).getMsg_time());

            if (Integer.valueOf(time) < 5) {
                viewHolder.tvSendTime.setVisibility(View.GONE);
            } else {
                viewHolder.tvSendTime.setText(entity.getMsg_time());
                viewHolder.tvSendTime.setVisibility(View.VISIBLE);
            }
        }

        if (isComMsg) {
            mImgeLoader.displayImage(entity.getHeadurl(), viewHolder.iv_userhead);
//            viewHolder.mLlNotify.setVisibility(View.GONE);
//            viewHolder.mRlView.setVisibility(View.VISIBLE);
        } else {
            mImgeLoader.displayImage(entity.getMyheadurl(), viewHolder.iv_userhead);
        }

        if (entity.getMsg_type() != null && entity.getMsg_type().equals("audio")) {
            final ImageView tvImage = viewHolder.tvImage;
            if (isComMsg) {
                viewHolder.tvImage.setVisibility(View.VISIBLE);
                viewHolder.tvImage.setImageResource(R.drawable.chatfrom_voice_playing);
            } else {
                viewHolder.tvImage.setVisibility(View.VISIBLE);
                viewHolder.tvImage.setImageResource(R.drawable.chatto_voice_playing);
            }
            final String content = entity.getMsg_content();
            new File(android.os.Environment.getExternalStorageDirectory() + "/yueqiu/emsg/receive/audio/")
                    .mkdirs();
            new File(android.os.Environment.getExternalStorageDirectory() + "/emsg/send/audio/")
                    .mkdirs();
            viewHolder.tvText.setText("");
            viewHolder.tvText.setVisibility(View.GONE);
            viewHolder.tvImage.setVisibility(View.VISIBLE);
            viewHolder.tvTime.setText(entity.getVoice_time() + "''");
            if (isComMsg) {
                String filename = android.os.Environment.getExternalStorageDirectory()
                        + "/yueqiu/emsg/receive/audio/" + getKeyFroComingMsg(content);
                if (!new File(filename).exists()) {
                    new AudioTask(filename).execute(content);

                } else {
                    /**
                     * 修改播放后无法播放的问题
                     */
                    completeflag = "complete";
                }
            }
            viewHolder.tvImage.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    if (isComMsg) {
                        if (completeflag.equals("complete")) {
                            tvImage.setImageResource(R.drawable.voice_from_icon);
                            animationDrawable = (AnimationDrawable) tvImage.getDrawable();
                            animationDrawable.start();
                            playMusic(android.os.Environment.getExternalStorageDirectory()
                                    + "/yueqiu/emsg/receive/audio/" + getKeyFroComingMsg(content), tvImage, 1);
                        } else {
                            Toast.makeText(ctx, "还未下载完，请稍后~", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        tvImage.setImageResource(R.drawable.voice_to_icon);
                        animationDrawable = (AnimationDrawable) tvImage.getDrawable();
                        animationDrawable.start();
                        playMusic(entity.getMsg_content(), tvImage, 2);
                    }
                }
            });

        } else if (entity.getMsg_type() != null && entity.getMsg_type().equals("image")) {
            final String content = entity.getMsg_imageUrlId();
            viewHolder.tvText.setVisibility(View.GONE);
            viewHolder.tvImage.setVisibility(View.VISIBLE);
            viewHolder.tvTime.setText("");

            new File(android.os.Environment.getExternalStorageDirectory()
                    + "/emsg/receive/image/thumb/").mkdirs();
            new File(android.os.Environment.getExternalStorageDirectory()
                    + "/emsg/receive/image/original/").mkdirs();
            new File(android.os.Environment.getExternalStorageDirectory()
                    + "/emsg/send/image/thumb/").mkdirs();
            new File(android.os.Environment.getExternalStorageDirectory()
                    + "/emsg/send/image/original/").mkdirs();

            if (isComMsg) {
                String filename = entity.getMsg_content();
                Bitmap bMap = ImageBase64.stringtoBitmap(filename);
                viewHolder.tvImage.setImageBitmap(ThumbExtractor.extractMiniThumb(bMap, 200, 200));
            } else {
                String filename = entity.getMsg_content();
                Bitmap bMap = ImageBase64.stringtoBitmap(filename);
                viewHolder.tvImage.setImageBitmap(ThumbExtractor.extractMiniThumb(bMap, 200, 200));
            }

//            viewHolder.tvImage.setOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    String photo = entity.getMsg_imageUrlId();
//                    if (!TextUtils.isEmpty(photo)) {
//                        Intent intent = new Intent(ctx, ImagePagerActivity.class);
//                        String[] s = {photo};
//                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, s);
//                        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
//                        ctx.startActivity(intent);
//                    }
//                }
//            });
        }
//        else if (entity.getMsg_type() != null && entity.getMsg_type().equals("geo")) {
//            if (isComMsg) {
//                viewHolder.tvText.setVisibility(View.GONE);
//                viewHolder.tvImage.setVisibility(View.VISIBLE);
//                viewHolder.tvTime.setText("");
//                viewHolder.tvImage.setImageResource(R.drawable.location_msg);
//            } else {
//                viewHolder.tvText.setVisibility(View.GONE);
//                viewHolder.tvImage.setVisibility(View.VISIBLE);
//                viewHolder.tvTime.setText("");
//                viewHolder.tvImage.setImageResource(R.drawable.location_msg);
//            }
//
//            viewHolder.tvImage.setOnClickListener(new OnClickListener() {
//                public void onClick(View v) {
//                    String lat = entity.getMsg_GeoLat();
//                    String lng = entity.getMsg_GeoLng();
//                    if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
//                        Intent Mintent = new Intent(ctx, EmsgLocationActivity
//                                .class);
//                        if (lat != null && lng != null) {
//                            Mintent.putExtra("groundlat", lat);
//                            Mintent.putExtra("groundlng", lng);
//
//                            ctx.startActivity(Mintent);
//                        }
//                    }
//                }
//            });
//        }
        else {
            Spannable span = SmileUtils
                    .getSmiledText(ctx, entity.getMsg_content());
            // 设置内容
            viewHolder.tvText.setText(span, TextView.BufferType.SPANNABLE);
//            viewHolder.tvText.setText(entity.getMsg_content());
            viewHolder.tvText.setVisibility(View.VISIBLE);
            viewHolder.tvImage.setVisibility(View.GONE);
            viewHolder.tvText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            viewHolder.tvTime.setText("");
        }

        if (isComMsg) {
            viewHolder.tvUserName.setText(entity.getNickname());
        } else {
            viewHolder.tvUserName.setText(entity.getMynickname());
        }

        return convertView;
    }


    static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvText;
        public ImageView tvImage;
        public RoundAngleImageView iv_userhead;
        public TextView tvTime;
        public LinearLayout row_rec_location;
        public LinearLayout tv_chatcontent;
        public TextView tv_location;
        public boolean isComMsg = true;
//        View mLlNotify;
//        View mRlView;
//        TextView mTvTitle;
//        TextView mTvDate;
//        TextView mTvName;
    }

    /**
     * @param name
     * @Description
     */
    private void playMusic(String name, final ImageView imageView, final int flag) {
        try {
            File file = new File(name);
            if (file.length() > 0) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(file.getAbsolutePath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (flag == 1) {
                            animationDrawable.stop();
                            imageView.setImageResource(R.drawable.chatfrom_voice_playing);
                            ;
                        } else if (flag == 2) {
                            animationDrawable.stop();
                            imageView.setImageResource(R.drawable.chatto_voice_playing);
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String getKeyFroComingMsg(String content) {
        return content.substring(content.lastIndexOf("/"), content.length());
    }

    private void stop() {

    }

    class AudioTask extends AsyncTask<String, Void, Boolean> {
        String filename;

        public AudioTask(String filename) {
            this.filename = filename;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String url = params[0];
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response = client.execute(get);
                InputStream is = response.getEntity().getContent();
                FileOutputStream fos = new FileOutputStream(filename);
                int ch = 0;
                while ((ch = is.read()) != -1) {
                    fos.write(ch);
                }
                fos.close();
                completeflag = "complete";
                return true;
            } catch (Exception ex) {
                Log.e(TAG, "发送异常." + ex.getMessage(), ex);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            super.onPostExecute(result);
        }
    }
}
