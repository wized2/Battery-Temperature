package com.alpha.battery;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainActivity.kt */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\b\u0010\u000e\u001a\u00020\u000bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lcom/alpha/battery/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "tempTextView", "Landroid/widget/TextView;", "refreshButton", "Landroid/widget/Button;", "batteryImage", "Landroid/widget/ImageView;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "showBatteryTemp", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
/* loaded from: /tmp/bt-dex/classes3.dex */
public final class MainActivity extends AppCompatActivity {
    private ImageView batteryImage;
    private Button refreshButton;
    private TextView tempTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tempTextView = (TextView) findViewById(R.id.tempTextView);
        this.refreshButton = (Button) findViewById(R.id.refreshButton);
        this.batteryImage = (ImageView) findViewById(R.id.batteryImage);
        showBatteryTemp();
        Button button = this.refreshButton;
        if (button == null) {
            Intrinsics.throwUninitializedPropertyAccessException("refreshButton");
            button = null;
        }
        button.setOnClickListener(new View.OnClickListener() { // from class: com.alpha.battery.MainActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.showBatteryTemp();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void showBatteryTemp() {
        TextView textView = null;
        Intent batteryStatusIntent = registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (batteryStatusIntent != null) {
            double intExtra = batteryStatusIntent.getIntExtra("temperature", 0) / 10.0d;
            TextView textView2 = this.tempTextView;
            if (textView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("tempTextView");
            } else {
                textView = textView2;
            }
            textView.setText("Battery Temp: " + intExtra + " °C");
        }
    }
}
