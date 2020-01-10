package teacherkothai.example.com.teacherkothai;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        loginBtn = (Button) findViewById(R.id.start_login);
        signupBtn = (Button) findViewById(R.id.start_signup);

        onLoginButtonClicked();
        onSignButtonClicked();
    }

    private void onSignButtonClicked() {

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(intent);


            }
        });

    }

    private void onLoginButtonClicked() {
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,SignUpActivity.class);
                startActivity(intent);

            }
        });

    }
}
