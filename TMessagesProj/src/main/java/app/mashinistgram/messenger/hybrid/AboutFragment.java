package app.mashinistgram.messenger.hybrid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.telegram.ui.ActionBar.BaseFragment;

public class AboutFragment extends BaseFragment {
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Intent intent = new Intent(getContext(), AboutActivity.class);
        getContext().startActivity(intent);
        return null;
    }
}
