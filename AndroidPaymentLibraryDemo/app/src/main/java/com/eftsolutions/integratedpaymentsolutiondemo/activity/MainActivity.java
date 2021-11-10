package com.eftsolutions.integratedpaymentsolutiondemo.activity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ViewUtils;
import com.eftsolutions.integratedpaymentsolution.payment.PaymentResult;
import com.eftsolutions.integratedpaymentsolution.payment.PaymentResultCallback;
import com.eftsolutions.integratedpaymentsolution.payment.SpiralPayment;
import com.eftsolutions.integratedpaymentsolutiondemo.R;
import com.eftsolutions.integratedpaymentsolutiondemo.databinding.ActivityMainBinding;
import com.eftsolutions.integratedpaymentsolutiondemo.dialog.LoadingDialog;
import com.eftsolutions.integratedpaymentsolutiondemo.payment.PaymentMethod;
import com.eftsolutions.integratedpaymentsolutiondemo.util.SessionIdUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String URL_API_UAT = "https://b8jhphintc.execute-api.ap-east-1.amazonaws.com/v1/sessions/";
    private static final String URL_API_PRODUCTION = "https://api-checkout.spiralplatform.com/v1";

    private PaymentMethod paymentMethod;

    private ActivityMainBinding binding;
    private Spinner spPaymentMethod;
    private Button btnPay;
    private EditText etAmount;
    private TextView tvResult;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spPaymentMethod = binding.spPaymentMethod;
        btnPay = binding.btnPay;
        etAmount = binding.etAmount;
        tvResult = binding.tvResult;

        loadingDialog = new LoadingDialog(MainActivity.this);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.name_payment_method, R.layout.item_spinner);
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner);
        spPaymentMethod.setAdapter(arrayAdapter);
        spPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                paymentMethod = PaymentMethod.getByMethod(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ClickUtils.applyGlobalDebouncing(btnPay, 200, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();

                clearResult();

                if (ObjectUtils.isEmpty(etAmount.getText().toString())) {
                    setResult("Amount cannot be empty");
                } else {
                    SessionIdUtils.getSessionID(paymentMethod, etAmount.getText().toString(), new SessionIdUtils.ResultCallback() {
                        @Override
                        public void onSuccess(String sessionID) {
                            pay(sessionID);
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            setResult(errMsg);
                        }
                    });
                }
            }
        });

        tvResult.setMovementMethod(new ScrollingMovementMethod());
    }

    private void pay(String sessionId) {
        switch (paymentMethod) {
            case ALIPAY_HK:
            case ALIPAY_CN:
//                com.alipay.sdk.interior.Log.setupLogCallback(new com.alipay.sdk.interior.Log.ISdkLogCallback() {
//                    @Override
//                    public void onLogLine(String s) {
//                        Log.w("AlipaySDKLog", s);
//                    }
//                });
            case MASTERCARD:
            case FPS:
            case OCTOPUS:
                SpiralPayment payment = new SpiralPayment(MainActivity.this, URL_API_PRODUCTION);
                payment.pay(sessionId, new PaymentResultCallback() {
                    @Override
                    public void onFinish(PaymentResult paymentResult) {
                        setResult(paymentResult);
                    }
                });
                break;
            default:
                setResult("Not support");
                break;
        }
    }

    private void showLoadingDialog() {
        ViewUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show();
            }
        });
    }

    private void dismissLoadingDialog() {
        ViewUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
            }
        });
    }

    private void setResult(String result) {
        ViewUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.setText("Result:" + result);
                loadingDialog.dismiss();
            }
        });
    }

    private void setResult(PaymentResult paymentResult) {
        ViewUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String paymentStatusStr;
                switch (paymentResult.getPaymentStatus()) {
                    case APPROVED:
                        paymentStatusStr = "APPROVED";
                        break;
                    case CANCELLED:
                        paymentStatusStr = "CANCELLED";
                        break;
                    case DECLINED:
                        paymentStatusStr = "DECLINED";
                        break;
                    case UNCONFIRMED:
                        paymentStatusStr = "UNCONFIRMED";
                        break;
                    case QUERY_FAILED:
                        paymentStatusStr = "QUERY_FAILED";
                        break;
                    case APP_NOT_INSTALLED:
                        paymentStatusStr = "APP_NOT_INSTALLED";
                        break;
                    default:
                        paymentStatusStr = "";
                        break;
                }
                tvResult.setText(String.format("Session ID:%s\nPayment Status:%s", paymentResult.getSessionId(), paymentStatusStr));
                loadingDialog.dismiss();
            }
        });
    }

    private void clearResult() {
        ViewUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.setText("");
            }
        });
    }
}