package fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.letsnurture.firebaseanonymousauthentication.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Suraj Makhija on 7/7/16.
 */

public class LinkAccountDialogFragment extends DialogFragment {

    CloseDialogInterface closeDialogInterface;
    View v;

    @OnClick({R.id.buttonEmail, R.id.buttonGoogle, R.id.buttonFacebook})
    public void chooseAccount(View view) {
        if (closeDialogInterface != null) {
            closeDialogInterface.onCloseDialogListener(view);
        }
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_linkaccountdialog, container, false);
        ButterKnife.bind(this, v);
        getDialog().setTitle("Choose Account to link with");
        return v;
    }

    public interface CloseDialogInterface {
        void onCloseDialogListener(View view);
    }

    public void setCloseDialogInterface(CloseDialogInterface closeDialogInterface) {
        this.closeDialogInterface = closeDialogInterface;
    }
}
