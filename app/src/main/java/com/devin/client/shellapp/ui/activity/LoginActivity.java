package com.devin.client.shellapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devin.client.shellapp.R;
import com.devin.client.shellapp.context.ApplicationContext;
import com.devin.client.shellapp.model.UserBaseInfo;
import com.devin.client.shellapp.ui.fragment.UserInfoFragment;
import com.devin.client.shellapp.utils.HttpResponeCallBack;
import com.devin.client.shellapp.utils.RequestApiData;
import com.devin.client.shellapp.utils.UserPreference;
import com.devin.client.shellapp.utils.ValidateUserInfo;
import com.devin.client.shellapp.utils.constant.Constants;
import com.devin.client.shellapp.utils.constant.KeyConstance;
import com.devin.client.shellapp.utils.constant.UrlConstance;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements HttpResponeCallBack {

    @Bind(R.id.txt_cancel)
    TextView mTxtCancel;
    @Bind(R.id.txt_email)
    AutoCompleteTextView mTxtEmail;
    @Bind(R.id.txt_password)
    EditText mTxtPassword;
    @Bind(R.id.txt_forgot)
    TextView mTxtForgot;
    @Bind(R.id.txt_create)
    TextView mTxtCreate;
    @Bind(R.id.email_sign_in_button)
    Button mEmailSignInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
    * 初始化数据
    * */
    private void initView() {
        //点击“注册”
        mTxtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //点击“忘记密码”
        mTxtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //点击“取消”
        mTxtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //点击登录按钮
        mEmailSignInButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLogin();
            }
        });
    }

    private void clickLogin() {
        String email = mTxtEmail.getText().toString();//邮箱
        String password = mTxtPassword.getText().toString();//密码
        if (checkInput(email,password)){
           //输入信息不匹配，登陆失败
            Toast.makeText(this, "邮箱或密码错误", Toast.LENGTH_SHORT).show();
        }else {
            //请求服务器获取数据登录账号
            RequestApiData.getInstance().getLoginData(email, password, UserBaseInfo.class, LoginActivity.this);
        }
    }

    /**
     * 检查输入
     * @param email
     * @param password
     * @return
     */
    private boolean checkInput(String email, String password) {
        //账号为空时提示
        if (email==null||email.trim().equals("")){
            //Toast.makeText(this, R.string.tip_account_empty, Toast.LENGTH_LONG).show();
        }else {
            //账号不匹配邮箱格式
            if (!ValidateUserInfo.isEmailValid(email)){
                //Toast.makeText(this, R.string.tip_account_regex_not_right, Toast.LENGTH_LONG).show();
            }else if (password==null||password.trim().equals("")){
               // Toast.makeText(this, R.string.tip_password_can_not_be_empty, Toast.LENGTH_SHORT).show();
            }else {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onResponeStart(String apiName) {
        if (UrlConstance.KEY_LOGIN_INFO.equals(apiName)) {
            Toast.makeText(this, "正在加载数据中...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoading(String apiName, long count, long current) {
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(String apiName, Object object) {
        if (UrlConstance.KEY_LOGIN_INFO.equals(apiName)) {
            //邮箱登录返回数据
            if (object != null && object instanceof UserBaseInfo) {
                UserBaseInfo info = (UserBaseInfo) object;
                if (info.getRet().equals(Constants.KEY_SUCCESS)) {
                    //登录成功，保存登录信息
                    ApplicationContext.getInstance().setBaseUser(info);//保存到Application中
                    //保存到SP中
                    UserPreference.save(KeyConstance.IS_USER_ID, info.getUserid());
                    UserPreference.save(KeyConstance.IS_USER_EMAIL, info.getEmail());
                    UserPreference.save(KeyConstance.IS_USER_ACCOUNT, info.getName());
                    UserPreference.save(KeyConstance.IS_USER_PASSWORD, mTxtPassword.getText().toString());

                    Intent intent = new Intent(LoginActivity.this, UserInfoFragment.class);
                    startActivity(intent);
                    //覆盖过渡
                    overridePendingTransition(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
                    finish();
                } else {
                    Log.e("TAG", "info=" + info.toString());
                    if (info.getErrcode().equals(Constants.KEY_NO_REGIST)) {
                        Toast.makeText(this, "此邮箱未注册！登录失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, info.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "info.getMsg()=" + info.getMsg());
                    }
                }
            }
        }
    }

    @Override
    public void onFailure(String apiName, Throwable throwable, int errorNo, String strMsg) {
        Toast.makeText(this, "Failure...", Toast.LENGTH_SHORT).show();
    }
}