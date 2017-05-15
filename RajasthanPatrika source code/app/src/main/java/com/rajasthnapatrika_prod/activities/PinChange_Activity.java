package com.rajasthnapatrika_prod.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.utils.AppUtills;

public class PinChange_Activity extends AppCompatActivity {

    private static final String TAG = "PinChange_Activity";
    EditText et_pin, et_new_pin, et_re_enter_new_pin;
    String flag = "";
    LinearLayout layout_new;
    Button btn_submit;
    String focused_et = "";
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_change_);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle(getResources().getString(R.string.add_news_txt));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setElevation(9f);

        et_pin = (EditText) findViewById(R.id.et_pin);
        layout_new = (LinearLayout) findViewById(R.id.layout_new);
        et_new_pin = (EditText) findViewById(R.id.et_new_pin);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        et_re_enter_new_pin = (EditText) findViewById(R.id.et_re_enter_new_pin);
        view = (View) findViewById(R.id.view);

        add_text_listener(et_pin);
        add_text_listener(et_new_pin);
        add_text_listener(et_re_enter_new_pin);
        focused_et = "et_pin";
        Log.e(TAG, "onCreate called");
        Log.e(TAG, "onCreate called");
        et_pin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                focused_et = "et_pin";
                return false;
            }
        });

        et_pin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                focused_et = "et_pin";
                return false;
            }
        });

        et_re_enter_new_pin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                focused_et = "et_re_enter_new_pin";
                return false;
            }
        });

        et_new_pin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                focused_et = "et_new_pin";
                return false;
            }
        });

        if (getIntent() != null) {
            if (getIntent().getStringExtra("flag").equals("first")) {
                flag = getIntent().getStringExtra("flag");
                mActionBar.setTitle("Set your pin");
                et_new_pin.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                et_pin.setHint(getResources().getString(R.string.hint_pin_name));
                et_re_enter_new_pin.setHint(getResources().getString(R.string.hint_again_pin_name));

            } else if (getIntent().getStringExtra("flag").equals("second")) {
                flag = getIntent().getStringExtra("flag");
                mActionBar.setTitle("Change your pin");
                et_new_pin.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                et_pin.setHint(getResources().getString(R.string.hint_old_pin_name));
                et_re_enter_new_pin.setHint(getResources().getString(R.string.hint_new_pin_name));
                et_new_pin.setHint(getResources().getString(R.string.hint_again_pin_name));

            }
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag.equals("first")) {
                    if (!et_pin.getText().toString().trim().equals("")) {
                        if (et_pin.getText().toString().trim().length() == 4) {
                            if (!et_re_enter_new_pin.getText().toString().trim().equals("")) {
                                if (et_pin.getText().toString().trim().equals(et_re_enter_new_pin.getText().toString().trim()))
                                    verifyPassword();
                                else {
                                    et_re_enter_new_pin.setError(getResources().getString(R.string.txt_pin_not_match));
                                    et_re_enter_new_pin.requestFocus();
                                }
                            } else {
                                et_re_enter_new_pin.setError(getResources().getString(R.string.hint_pin_name));
                                et_re_enter_new_pin.requestFocus();
                            }
                        } else {
                            et_pin.setError(getResources().getString(R.string.txt_pin_short));
                            et_pin.requestFocus();
                        }
                    } else {
                        et_pin.setError(getResources().getString(R.string.hint_pin_name));
                        et_pin.requestFocus();
                    }
                } else if (flag.equals("second")) {
                    if (!et_pin.getText().toString().trim().equals("")) {
                        if (!et_re_enter_new_pin.getText().toString().trim().equals("")) {
                            if (et_re_enter_new_pin.getText().toString().trim().length() == 4) {
                                if (!et_new_pin.getText().toString().trim().equals("")) {
                                    if (et_re_enter_new_pin.getText().toString().trim().equals(et_new_pin.getText().toString().trim())) {

                                        if (et_pin.getText().toString().trim().equals(AppController_Patrika.getSharedPreferences().getString("pin", ""))) {
                                            verifyPassword();
                                        } else {
                                            et_pin.setError(getResources().getString(R.string.txt_pin_not_match));
                                            et_pin.requestFocus();
                                        }
                                    } else {
                                        et_new_pin.setError(getResources().getString(R.string.txt_pin_not_match));
                                        et_new_pin.requestFocus();
                                    }
                                } else {
                                    et_new_pin.setError(getResources().getString(R.string.hint_pin_name));
                                    et_new_pin.requestFocus();
                                }
                            } else {
                                et_re_enter_new_pin.setError(getResources().getString(R.string.txt_pin_short));
                                et_re_enter_new_pin.requestFocus();
                            }
                        } else {
                            et_re_enter_new_pin.setError(getResources().getString(R.string.hint_pin_name));
                            et_re_enter_new_pin.requestFocus();
                        }
                    } else {
                        et_pin.setError(getResources().getString(R.string.hint_pin_name));
                        et_pin.requestFocus();
                    }
                }
            }
        });
    }

    void add_text_listener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.toString().length() > 0) {
                        if (s.length() == 4) {
                            if (flag.equals("first")) {
                                if (focused_et.equals("et_pin")) {
                                    et_pin.clearFocus();
                                    et_re_enter_new_pin.requestFocus();
                                    et_re_enter_new_pin.setCursorVisible(true);
                                    focused_et = "et_re_enter_new_pin";
                                } else if (focused_et.equals("et_re_enter_new_pin")) {
                                    if (!et_pin.getText().toString().trim().equals("")) {
                                        if (!et_re_enter_new_pin.getText().toString().trim().equals("")) {
                                            if (et_pin.getText().toString().trim().equals(et_re_enter_new_pin.getText().toString().trim()))
                                                verifyPassword();
                                            else {
                                                et_re_enter_new_pin.setError(getResources().getString(R.string.txt_pin_not_match));
                                                et_re_enter_new_pin.requestFocus();
                                            }
                                        } else {
                                            et_re_enter_new_pin.setError(getResources().getString(R.string.hint_pin_name));
                                            et_re_enter_new_pin.requestFocus();
                                        }
                                    }
                                }
                            } else if (flag.equals("second")) {
                                if (focused_et.equals("et_pin")) {
                                    et_pin.clearFocus();
                                    et_re_enter_new_pin.requestFocus();
                                    et_re_enter_new_pin.setCursorVisible(true);
                                    focused_et = "et_re_enter_new_pin";
                                } else if (focused_et.equals("et_re_enter_new_pin")) {
                                    et_re_enter_new_pin.clearFocus();
                                    et_new_pin.requestFocus();
                                    et_new_pin.setCursorVisible(true);
                                    focused_et = "et_new_pin";
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void verifyPassword() {
        final Dialog dialog = new Dialog(this, R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_layout_password);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        final EditText txt_password = (EditText) dialog.findViewById(R.id.txt_password);
        txt_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txt_password.setHint(getResources().getString(R.string.hint_password));
        final TextInputLayout input_layout_pass = (TextInputLayout) dialog.findViewById(R.id.input_layout_pass);
        txt_password.addTextChangedListener(AppUtills.removeTextWatcher(txt_password, input_layout_pass));
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.findViewById(R.id.btn_submit).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (txt_password.getText().toString().trim().length() > 0) {
                            if (AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("password", "").equals(txt_password.getText().toString().trim())) {
                                dialog.dismiss();
                                Log.v("pin is ", "pin is " + et_pin.getText().toString().trim());
                                if (flag.equals("first"))
                                    AppController_Patrika.getSharedPreferences().edit().putString("pin", et_pin.getText().toString().trim()).commit();
                                else if (flag.equals("second"))
                                    AppController_Patrika.getSharedPreferences().edit().putString("pin", et_new_pin.getText().toString().trim()).commit();

                                Log.v("pin is ", "pin is " + AppController_Patrika.getSharedPreferences().getString("pin", ""));
                                startActivity(new Intent(PinChange_Activity.this, Drawer_Activity.class));
                                finish();
                                if (flag.equals("first"))
                                    Toast.makeText(PinChange_Activity.this, getResources().getString(R.string.txt_pin_set), Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(PinChange_Activity.this, getResources().getString(R.string.txt_pin_changed), Toast.LENGTH_LONG).show();
                            } else {
                                input_layout_pass.setError(getResources().getString(R.string.hint_incorrect_password));
                            }
                        } else {
                            input_layout_pass.setError(getResources().getString(R.string.hint_password));
                        }
                    }
                });
        dialog.show();

        txt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() > 0) {
                        txt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        txt_password.setSelection(txt_password.getText().length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                if (flag.equals("first")) {
                    finish();
                    break;
                } else if (flag.equals("second")) {
                    finish();
                    break;
                }
            default:
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        if (flag.equals("first"))
            finish();
        else if (flag.equals("second")) {
            finish();
        }
    }
}
