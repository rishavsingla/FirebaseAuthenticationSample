package pcvark.com.firebasedemo.util;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

public class Util {

    public static boolean checkEmail(String email) {
        return !(email == null || TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public static void showSnackBar(@NonNull View view, String snackText) {
        Snackbar snackbar = Snackbar.make(view, snackText, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
