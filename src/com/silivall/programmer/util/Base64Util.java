package com.silivall.programmer.util;

import java.io.UnsupportedEncodingException;

import com.silivall.programmer.statis.ApplicationSystemStatis;

import android.util.Base64;

public class Base64Util {

	public static String decode(String str){
    	try{
    		return new String(Base64.decode(str, Base64.DEFAULT), ApplicationSystemStatis.CHARSET);
    	} catch (UnsupportedEncodingException e) {         
            throw new AssertionError(e);
        }
    }
}
