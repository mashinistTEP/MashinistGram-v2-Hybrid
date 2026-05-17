package app.mashinistgram.messenger.hybrid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.telegram.ui.ActionBar.BaseFragment;

public class CacheFragment extends BaseFragment {
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText("Cache - скоро будет!");
        textView.setTextSize(20);
        return textView;
    }
}
