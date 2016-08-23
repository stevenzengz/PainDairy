package monash.fit5046.assign.assignmentpaindiary.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import monash.fit5046.assign.assignmentpaindiary.MainMenuActivity;
import monash.fit5046.assign.assignmentpaindiary.R;

/**
 * Created by Steven on 23/04/2016.
 */
public class MainMenuFragment extends Fragment {
    private View mainMenuView;
    private TextView tv_today;
    private TextView tv_username;
    private Button btn_record;
    MainMenuActivity mainMenuActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainMenuView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        mainMenuActivity = (MainMenuActivity) getActivity();
        initiateWidgets();

        tv_username.append(mainMenuActivity.loginUser.getUsername());
        tv_today.append(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordCreateFragment recordCreateFragment = new RecordCreateFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.mm_fragment, recordCreateFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return mainMenuView;
    }

    private void initiateWidgets() {
        tv_username = (TextView) mainMenuView.findViewById(R.id.mm_tv_username);
        tv_today = (TextView) mainMenuView.findViewById(R.id.mm_tv_today);
        btn_record = (Button) mainMenuView.findViewById(R.id.mm_btn_record);
    }

}
