package hu.bme.aut.listapp.list;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import hu.bme.aut.listapp.R;
import hu.bme.aut.listapp.list.model.ItemList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddNewListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModifyListFragment extends DialogFragment {
    // Log tag
    public static final String TAG = "ItemListModifyFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.listNameInput)
    TextInputEditText listNameInput;
    @BindView(R.id.saveListBtn)
    Button saveListBtn;
    @BindView(R.id.cancelBtn)
    Button cancelBtn;
    Unbinder unbinder;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener interactionListener;

    public ModifyListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewListFragment.
     */
    public static ModifyListFragment newInstance(String param1, String param2) {
        ModifyListFragment fragment = new ModifyListFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_new_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (interactionListener != null) {
            interactionListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.saveListBtn, R.id.cancelBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.saveListBtn:
                ItemListActivity activity = (ItemListActivity)getActivity();

                ItemList itemlist = activity.getItemListObject();

                String text = "" + listNameInput.getText().toString();
                itemlist.setName(text);
                itemlist.refreshLastModifiedDate();
                itemlist.save();

                interactionListener.listModified();
                this.dismiss();
                break;
            case R.id.cancelBtn:
                this.dismiss();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void listModified();
    }
}
