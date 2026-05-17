package app.mashinistgram.messenger.hybrid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.telegram.ui.ActionBar.BaseFragment;

public class PremiumFragment extends BaseFragment {
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText("Premium - скоро будет!");
        textView.setTextSize(20);
        return textView;
    }
}
