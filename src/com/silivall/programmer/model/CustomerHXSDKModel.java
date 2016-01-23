package com.silivall.programmer.model;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.easemob.applib.model.HXSDKModel;
import com.easemob.applib.utils.HXPreferenceUtils;
import com.silivall.programmer.proenum.EasemobEnumKey;
import com.silivall.programmer.statis.ApplicationSystemStatis;

public class CustomerHXSDKModel extends HXSDKModel {

	private static final String PREF_USERNAME = "username";
    private static final String PREF_PWD = "pwd";
    protected Context context = null;
    protected Map<EasemobEnumKey,Object> valueCache = new HashMap<EasemobEnumKey,Object>();
    
    public CustomerHXSDKModel(Context ctx){
        context = ctx;
        HXPreferenceUtils.init(context);
    }
    
    @Override
    public void setSettingMsgNotification(boolean paramBoolean) {
        HXPreferenceUtils.getInstance().setSettingMsgNotification(paramBoolean);
        valueCache.put(EasemobEnumKey.VibrateAndPlayToneOn, paramBoolean);
    }

    @Override
    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(EasemobEnumKey.VibrateAndPlayToneOn);

        if(val == null){
            val = HXPreferenceUtils.getInstance().getSettingMsgNotification();
            valueCache.put(EasemobEnumKey.VibrateAndPlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    @Override
    public void setSettingMsgSound(boolean paramBoolean) {
        HXPreferenceUtils.getInstance().setSettingMsgSound(paramBoolean);
        valueCache.put(EasemobEnumKey.PlayToneOn, paramBoolean);
    }

    @Override
    public boolean getSettingMsgSound() {
        Object val = valueCache.get(EasemobEnumKey.PlayToneOn);

        if(val == null){
            val = HXPreferenceUtils.getInstance().getSettingMsgSound();
            valueCache.put(EasemobEnumKey.PlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    @Override
    public void setSettingMsgVibrate(boolean paramBoolean) {
        HXPreferenceUtils.getInstance().setSettingMsgVibrate(paramBoolean);
        valueCache.put(EasemobEnumKey.VibrateOn, paramBoolean);
    }

    @Override
    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(EasemobEnumKey.VibrateOn);

        if(val == null){
            val = HXPreferenceUtils.getInstance().getSettingMsgVibrate();
            valueCache.put(EasemobEnumKey.VibrateOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    @Override
    public void setSettingMsgSpeaker(boolean paramBoolean) {
        HXPreferenceUtils.getInstance().setSettingMsgSpeaker(paramBoolean);
        valueCache.put(EasemobEnumKey.SpakerOn, paramBoolean);
    }

    @Override
    public boolean getSettingMsgSpeaker() {        
        Object val = valueCache.get(EasemobEnumKey.SpakerOn);

        if(val == null){
            val = HXPreferenceUtils.getInstance().getSettingMsgSpeaker();
            valueCache.put(EasemobEnumKey.SpakerOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    @Override
    public boolean getUseHXRoster() {
        return false;
    }

    @Override
    public boolean saveHXId(String hxId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_USERNAME, hxId).commit();
    }

    @Override
    public String getHXId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_USERNAME, null);
    }

    @Override
    public boolean savePassword(String pwd) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_PWD, pwd).commit();    
    }

    @Override
    public String getPwd() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_PWD, null);
    }

    @Override
    public String getAppProcessName() {
        return ApplicationSystemStatis.PACKAGE_NAME;
    }
    
    public void allowChatroomOwnerLeave(boolean value){
        HXPreferenceUtils.getInstance().setSettingAllowChatroomOwnerLeave(value);
    }
    
    public boolean isChatroomOwnerLeaveAllowed(){
        return HXPreferenceUtils.getInstance().getSettingAllowChatroomOwnerLeave();
    }
    
    public void setGroupsSynced(boolean synced){
        HXPreferenceUtils.getInstance().setGroupsSynced(synced);
    }
    
    public boolean isGroupsSynced(){
        return HXPreferenceUtils.getInstance().isGroupsSynced();
    }
    
    public void setContactSynced(boolean synced){
        HXPreferenceUtils.getInstance().setContactSynced(synced);
    }
    
    public boolean isContactSynced(){
        return HXPreferenceUtils.getInstance().isContactSynced();
    }
    
    public void setBlacklistSynced(boolean synced){
        HXPreferenceUtils.getInstance().setBlacklistSynced(synced);
    }
    
    public boolean isBacklistSynced(){
        return HXPreferenceUtils.getInstance().isBacklistSynced();
    }
    
}
