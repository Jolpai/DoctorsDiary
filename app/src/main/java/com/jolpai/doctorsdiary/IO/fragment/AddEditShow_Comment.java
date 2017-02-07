package com.jolpai.doctorsdiary.IO.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.jolpai.doctorsdiary.R;
import com.jolpai.doctorsdiary.Realm_Model.Comments;
import com.jolpai.doctorsdiary.Worker.Database.GetData;
import com.jolpai.doctorsdiary.Worker.Parse.IntParser;
import com.jolpai.doctorsdiary.Worker.MyDateFormat;
import com.jolpai.doctorsdiary.Worker.MyToast;
import com.jolpai.doctorsdiary.Worker.Adapter.RecyclerAdapter_Comment;
import com.jolpai.doctorsdiary.Worker.Database.SaveData;
import com.jolpai.doctorsdiary.Worker.Listener.Comment_Item_TouchListener;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddEditShow_Comment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddEditShow_Comment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEditShow_Comment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String YEAR = "YEAR";
    private static final String MONTH = "MONTH";
    private static final String DAY="DAY";
    private static String  commentStatus = "";
    private static String commentDate = MyDateFormat.getDateTimeNow();

    private int year;
    private int month;
    private int day;

    private ArrayList<Comments> commentList;

    Button btnSave;
    private EditText editName;
    private EditText editComment;

    private RecyclerView recyclerView_Comment;

    private OnFragmentInteractionListener mListener;

    public AddEditShow_Comment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param year Parameter 1. selected year by uers
     * @param month Parameter 2. selected month of @param year
     * @param day Parameter 3. selected day of @param month
     * @return A new instance of fragment AddEditShow_Comment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddEditShow_Comment newInstance(String year, String month, String day) {
        AddEditShow_Comment fragment = new AddEditShow_Comment();
        Bundle bundle = new Bundle();
        bundle.putString(YEAR, year);
        bundle.putString(MONTH, month);
        bundle.putString(DAY,day);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            year = IntParser.parseStrToInt(getArguments().getString(YEAR));
            month = IntParser.parseStrToInt(getArguments().getString(MONTH));
            day = IntParser.parseStrToInt(getArguments().getString(DAY));
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            view=inflater.inflate(R.layout.v21_fragment_comment, container, false);
        }

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        btnSave =(Button) toolbar.findViewById(R.id.btnSave);
        initialization(view);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comments comments=new Comments();
                String ss = MyDateFormat.getDateTimeNow();
                if(checkValidation()) {

                    if (commentStatus.equalsIgnoreCase("update")) {

                    }
                    comments.setDate(commentDate);
                    comments.setPersonsName(editName.getText().toString());
                    comments.setComment(editComment.getText().toString());
                    comments.setYear(year);
                    comments.setMonth(month);
                    comments.setDay(day);
                    comments.setUpdateDate(ss);
                    SaveData.saveDataToRealm(getContext(), comments, (Class) Comments.class);

                    showComment();
                    clear();
                }
            }
        });

        return view;
    }

    private boolean checkValidation(){
        String name =editName.getText().toString();
        String comment=editComment.getText().toString();
        View focuasableView;
        if(name.equalsIgnoreCase("")){
            focuasableView=editName;
            //focuasableView.setBackgroundColor(getActivity().getResources(R.color.amber_500));
            MyToast.toast_short("Please Enter Your Name.");
            return false;

        }else if(comment.equalsIgnoreCase("")){
            MyToast.toast_short("Please Enter Your Comment.");
            return false;
        }
        return true;
    }

    private void clear(){

        editName.setText("");
        editComment.setText("");
        btnSave.setText("SAVE");
        clearFocous();

    }

    private void clearFocous(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm =(InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    private void setFocous(){

        editComment.setFocusable(true);
        editName.setFocusable(true);
    }

    private void initialization(View view){

        editName = (EditText)view.findViewById(R.id.editName);
        editComment =(EditText)view.findViewById(R.id.editComment);
        recyclerView_Comment = (RecyclerView)view.findViewById(R.id.recyclerViewCommenters);
        //recyclerView_Comment.
        recyclerView_Comment.addOnItemTouchListener(
                new Comment_Item_TouchListener(getActivity(),recyclerView_Comment,new Comment_Item_TouchListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View view, int position) {
                        Comments cm =(Comments)view.getTag();
                        editName.setText(cm.getPersonsName());
                        editComment.setText(cm.getComment());

                        btnSave.setText("UPDATE");
                        commentDate=cm.getDate();
                       // setFocous();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

        showComment();

    }
    private void showComment(){

        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RealmResults<Comments> realmCommentList= GetData.getOneMonthCommentFromRealm(getActivity(),(Class)Comments.class,year,month,day);
                commentList  = new ArrayList<>();
                for (Comments comments : realmCommentList){

                    commentList.add(comments);
                }
                Collections.sort(commentList);
                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                recyclerView_Comment.setLayoutManager(horizontalLayoutManager);
                recyclerView_Comment.setAdapter(new RecyclerAdapter_Comment(commentList,getActivity()));

            }
        },300);



    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public View setParentLayout(){
        View view =null;

        return view;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
