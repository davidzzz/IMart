package com.imart.shop.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Alessandro on 09/01/2016.
 */
public class FacebookSign {

    private FragmentActivity mActivity;
    private CallbackManager mCallbackManager;
    private InfoLoginFaceCallback mFaceCallback;

    public FacebookSign(FragmentActivity mActivity, InfoLoginFaceCallback mFaceCallback) {
        FacebookSdk.sdkInitialize(mActivity.getApplicationContext());
        this.mActivity = mActivity;
        this.mFaceCallback = mFaceCallback;
        mCallbackManager = CallbackManager.Factory.create();
        if (isLoggedIn()) {
            LoginManager.getInstance().logOut();
        }
    }

    public void signInWithFaceButton(LoginButton loginButton){
        List<String> permissionNeeds= Arrays.asList("email", "public_profile");
        loginButton.setReadPermissions(permissionNeeds);
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("TAG", "JSON: " + object);
                                try {
                                    String id = object.getString("id");
                                    String foto = "https://graph.facebook.com/" + id + "/picture?height=120&width=120";
                                    String nome = object.getString("name");
                                    String email = object.getString("email");
                                    if (mFaceCallback != null){
                                        mFaceCallback.getInfoFace(id,nome,email,foto);
                                    }else{
                                        throw new IllegalArgumentException("interface InfoLoginFaceCallback is null");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                if (mFaceCallback != null){
                    mFaceCallback.cancelLoginFace();
                }else{
                    throw new IllegalArgumentException("interface InfoLoginFaceCallback is null");
                }
            }

            @Override
            public void onError(FacebookException error) {
                if (mFaceCallback != null){
                    mFaceCallback.errorLoginFace(error);
                }else {
                    throw new IllegalArgumentException("interface InfoLoginFaceCallback is null");
                }
            }
        });
    }

    public void signIn(){
        List<String> permissionNeeds= Arrays.asList("email", "public_profile");
        LoginManager.getInstance().logInWithReadPermissions(
                mActivity,
                permissionNeeds);
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResults) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResults.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        Log.v("TAG", "JSON: " + object);
                                        try {
                                            String id = object.getString("id");
                                            String foto = "https://graph.facebook.com/" + id + "/picture?height=120&width=120";
                                            String nome = object.getString("name");
                                            String email = object.getString("email");
                                            if (mFaceCallback != null){
                                                mFaceCallback.getInfoFace(id,nome,email,foto);
                                            }else{
                                                throw new IllegalArgumentException("interface InfoLoginFaceCallback is null");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        if (mFaceCallback != null){
                            mFaceCallback.cancelLoginFace();
                        }else{
                            throw new IllegalArgumentException("interface InfoLoginFaceCallback is null");
                        }
                    }

                    @Override
                    public void onError(FacebookException e) {
                        if (mFaceCallback != null){
                            mFaceCallback.errorLoginFace(e);
                        }else {
                            throw new IllegalArgumentException("interface InfoLoginFaceCallback is null");
                        }
                    }
                });
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void resultFaceLogin(int requestCode, int resultCode, Intent data){
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public interface InfoLoginFaceCallback {
        void getInfoFace(String id, String name, String email, String foto);
        void cancelLoginFace();
        void errorLoginFace(FacebookException e);
    }

}
