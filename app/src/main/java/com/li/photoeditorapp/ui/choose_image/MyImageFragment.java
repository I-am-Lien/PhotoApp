package com.li.photoeditorapp.ui.choose_image;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.li.photoeditorapp.R;
import com.li.photoeditorapp.base.BaseFragment;
import com.li.photoeditorapp.common.Constanst;
import com.li.photoeditorapp.data.base.ImgEditedDatabase;
import com.li.photoeditorapp.data.base.entity.ImageEdited;
import com.li.photoeditorapp.databinding.FragmentMyImageBinding;
import com.li.photoeditorapp.ui.ShowImageActivity;
import com.li.photoeditorapp.ui.choose_image.adapter.MyImageAdapter;
import com.li.photoeditorapp.ui.choose_image.call_back.OnItemMyImageClickListener;
import com.li.photoeditorapp.ui.choose_image.call_back.UpdateMyImageListener;

import java.util.ArrayList;
import java.util.List;

public class MyImageFragment extends BaseFragment<FragmentMyImageBinding> implements OnItemMyImageClickListener, UpdateMyImageListener {
    private List<ImageEdited> imageEditedList;
    private MyImageAdapter myImageAdapter;
    private GetImageTask getImageTask;


    @Override
    protected int getFragmentId() {
        return R.layout.fragment_my_image;
    }

    @Override
    protected void onViewReady(View view) {
        getImageTask = new GetImageTask();
        getImageTask.execute();
    }

    @Override
    public void onMyImageClick(int potision) {
        Intent intent = new Intent(getActivity(), ShowImageActivity.class);
        intent.putExtra(Constanst.KEY_IMAGE,imageEditedList.get(potision).getImageData());
        startActivity(intent);
    }

    @Override
    public void fragmentBecameVisible() {
        getImageTask = new GetImageTask();
        getImageTask.execute();
    }

    public class GetImageTask extends AsyncTask<Void,Void, List<ImageEdited>> {
        @Override
        protected List<ImageEdited> doInBackground(Void... voids) {
            List<ImageEdited> myImages = new ArrayList<>();
            myImages = ImgEditedDatabase.getInstance(getActivity().getApplicationContext()).getImageDao().getAll();
            return myImages;
        }

        @Override
        protected void onPostExecute(List<ImageEdited> imageEditeds) {
            super.onPostExecute(imageEditeds);
            imageEditedList = new ArrayList<>();
            imageEditedList.addAll(imageEditeds);
            myImageAdapter = new MyImageAdapter(imageEditedList,getLayoutInflater(),MyImageFragment.this::onMyImageClick );
            dataBinding.rvMyImage.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
            dataBinding.rvMyImage.setLayoutManager(layoutManager);
            dataBinding.rvMyImage.setAdapter(myImageAdapter);
            myImageAdapter.setImageEditedList(imageEditedList);

        }
    }

}
