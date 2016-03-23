package com.vurtnewk.emsgdemo.utils;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * @author VurtneWk
 * @time created on 2016/3/16.16:24
 */
public class UIUitls {

    /**
     * @author VurtneWk
     * @time Created on 2016/3/16 16:47
     */
    public static void setCursorToEnd(EditText editText) {
        CharSequence text = editText.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    /**
     * @param editText
     * @param limit
     */
    public static void setEditTextLimit(final EditText editText, final int limit) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > limit) {
                    VToast.showLongToast(editText.getContext(), "您输入的字数已超出限制");
                    editText.setText(s.toString().substring(0, s.toString().length() - 1));
                    setCursorToEnd(editText);
                }
            }
        });
    }
}
