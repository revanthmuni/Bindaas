package com.tachyon.bindaas;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tachyon.bindaas.Inbox.MessagesTab;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Notifications.NotificationTab;
import com.tachyon.bindaas.SimpleClasses.Functions;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabsView extends RootFragment {

    View view;
    Context context;
    TextView notification_view, messages_view;
    FrameLayout frameLayout;
    public TabsView() {
        // Required empty public constructor
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try{
            getActivity().setTheme(Functions.getSavedTheme());
            view = inflater.inflate(R.layout.fragment_tabs_view, container, false);

        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
        try {
            context = getContext();

            frameLayout = view.findViewById(R.id.tabs_container);
            notification_view = view.findViewById(R.id.textView3);
            messages_view = view.findViewById(R.id.textView4);
            loadNoficationTab();
            notification_view.setOnClickListener(view -> {
                frameLayout.setVisibility(View.GONE);

                /*  messages_view.setBackground(getResources().getDrawable(R.color.white));
                messages_view.setAlpha(0.5f);*/

                notification_view.setBackground(getResources().getDrawable(R.color.themecolor));
                notification_view.setAlpha(1f);
                if (Functions.getSavedTheme() == R.style.WhiteTheme){
                    messages_view.setBackground(getResources().getDrawable(R.color.white));
                    messages_view.setAlpha(0.5f);
                }else{
                    messages_view.setBackground(getResources().getDrawable(R.color.blackPrimaryColor));
                    messages_view.setTextColor(getResources().getColor(R.color.white));
                }
                loadNoficationTab();
            });
            messages_view.setOnClickListener(view -> {
                messages_view.setBackground(getResources().getDrawable(R.color.themecolor));
                messages_view.setAlpha(1f);
                /*notification_view.setBackground(getResources().getDrawable(R.color.white));
                notification_view.setAlpha(0.5f);*/
                if (Functions.getSavedTheme() == R.style.WhiteTheme){
                    notification_view.setBackground(getResources().getDrawable(R.color.white));
                    notification_view.setAlpha(0.5f);
                }else{
                    notification_view.setBackground(getResources().getDrawable(R.color.blackPrimaryColor));
                    notification_view.setTextColor(getResources().getColor(R.color.white));
                }                loadMessageTab();
            });
            view.findViewById(R.id.back_btn2).setOnClickListener(v -> {
                EventBus.getDefault().post("done");
                getActivity().onBackPressed();
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return view;
    }

    private void loadMessageTab() {
        try {
            MessagesTab fragment = new MessagesTab();
//            Notification_F notification_F = new Notification_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
//            transaction.addToBackStack(null);
            transaction.replace(R.id.tabs_container, fragment).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void loadNoficationTab() {

        try {
            NotificationTab fragment = new NotificationTab();
//            Notification_F notification_F = new Notification_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            transaction.replace(R.id.tabs_container, fragment).commit();
            transaction.addToBackStack(null);
            frameLayout.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

}
