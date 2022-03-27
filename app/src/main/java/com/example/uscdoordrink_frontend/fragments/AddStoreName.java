package com.example.uscdoordrink_frontend.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.AddStoreActivity;
import com.example.uscdoordrink_frontend.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddStoreName#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddStoreName extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddStoreName() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddStoreName.
     */
    // TODO: Rename and change types and number of parameters
    public static AddStoreName newInstance(String param1, String param2) {
        AddStoreName fragment = new AddStoreName();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_add_store_name, container, false);

        EditText textName = (EditText) mainView.findViewById(R.id.editTextStoreName);
        String defaultName = textName.getText().toString();

        Button confirmButton = (Button)mainView.findViewById(R.id.button_confirm_name_and_address);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.action_name_to_menu);
                Log.d("AddstoreName", textName.getText().toString());
            }
        });
        return mainView;
    }
}