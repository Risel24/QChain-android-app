package io.rsl.pragma.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rsl.pragma.App;
import io.rsl.pragma.R;
import io.rsl.pragma.activities.PersonalAreaActivity;
import io.rsl.pragma.api.QService;
import io.rsl.pragma.api.models.AuthResponse;
import io.rsl.pragma.api.models.SignupData;
import io.rsl.pragma.api.models.User;
import io.rsl.pragma.db.dbmodels.DBUser;
import io.rsl.pragma.repositories.UserRepository;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username_inputlayout) TextInputLayout usernameInput;
    @BindView(R.id.password_inputlayout) TextInputLayout passwordInput;

    @Inject
    QService qService;

    @Inject
    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        App.get(this).getComponent().inject(this);
        if(userRepository.hasUser()) {
            Intent intent = new Intent(LoginActivity.this, PersonalAreaActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        ButterKnife.bind(this);
    }

    @OnClick(R.id.loginButton)
    void login(View view) {
        auth(toString(usernameInput), toString(passwordInput));
    }

    @OnClick(R.id.registerButton)
    void register(View view) {
        showRegisterDialog();
    }

    private void showRegisterDialog() {

        View signupView = getLayoutInflater().inflate(R.layout.layout_signup, null, false);

        TextInputLayout email = signupView.findViewById(R.id.email_inputlayout);
        TextInputLayout username = signupView.findViewById(R.id.username_inputlayout);
        TextInputLayout name = signupView.findViewById(R.id.name_inputlayout);
        TextInputLayout pass = signupView.findViewById(R.id.pass_inputlayout);
        TextInputLayout passConf = signupView.findViewById(R.id.passrepeat_inputlayout);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(signupView)
                .setPositiveButton("Продолжить", null)
                .setNegativeButton("Отмена", (dialog12, which) -> dialog12.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                email.setErrorEnabled(false);
                username.setErrorEnabled(false);
                name.setErrorEnabled(false);
                pass.setErrorEnabled(false);
                passConf.setErrorEnabled(false);

                boolean correct = true;
                if(toString(pass).length() < 4) {
                    pass.setErrorEnabled(true);
                    pass.setError("Слишком короткий пароль");
                }
                if(!Objects.equals(toString(pass), toString(passConf))) {
                    passConf.setErrorEnabled(true);
                    passConf.setError("Пароли не совпадают");
                    correct = false;
                }
                if (!pattern.matcher(toString(email)).matches()) {
                    email.setErrorEnabled(true);
                    email.setError("Некорректный email");
                    correct = false;
                }
                if (toString(username).length() < 4) {
                    username.setErrorEnabled(true);
                    username.setError("Слишком короткое имя пользователя");
                    correct = false;
                }
                if (toString(name).length() < 4) {
                    name.setErrorEnabled(true);
                    name.setError("Слишком короткое имя");
                    correct = false;
                }
                if(correct) {
                    signup(toString(email), toString(username), toString(name), toString(pass));
                }
            });
        });

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.colorPrimary);
        dialog.show();
    }

    private void auth(String login, String pass) {
        qService.auth(new User(login, pass)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                AuthResponse authResponse = response.body();
                if(response.isSuccessful() && authResponse != null) {
                    userRepository.saveUser(new DBUser(authResponse.getUserId(), authResponse.getToken()));
                    Intent intent = new Intent(LoginActivity.this, PersonalAreaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else
                    Snackbar.make(findViewById(android.R.id.content), "Ошибка авторизации. Проверьте введённые данные.", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Ошибка соединения. Попробуйте позже.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void signup(String email, String username, String name, String pass) {
        qService.signup(new SignupData(email, username, name, pass)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                if(response.isSuccessful() && responseBody != null) {
                    auth(email, username);
                } else
                    Snackbar.make(findViewById(android.R.id.content), "Ошибка регистрации. Проверьте введённые данные.", Snackbar.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Ошибка соединения. Попробуйте позже.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private String toString(TextInputLayout textInputLayout) {
        return textInputLayout.getEditText().getText().toString();
    }
}

